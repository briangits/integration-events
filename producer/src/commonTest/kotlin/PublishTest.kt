import io.github.briangits.events.integration.metadata.Metadata
import io.github.briangits.events.integration.producer.Producer
import io.github.briangits.events.integration.producer.publish
import io.github.briangits.events.integration.serialization.json.json
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

private val json = json()

class PublishTest {
    @Test
    fun `publishing events`() = runTest {
        val (producer, publisher) = createProducer()
        val event = TestEvent(id = 123L)

        producer.publish(event)

        assertEquals(1, publisher.messages.size)
        val message = publisher.messages.first()

        assertEquals("test", message.route.topic)
        assertEquals("123", message.key)
    }

    @Test
    fun `publishing with metadata`() = runTest {
        val (producer, publisher) = createProducer()
        val event = TestEvent(id = 456L)

        val sample = mapOf(
            "key" to "value",
            "key2" to "value2"
        )

        producer.publish(event) {
            for ((key, value) in sample) {
                set(key, value)
            }
        }

        val message = publisher.messages.first()
        val metadata = Metadata(json, message.metadata)

        sample.forEach { (key, value) ->
            assertEquals(value, metadata.get(key))
        }
    }

    @Test
    fun `publishing unregistered events`() = runTest {
        val publisher = TestPublisher()
        val producer = Producer(publisher)

        val exception = assertFailsWith<IllegalStateException> {
            producer.publish(TestEvent(id = 1L))
        }

        assertTrue(exception.message!!.contains("No event definition found"))
    }
}
