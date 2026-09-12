package io.github.briangits.events.integration.broker.kafka.producer

import io.github.briangits.events.integration.broker.Message

internal interface Producer {
    suspend fun publish(message: Message)

    suspend fun close()
}

internal expect fun Producer(options: ProducerOptions): Producer
