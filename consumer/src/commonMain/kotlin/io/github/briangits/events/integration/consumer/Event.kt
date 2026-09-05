package io.github.briangits.events.integration.consumer

import io.github.briangits.events.integration.metadata.Metadata

data class Event<out T>(
    val data: T,
    val metadata: Metadata
)
