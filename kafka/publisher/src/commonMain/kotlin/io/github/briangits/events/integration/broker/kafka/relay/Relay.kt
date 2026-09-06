package io.github.briangits.events.integration.broker.kafka.relay

import io.github.briangits.events.integration.broker.Message

internal interface Relay {
    suspend fun publish(message: Message)

    suspend fun close()
}

internal expect fun Relay(config: RelayConfig): Relay
