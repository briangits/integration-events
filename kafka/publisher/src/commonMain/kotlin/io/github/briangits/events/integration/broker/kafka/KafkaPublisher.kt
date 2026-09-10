package io.github.briangits.events.integration.broker.kafka

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.kafka.relay.Relay
import io.github.briangits.events.integration.broker.kafka.relay.RelayConfig
import io.github.briangits.events.integration.broker.publisher.Publisher

class KafkaPublisher(
    brokers: List<String>,
    config: KafkaPublisherConfig.() -> Unit = {}
) : Publisher {
    private val config = KafkaPublisherConfig(brokers) { config() }

    private val relay by lazy {
        val config = RelayConfig(
            brokers = this.config.brokers
        )

        Relay(config)
    }

    override suspend fun publish(message: Message) = relay.publish(message)

    override suspend fun close() = relay.close()
}
