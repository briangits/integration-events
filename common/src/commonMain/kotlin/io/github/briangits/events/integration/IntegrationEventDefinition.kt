package io.github.briangits.events.integration

import kotlinx.serialization.KSerializer

class IntegrationEventDefinition<T>(
    val name: String,
    val topic: String,
    val serializer: KSerializer<T>,
    val key: T.() -> Any?
)
