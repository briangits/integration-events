package io.github.briangits.events.integration.producer

import io.github.briangits.events.integration.serialization.Serializer
import io.github.briangits.events.integration.serialization.json.json
import kotlinx.coroutines.CoroutineDispatcher

internal expect val defaultDispatcher: CoroutineDispatcher

data class Config(
    var dispatcher: CoroutineDispatcher = defaultDispatcher,
    internal var serializer: Serializer = json()
) {
    fun serialization(block: () -> Serializer) {
        serializer = block()
    }
}