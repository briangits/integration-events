package io.github.briangits.events.integration.broker

data class Route(
    val topic: String,
    val name: String,
    val key: String? = null
)
