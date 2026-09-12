import io.github.briangits.events.integration.broker.Route
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class ConsumerLifecycleTest {
    @Test
    fun `consuming after closing consumer`() = runTest {
        val consumer = createConsumer()
        consumer.close()

        assertFailsWith<IllegalStateException> {
            consumer.consume(route = Route(topic = Uuid.random().toString()))
        }
    }
}
