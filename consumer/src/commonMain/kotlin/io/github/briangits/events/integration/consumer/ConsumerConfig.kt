package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.serialization.Serializer
import io.github.briangits.events.integration.serialization.json.json

class ConsumerConfig(
    internal var serializer: Serializer = json(),
    block: ConsumerConfig.() -> Unit = {}
) {
    init { block() }

    fun serialization(block: () -> Serializer) {
        serializer = block()
    }
}
