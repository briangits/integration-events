package io.github.briangits.events.integration.broker.kafka.consumer

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.consumer.MessageConsumer
import io.github.briangits.events.integration.broker.kafka.consumer.relay.Consumer
import io.github.briangits.events.integration.broker.kafka.consumer.relay.ConsumerOptions
import kotlinx.coroutines.flow.Flow

class KafkaConsumer(
    val brokers: List<String>,
    val groupId: String,
    config: KafkaConsumerConfig.() -> Unit = {}
) : MessageConsumer {
    private val config = KafkaConsumerConfig(brokers, groupId) { config() }
    private val consumer: Consumer by lazy {
        val config = ConsumerOptions(
            brokers = this.config.brokers,
            groupId = this.config.groupId,
            consumerId = this.config.consumerId,
            pollTimeout = this.config.pollTimeout,
            dispatcher = this.config.dispatcher
        )

        Consumer(config)
    }

    override suspend fun start() = consumer.start()

    override suspend fun close() = consumer.close()

    override suspend fun consume(route: Route): Flow<Message> = consumer.consume(route)
}
