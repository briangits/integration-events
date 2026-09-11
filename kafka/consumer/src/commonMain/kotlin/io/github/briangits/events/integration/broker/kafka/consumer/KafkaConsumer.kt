package io.github.briangits.events.integration.broker.kafka.consumer

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.consumer.MessageConsumer
import io.github.briangits.events.integration.broker.kafka.consumer.relay.Relay
import io.github.briangits.events.integration.broker.kafka.consumer.relay.RelayConfig
import kotlinx.coroutines.flow.Flow

class KafkaConsumer(
    val brokers: List<String>,
    val groupId: String,
    config: KafkaConsumerConfig.() -> Unit = {}
) : MessageConsumer {
    private val config = KafkaConsumerConfig(brokers, groupId) { config() }
    private val relay: Relay by lazy {
        val config = RelayConfig(
            brokers = this.config.brokers,
            groupId = this.config.groupId,
            consumerId = this.config.consumerId,
            pollTimeout = this.config.pollTimeout,
            dispatcher = this.config.dispatcher
        )

        Relay(config)
    }

    override suspend fun start() = relay.start()

    override suspend fun close() = relay.close()

    override suspend fun consume(route: Route): Flow<Message> = relay.consume(route)
}
