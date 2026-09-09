package io.github.briangits.events.integration.broker.kafka.consumer.relay

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import kotlinx.coroutines.flow.Flow

internal interface Relay {
    suspend fun start()
    suspend fun close()

    suspend fun consume(route: Route): Flow<Message>
}

internal expect fun Relay(config: RelayConfig): Relay
