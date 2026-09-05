package io.github.briangits.events.integration.broker.publisher

import io.github.briangits.events.integration.broker.Message

interface Publisher {
    suspend fun publish(message: Message)
}
