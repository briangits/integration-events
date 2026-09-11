package io.github.briangits.events.integration.broker.kafka.relay

import io.github.briangits.events.integration.broker.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import java.util.Properties
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal actual fun Relay(config: RelayConfig) = object : Relay {
    val mutex = Mutex()

    private var initialized: Boolean = false
    private var closed: Boolean = false

    private val producer: KafkaProducer<ByteArray, ByteArray> by lazy {
        val config =
            Properties().apply {
                put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.brokers.joinToString(","))
                put(ProducerConfig.ACKS_CONFIG, "all")

                put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer::class.java)
                put(
                    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                    ByteArraySerializer::class.java
                )
            }

        KafkaProducer<ByteArray, ByteArray>(config)
            .also { initialized = true }
    }

    override suspend fun publish(message: Message) {
        val producer = mutex.withLock {
            check(!closed) { "Attempt to publish an event after closing the publisher" }

            this.producer
        }

        val record: ProducerRecord<ByteArray?, ByteArray> = message.let {
            ProducerRecord(it.route.topic, it.key?.encodeToByteArray(), it.data).apply {
                message.metadata.entries.forEach {
                    headers().add(it.key, it.value)
                }
            }
        }

        suspendCancellableCoroutine { continuation ->
            producer.send(record) { _, e ->
                if (e != null) continuation.resumeWithException(e)
                else continuation.resume(Unit)
            }
        }
    }

    override suspend fun close() = mutex.withLock {
        if (closed || !initialized) return@withLock

        closed = true
        producer.close()
    }
}
