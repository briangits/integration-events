package io.github.briangits.events.integration.broker.consumer

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.metadata.Metadata
import io.github.briangits.events.integration.serialization.Serializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer

abstract class Consumer(val serializer: Serializer) {
    abstract suspend fun consume(route: Route): Flow<Message<ByteArray>>

    suspend inline fun <reified T> consume(route: Route): Flow<Message<T>> =
        consume(route).map {
            val data = serializer.deserialize(it.data, serializer<T>())

            Message(
                route = it.route,
                key = it.key,
                metadata = it.metadata,
                data = data
            )
        }
}

suspend inline fun <reified T> Consumer.consume(
    topic: String,
    name: String = T::class.simpleName!!
): Flow<Message<T>> = consume<T>(Route(topic, name))

suspend inline fun <reified T> Consumer.subscribe(
    route: Route,
    scope: CoroutineScope? = null,
    crossinline block: suspend (data: T, metadata: Metadata) -> Unit
): Job {
    val subscriptionScope = scope ?: CoroutineScope(currentCoroutineContext())

    return subscriptionScope.launch(start = CoroutineStart.UNDISPATCHED) {
        consume<T>(route).collect {
            block(it.data, it.metadata)
        }
    }
}

suspend inline fun <reified  T> Consumer.subscribe(
    topic: String,
    name: String = T::class.simpleName!!,
    scope: CoroutineScope? = null,
    crossinline block: suspend (data: T, metadata: Metadata) -> Unit
): Job = subscribe<T>(route = Route(topic, name), scope, block)
