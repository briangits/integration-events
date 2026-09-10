package io.github.briangits.events.integration.producer

import io.github.briangits.events.integration.EventType
import io.github.briangits.events.integration.IntegrationEventDefinition
import io.github.briangits.events.integration.IntegrationEventRegistry
import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.publisher.Publisher
import io.github.briangits.events.integration.eventType
import io.github.briangits.events.integration.metadata.Metadata
import kotlinx.coroutines.withContext
import kotlinx.serialization.serializer

class Producer(
    val publisher: Publisher,
    config: Config.() -> Unit,
) : IntegrationEventRegistry() {
    private val config = Config().apply { config() }

    suspend fun <T : Any> publish(
        data: T,
        type: EventType<T>,
        block: Metadata.() -> Unit
    ) = withContext(config.dispatcher) {
        @Suppress("UNCHECKED_CAST")
        val definition = events[type] as? IntegrationEventDefinition<T>
            ?: error("No event definition found for ${type.eventClass.qualifiedName}")

        val metadata = Metadata(config.serializer).apply {
            block()

            "eventName" to definition.name
        }

        val message = Message(
            route = Route(topic = definition.topic),
            key = definition.key(data)?.toString(),
            metadata = metadata.entries,
            data = config.serializer.serialize(data, serializer(type.type))
        )

        publisher.publish(message)
    }

    suspend inline fun <reified T : Any> publish(data: T, noinline block: Metadata.() -> Unit) =
        publish(data, type = eventType<T>(), block)
}
