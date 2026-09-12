import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.testcontainers.kafka.KafkaContainer
import java.util.Properties

val kafkaContainer by lazy {
    KafkaContainer("apache/kafka-native:latest").also {
        it.start()
    }
}

fun createProducer(): KafkaProducer<ByteArray?, ByteArray> {
    val config = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)

        val serializer = ByteArraySerializer::class.java
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializer)
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializer)

        put(ProducerConfig.ACKS_CONFIG, "all")
        put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true)
    }

    return KafkaProducer(config)
}
