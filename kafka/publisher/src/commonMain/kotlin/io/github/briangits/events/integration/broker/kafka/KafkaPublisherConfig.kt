package io.github.briangits.events.integration.broker.kafka

class KafkaPublisherConfig(
    val brokers: List<String>,
    block: KafkaPublisherConfig.() -> Unit = {}
) {
    init { block() }
}
