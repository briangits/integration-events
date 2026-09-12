package io.github.briangits.events.integration.broker.kafka.consumer.relay

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Duration

internal data class ConsumerOptions(
    val brokers: List<String>,
    val groupId: String,
    val consumerId: String?,
    val pollTimeout: Duration,
    val dispatcher: CoroutineDispatcher?
)
