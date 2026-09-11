
import io.github.briangits.events.integration.eventType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LifecycleTest {
    @Test
    fun `consuming events after closing the relay`() = runTest {
        val (consumer, relay) = createConsumer()
        relay.close()

        assertFailsWith<IllegalStateException> {
            consumer.consume(eventType<TestEvent>())
        }
    }
}
