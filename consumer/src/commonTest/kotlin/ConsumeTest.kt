
import io.github.briangits.events.integration.consumer.Consumer
import io.github.briangits.events.integration.consumer.Event
import io.github.briangits.events.integration.eventType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ConsumeTest {
    @Test
    fun `consuming events`() = runTest {
        val (consumer, relay) = createConsumer()
        val eventData = TestEvent(id = 42L)
        val message = createMessage(eventData)

        val flow = consumer.consume(eventType<TestEvent>())

        backgroundScope.launch {
            relay.emit(message)
        }

        val receivedEvent = flow.first()
        assertEquals(42L, receivedEvent.data.id)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `consume() filters unmatched events`() = runTest {
        val (consumer, relay) = createConsumer()
        val message = createMessage(TestEvent(id = 1L), eventName = testEvent.name)
        val otherMessage = createMessage(TestEvent(id = 2L), eventName = "other")

        val received = mutableListOf<Event<TestEvent>>()
        backgroundScope.launch {
            consumer.consume(eventType<TestEvent>()).collect { received.add(it) }
        }

        backgroundScope.launch {
            relay.emit(message)
            relay.emit(otherMessage)
        }

        runCurrent()

        assertEquals(1, received.size)
        assertEquals(1L, received.first().data.id)
    }

    @Test
    fun `consuming unregistered events`() = runTest {
        val relay = TestMessageConsumer()
        val consumer = Consumer(relay)

        val exception = assertFailsWith<IllegalStateException> {
            consumer.consume(eventType<TestEvent>())
        }

        assertTrue(exception.message!!.contains("No event definition found"))
    }
}