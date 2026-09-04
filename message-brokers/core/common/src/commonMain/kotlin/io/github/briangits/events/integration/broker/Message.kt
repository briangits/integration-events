package io.github.briangits.events.integration.broker

import io.github.briangits.events.integration.metadata.Metadata

class Message<T>(
    val route: Route,
    val metadata: Metadata,
    val data: T
)
