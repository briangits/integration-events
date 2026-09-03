package io.github.briangits.events.integration

import kotlin.reflect.KClass

interface IntegrationEventRegistry {
    val events: Map<KClass<*>, IntegrationEventDefinition<*>>

    fun <T : Any> register(
        type: KClass<T>,
        definition: IntegrationEventDefinition<T>
    )

    companion object : IntegrationEventRegistry {
        override val events: Map<KClass<*>, IntegrationEventDefinition<*>>
            field = mutableMapOf()

        override fun <T : Any> register(
            type: KClass<T>,
            definition: IntegrationEventDefinition<T>
        ) {
            events[type] = definition
        }
    }
}
