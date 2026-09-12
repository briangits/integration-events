package io.github.briangits.events.integration.broker.kafka

import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.kafka.producer.Producer
import io.github.briangits.events.integration.broker.kafka.producer.createOptions
import io.github.briangits.events.integration.broker.publisher.Publisher
import kotlinx.coroutines.withContext

class KafkaPublisher(
    brokers: List<String>,
    configBlock: KafkaPublisherConfig.() -> Unit = {}
) : Publisher {
    private val config = KafkaPublisherConfig(brokers) { configBlock() }

    private val producer by lazy { Producer(options = createOptions(config)) }

    override suspend fun publish(message: Message) =
        withContext(config.dispatcher) { producer.publish(message) }

    override suspend fun close() = withContext(config.dispatcher) { producer.close() }
}
