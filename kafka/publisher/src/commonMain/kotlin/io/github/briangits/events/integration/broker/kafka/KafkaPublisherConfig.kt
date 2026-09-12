package io.github.briangits.events.integration.broker.kafka

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class KafkaPublisherConfig(
    val brokers: List<String>,
    var dispatcher: CoroutineDispatcher = Dispatchers.Default,
    block: KafkaPublisherConfig.() -> Unit = {}
) {
    init { block() }
}
