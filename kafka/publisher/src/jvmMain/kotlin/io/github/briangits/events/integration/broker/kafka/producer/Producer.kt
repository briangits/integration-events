package io.github.briangits.events.integration.broker.kafka.producer

import io.github.briangits.events.integration.broker.Message
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import java.util.Properties
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private fun createConfig(config: ProducerOptions): Properties =
    Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.brokers.joinToString(","))

        put(ProducerConfig.ACKS_CONFIG, "all")

        val serializer = ByteArraySerializer::class.java
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializer)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer)
    }

private val Message.producerRecord: ProducerRecord<ByteArray?, ByteArray>
    get() = ProducerRecord(route.topic, key?.encodeToByteArray(), data)
        .apply {
            metadata.entries.forEach {
                headers().add(it.key, it.value)
            }
        }


internal actual fun Producer(options: ProducerOptions): Producer {
    val producer = KafkaProducer<ByteArray?, ByteArray>(createConfig(options))

    return object : Producer {
        @Volatile var closed: Boolean = false

        override suspend fun publish(message: Message) {
            check(!closed) { "Attempt to publish an event after closing the publisher" }

            suspendCancellableCoroutine { continuation ->
                producer.send(message.producerRecord) { _, e ->
                    if (e != null) continuation.resumeWithException(e)
                    else continuation.resume(Unit)
                }
            }
        }

        override suspend fun close() {
            if (closed) return

            closed = true
            producer.close()
        }
    }
}
