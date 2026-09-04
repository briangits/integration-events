package io.github.briangits.events.integration

import kotlin.reflect.KClass

abstract class IntegrationEventRegistry {
    val events: Map<EventType<*>, IntegrationEventDefinition<*>>
    field = mutableMapOf()

    fun <T : Any> register(
        type: EventType<T>,
        definition: IntegrationEventDefinition<T>
    ) {
        events[type] = definition
    }
}
