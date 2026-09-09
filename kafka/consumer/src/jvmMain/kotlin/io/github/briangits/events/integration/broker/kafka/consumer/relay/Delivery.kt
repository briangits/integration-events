package io.github.briangits.events.integration.broker.kafka.consumer.relay

import io.github.briangits.events.integration.broker.Message
import kotlinx.coroutines.CompletableDeferred

class Delivery(
    val message: Message,
) {

    enum class Result { Success, Failed }
    
    private val result = CompletableDeferred<Result>()

    fun ack()  { result.complete(Result.Success) }

    fun nack() { result.complete(Result.Failed) }

    suspend fun await() = result.await()
}
