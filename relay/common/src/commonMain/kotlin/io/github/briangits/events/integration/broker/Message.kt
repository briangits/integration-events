package io.github.briangits.events.integration.broker

class Message(
    val route: Route,
    val key: String? = null,
    val metadata: Map<String, ByteArray>,
    val data: ByteArray
)
