package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.EventType
import io.github.briangits.events.integration.IntegrationEventDefinition
import io.github.briangits.events.integration.IntegrationEventRegistry
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.eventType
import io.github.briangits.events.integration.metadata.Metadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import io.github.briangits.events.integration.broker.consumer.MessageConsumer

class Consumer(
    val consumer: MessageConsumer,
    config: ConsumerConfig.() -> Unit = {}
) : IntegrationEventRegistry() {
    private val config = ConsumerConfig { config() }

    suspend fun <T : Any> consume(type: EventType<T>): Flow<Event<T>> {
        @Suppress("UNCHECKED_CAST")
        val definition = events[type] as? IntegrationEventDefinition<T>
            ?: error("No event definition found for ${type.eventClass.simpleName}")

        return consumer.consume(route = Route(topic = definition.topic))
            .filter {
                val name = it.metadata["eventName"]?.let {
                    config.serializer.deserialize(it, serializer<String>())
                }

                name == definition.name
            }.map {
                @Suppress("UNCHECKED_CAST")
                Event(
                    data = config.serializer.deserialize(it.data, serializer(type.type)) as T,
                    metadata = Metadata(config.serializer, it.metadata)
                )
            }
    }
}

suspend inline fun <reified T : Any> Consumer.subscribe(
    scope: CoroutineScope? = null,
    crossinline block: suspend (data: T, metadata: Metadata) -> Unit
): Job {
    val subscriptionScope = scope ?: CoroutineScope(currentCoroutineContext())

    return subscriptionScope.launch(start = CoroutineStart.UNDISPATCHED) {
        consume(type = eventType<T>()).collect {
            block(it.data, it.metadata)
        }
    }
}
