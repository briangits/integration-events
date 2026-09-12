
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.testcontainers.kafka.KafkaContainer
import java.util.Properties

val kafkaContainer by lazy {
    KafkaContainer("apache/kafka-native:latest").also {
        it.start()
    }
}

fun testConsumer(vararg topics: String): KafkaConsumer<ByteArray, ByteArray> {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
        put(ConsumerConfig.GROUP_ID_CONFIG, "test-group-${System.currentTimeMillis()}")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

        val deserializer = ByteArrayDeserializer::class.java
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializer)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer)
    }

    return KafkaConsumer<ByteArray, ByteArray>(props).also {
        it.subscribe(topics.toList())
    }
}