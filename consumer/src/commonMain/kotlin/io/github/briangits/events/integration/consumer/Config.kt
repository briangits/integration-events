package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.serialization.Serializer
import io.github.briangits.events.integration.serialization.json.json

data class Config(
    internal var serializer: Serializer = json()
) {
    fun serialization(block: () -> Serializer) {
        serializer = block()
    }
}
