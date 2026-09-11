
import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.publisher.Publisher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TestPublisher : Publisher {
    private var closed: Boolean = false

    val messages = mutableListOf<Message>()

    private val dispatcher = Dispatchers.Default.limitedParallelism(1)

    override suspend fun publish(message: Message) = withContext(dispatcher) {
        check(!closed) { "Attempt to publish an event after closing the publisher" }

        messages += (message)
    }

    override suspend fun close() = withContext(dispatcher) {
        closed = true
    }
}
