package io.github.briangits.events.integration.broker.publisher

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.metadata.Metadata
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.serializer

abstract class Publisher(val format: BinaryFormat) {
    abstract suspend fun publish(message: Message<ByteArray>)

    suspend inline fun <reified T> publish(message: Message<T>) =
        publish(
            message.let {
                val value = format.encodeToByteArray(serializer<T>(), it.data)
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
        val metadata = Metadata(format)
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
