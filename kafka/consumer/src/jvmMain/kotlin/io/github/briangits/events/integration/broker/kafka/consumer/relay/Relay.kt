package io.github.briangits.events.integration.broker.kafka.consumer.relay

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import java.util.Properties
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

private typealias Subscriptions = ConcurrentHashMap<String, CopyOnWriteArrayList<Channel<Delivery>>>

internal actual fun Relay(config: RelayConfig): Relay = object : Relay {
    @Volatile var closed: Boolean = false
    @Volatile var started: Boolean = false

    val dispatcher = config.dispatcher ?: Dispatchers.IO
    val kafkaDispatcher = dispatcher.limitedParallelism(1)
    val processingScope = CoroutineScope(dispatcher + SupervisorJob())

    @Volatile var subscribedTopics: Set<String> = emptySet()

    val consumer: KafkaConsumer<ByteArray, ByteArray> by lazy {
        val config = Properties().apply {
            put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.brokers.joinToString(","))

            put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer::class.java)
            put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer::class.java)

            put(ConsumerConfig.GROUP_ID_CONFIG, config.groupId)
            put(ConsumerConfig.CLIENT_ID_CONFIG, config.consumerId)

            put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
            put(ConsumerConfig.ENABLE_METRICS_PUSH_CONFIG, false)
        }

        KafkaConsumer<ByteArray, ByteArray>(config)
    }

    private val subscriptions = Subscriptions()

    override suspend fun start() {
        check(!closed) { "Attempt to start a closed consumer" }

        if (started) return
        started = true

        processingScope.launch(kafkaDispatcher) {
            try {
                while (isActive && !closed) {
                    if (subscriptions.isNotEmpty()) {
                        updateSubscriptions()

                        val records = consumer.poll(config.pollTimeout.toJavaDuration())
                        if (!records.isEmpty) processBatch(records)
                    } else delay(100.milliseconds)
                }
            } catch (e: WakeupException) {
                if (!closed) throw e
            }
        }
    }

    private suspend fun updateSubscriptions() {
        val activeTopics = subscriptions.entries
            .filter { it.value.isNotEmpty() }
            .map { it.key }
            .toSet()

        if (activeTopics == subscribedTopics) return

        subscribedTopics = activeTopics

        withContext(kafkaDispatcher) {
            if (activeTopics.isNotEmpty()) consumer.subscribe(activeTopics)
            else consumer.unsubscribe()
        }
    }

    private suspend fun processBatch(
        records: ConsumerRecords<ByteArray, ByteArray>
    ) {
        val recordsByPartition = records.partitions().associateWith { partition ->
            records.records(partition)
        }

        val partitionJobs = recordsByPartition.map { (partition, records) ->
            processingScope.async {
                dispatch(partition, records)?.let { partition to it }
            }
        }

        val commitOffsets = partitionJobs.awaitAll().filterNotNull().toMap()

        if (commitOffsets.isNotEmpty()) {
            withContext(kafkaDispatcher) {
                consumer.commitSync(commitOffsets)
            }
        }
    }

    private suspend fun dispatch(
        partition: TopicPartition,
        records: List<ConsumerRecord<ByteArray, ByteArray>>
    ): OffsetAndMetadata? {
        var lastSuccessfulOffset: Long? = null

        val subscribers = subscriptions[partition.topic()]
        if (subscribers.isNullOrEmpty()) return null

        for (record in records) {
            val message = Message(
                route = Route(topic = record.topic()),
                key = record.key()?.decodeToString(),
                metadata = record.headers().associate { it.key() to it.value() },
                data = record.value()
            )

            val processingJobs = subscribers.map { channel ->
                processingScope.async {
                    val delivery = Delivery(message)
                    val result = runCatching {
                        channel.send(delivery)
                        check(delivery.await() == Delivery.Result.Success) { "Delivery failed" }
                    }

                    return@async result.isSuccess
                }
            }

            val success = processingJobs.awaitAll().all { it }

            if (success) lastSuccessfulOffset = record.offset()
            else break
        }

        return lastSuccessfulOffset?.let { OffsetAndMetadata(it + 1) }
    }

    override suspend fun consume(route: Route): Flow<Message> {
        check(!closed) { "Attempt to consume events after closing the consumer" }

        val channel = Channel<Delivery>(Channel.RENDEZVOUS)
        subscriptions.computeIfAbsent(route.topic) { CopyOnWriteArrayList() }
            .add(channel)

        return flow {
            channel.consumeAsFlow().collect { delivery ->
                try {
                    emit(delivery.message)
                    delivery.ack()
                } catch (e: Throwable) {
                    delivery.nack()
                    throw e
                }
            }
        }.onCompletion {
            subscriptions[route.topic]?.remove(channel)
        }
    }

    override suspend fun close() = withContext(kafkaDispatcher) {
        if (closed || !started) return@withContext
        closed = true

        consumer.wakeup()

        subscriptions.values.flatten().forEach { it.close() }
        processingScope.cancel()

        consumer.close()
    }
}
