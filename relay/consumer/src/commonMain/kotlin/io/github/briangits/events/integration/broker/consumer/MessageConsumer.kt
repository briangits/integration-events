package io.github.briangits.events.integration.broker.consumer

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import kotlinx.coroutines.flow.Flow

interface MessageConsumer {
    suspend fun start()
    suspend fun close()

    suspend fun consume(route: Route): Flow<Message>
}
