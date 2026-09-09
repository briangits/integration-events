package io.github.briangits.events.integration.broker.kafka.consumer

import kotlinx.coroutines.CoroutineDispatcher
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class ConsumerConfig(
    val brokers: List<String>,
    val groupId: String,
    var consumerId: String? = null,
    var pollTimeout: Duration = 1.seconds,
    var dispatcher: CoroutineDispatcher? = null
)
