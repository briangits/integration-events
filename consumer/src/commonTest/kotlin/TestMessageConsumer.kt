
import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.consumer.MessageConsumer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TestMessageConsumer : MessageConsumer {
    var isStarted: Boolean = false
        private set
    var isClosed: Boolean = false
        private set

    private val flowsByRoute = mutableMapOf<String, MutableSharedFlow<Message>>()

    override suspend fun start() {
        check(!isClosed) { "Cannot start a closed consumer" }
        isStarted = true
    }

    override suspend fun close() {
        isClosed = true
        isStarted = false
    }

    override suspend fun consume(route: Route): Flow<Message> {
        check(!isClosed) { "Cannot consume from a closed consumer" }
        return getOrCreateFlow(route.topic).asSharedFlow()
    }

    suspend fun emit(message: Message) {
        getOrCreateFlow(message.route.topic).emit(message)
    }

    private fun getOrCreateFlow(topic: String): MutableSharedFlow<Message> {
        return flowsByRoute.getOrPut(topic) { MutableSharedFlow() }
    }
}
