package io.github.briangits.events.integration.broker.publisher

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.metadata.Metadata
import io.github.briangits.events.integration.serialization.Serializer
import kotlinx.serialization.serializer

abstract class Publisher(val serializer: Serializer) {
    abstract suspend fun publish(message: Message<ByteArray>)

    suspend inline fun <reified T> publish(message: Message<T>) =
        publish(
            message.let {
                val value = serializer.serialize(it.data, serializer<T>())
                Message(route = it.route, metadata = it.metadata, data = value)
            }
        )

    suspend inline fun <reified T> publish(
        data: T,
        topic: String,
        name: String = T::class.simpleName!!,
        key: String? = null,
        block: Metadata.() -> Unit = {}
    ) {
        val metadata = Metadata(serializer)
            .apply { block() }

        publish(
            Message(
                route = Route(topic, name),
                key = key,
                metadata = metadata,
                data = data
            )
        )
    }
}
