package io.github.briangits.events.integration

import kotlin.reflect.KClass

abstract class IntegrationEventRegistry {
    val events: Map<KClass<*>, IntegrationEventDefinition<*>>
    field = mutableMapOf()

    fun <T : Any> register(
        type: KClass<T>,
        definition: IntegrationEventDefinition<T>
    ) {
        events[type] = definition
    }
}
