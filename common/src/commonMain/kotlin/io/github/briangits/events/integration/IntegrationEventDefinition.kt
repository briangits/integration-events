package io.github.briangits.events.integration

class IntegrationEventDefinition<T : Any>(
    val name: String,
    val topic: String,
    val key: T.() -> Any?
)
