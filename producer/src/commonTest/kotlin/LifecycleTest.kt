import io.github.briangits.events.integration.producer.publish
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LifecycleTest {
    @Test
    fun `publishing after closing the publisher`() = runTest {
        val (producer, publisher) = createProducer()
        publisher.close()

        assertFailsWith<IllegalStateException> {
            producer.publish(TestEvent(id = 100L))
        }
    }
}