import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.kafka.producer.Producer
import io.github.briangits.events.integration.broker.kafka.producer.ProducerOptions
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class ProducerLifecycleTest {
    @Test
    fun `publishing after relay closing the producer`() = runTest {
        val config = ProducerOptions(brokers = listOf(kafkaContainer.bootstrapServers))
        val producer = Producer(config)

        val message = Message(
            route = Route(topic = Uuid.random().toString()),
            key = null,
            metadata = emptyMap(),
            data = ByteArray(0)
        )

        // Force initialization
        producer.publish(message)

        producer.close()

        val exception = assertFailsWith<IllegalStateException> {
            producer.publish(message)
        }

        assertTrue(exception.message!!.contains("closing the publisher"))
    }
}