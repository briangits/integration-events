import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.consumer.Consumer
import io.github.briangits.events.integration.eventType
import io.github.briangits.events.integration.serialization.json.json
import kotlinx.serialization.serializer

private val json = json()

fun createConsumer(): Pair<Consumer, TestMessageConsumer> {
    val relay = TestMessageConsumer()
    val consumer = Consumer(relay) {
        serialization { json }
    }

    with(consumer) {
        register(eventType<TestEvent>(), testEvent)
    }

    return consumer to relay
}

fun createMessage(
    data: TestEvent,
    eventName: String = "test",
    extraMetadata: Map<String, Any> = emptyMap()
): Message {
    val serializedData = json.serialize(data, serializer<TestEvent>())
    val serializedName = json.serialize(eventName, serializer<String>())

    val metadata = extraMetadata.mapValues { (_, v) ->
        json.serialize(v.toString(), serializer<String>())
    }.toMutableMap()
    metadata["eventName"] = serializedName

    return Message(
        route = Route(topic = "test"),
        key = data.id.toString(),
        metadata = metadata,
        data = serializedData
    )
}
