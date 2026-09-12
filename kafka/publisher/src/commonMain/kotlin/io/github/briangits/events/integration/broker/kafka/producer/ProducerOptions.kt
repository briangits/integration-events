package io.github.briangits.events.integration.broker.kafka.producer

import io.github.briangits.events.integration.broker.kafka.KafkaPublisherConfig

internal data class ProducerOptions(
    val brokers: List<String>
)

internal fun createOptions(config: KafkaPublisherConfig): ProducerOptions =
    ProducerOptions(
        brokers = config.brokers
    )
