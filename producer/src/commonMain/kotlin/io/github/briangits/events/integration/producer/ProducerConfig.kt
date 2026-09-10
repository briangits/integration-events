package io.github.briangits.events.integration.producer

import io.github.briangits.events.integration.serialization.Serializer
import io.github.briangits.events.integration.serialization.json.json
import kotlinx.coroutines.CoroutineDispatcher

internal expect val defaultDispatcher: CoroutineDispatcher

class ProducerConfig(
    var dispatcher: CoroutineDispatcher = defaultDispatcher,
    internal var serializer: Serializer = json(),
    block: ProducerConfig.() -> Unit = {}
) {
    init { block() }

    fun serialization(block: () -> Serializer) {
        serializer = block()
    }
}
