import io.github.briangits.events.integration.broker.kafka.consumer.relay.Consumer
import io.github.briangits.events.integration.broker.kafka.consumer.relay.ConsumerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

internal fun createConsumer(): Consumer {
    val options = ConsumerOptions(
        brokers = listOf(kafkaContainer.bootstrapServers),
        groupId = Uuid.random().toString(),
        consumerId = Uuid.random().toString(),
        pollTimeout = 100.milliseconds,
        dispatcher = Dispatchers.IO
    )

    return Consumer(options)
}

suspend fun KafkaProducer<ByteArray?, ByteArray>.send(
    topic: String,
    key: String?,
    payload: ByteArray,
    headers: Map<String, ByteArray> = emptyMap()
) {
    val record = ProducerRecord(topic, key?.encodeToByteArray(), payload).also {
        headers.forEach { key, value ->
            it.headers().add(key, value)
        }
    }

    suspendCancellableCoroutine { continuation ->
        send(record) { _, e ->
            if (e != null) continuation.resumeWithException(e)
            else continuation.resume(Unit)
        }
    }
}
