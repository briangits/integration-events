import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.kafka.producer.Producer
import io.github.briangits.events.integration.broker.kafka.producer.ProducerOptions
import kotlinx.coroutines.test.runTest
import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid

class PublishTest {
    @Test
    fun `publishing messages`() = runTest {
        val topic = Uuid.random().toString()
        val consumer = testConsumer(topic)

        val config = ProducerOptions(brokers = listOf(kafkaContainer.bootstrapServers))
        val producer = Producer(config)

        val eventKey = Uuid.random().toString()
        val payload = "This is a test message"
        val messageType = "TestMessage"

        val message = Message(
            route = Route(topic = topic),
            key = eventKey,
            metadata = mapOf("messageType" to messageType.encodeToByteArray()),
            data = payload.encodeToByteArray()
        )

        producer.publish(message)

        val records = consumer.poll(Duration.ofSeconds(10))
        assertEquals(1, records.count())

        val record = records.first()
        assertEquals(topic, record.topic())
        assertEquals(eventKey, record.key()?.decodeToString())
        assertEquals(payload, record.value().decodeToString())

        val header = record.headers().headers("messageType").firstOrNull()
        assertNotNull(header)
        assertEquals(messageType, header.value().decodeToString())

        producer.close()
    }
}