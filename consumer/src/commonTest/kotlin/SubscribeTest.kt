
import io.github.briangits.events.integration.consumer.subscribe
import io.github.briangits.events.integration.metadata.Metadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SubscribeTest {
    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `subscribing to events`() = runTest {
        val (consumer, relay) = createConsumer()
        val received = mutableListOf<Pair<TestEvent, Metadata>>()

        val job = consumer.subscribe<TestEvent>(scope = backgroundScope) { data, metadata ->
            received.add(data to metadata)
        }

        relay.emit(
            message = createMessage(
                data = TestEvent(id = 1L),
                extraMetadata = mapOf("key" to "value")
            )
        )
        runCurrent()

        assertEquals(1, received.size)
        val (data, metadata) = received.first()

        assertEquals(1L, data.id)
        assertEquals("value", metadata["key"])

        job.cancel()
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `cancelling a subscription`() = runTest {
        val (consumer, relay) = createConsumer()
        val received = mutableListOf<TestEvent>()

        val job = consumer.subscribe<TestEvent>(scope = backgroundScope) { data, _ ->
            received.add(data)
        }

        relay.emit(createMessage(TestEvent(id = 1L)))
        runCurrent()

        job.cancelAndJoin()

        relay.emit(createMessage(TestEvent(id = 2L)))
        runCurrent()

        assertEquals(1, received.size)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `cancelling parent coroutine scope cancels subscription`() = runTest {
        val (consumer, relay) = createConsumer()
        val scope = CoroutineScope(Dispatchers.Default)
        val received = mutableListOf<TestEvent>()

        val job = consumer.subscribe<TestEvent>(scope) { data, _ ->
            received.add(data)
        }

        relay.emit(createMessage(TestEvent(id = 10L)))
        runCurrent()

        scope.cancel()
        runCurrent()

        assertTrue(job.isCancelled)

        relay.emit(createMessage(TestEvent(id = 20L)))
        runCurrent()

        assertEquals(1, received.size)
    }

    @Test
    @OptIn(ExperimentalCoroutinesApi::class)
    fun `multiple subscriptions to the same event`() = runTest {
        val (consumer, relay) = createConsumer()
        val subscriber1Events = mutableListOf<TestEvent>()
        val subscriber2Events = mutableListOf<TestEvent>()

        val job1 = consumer.subscribe<TestEvent>(scope = backgroundScope) { data, _ ->
            subscriber1Events.add(data)
        }
        val job2 = consumer.subscribe<TestEvent>(scope = backgroundScope) { data, _ ->
            subscriber2Events.add(data)
        }

        relay.emit(createMessage(TestEvent(id = 500L)))
        runCurrent()

        assertEquals(1, subscriber1Events.size)
        assertEquals(1, subscriber2Events.size)
        assertEquals(500L, subscriber1Events.first().id)
        assertEquals(500L, subscriber2Events.first().id)

        job1.cancel()
        job2.cancel()
    }
}
