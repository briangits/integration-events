import io.github.briangits.events.integration.eventType
import io.github.briangits.events.integration.producer.Producer

fun createProducer(): Pair<Producer, TestPublisher> {
    val publisher = TestPublisher()
    val producer = Producer(publisher).also {
        with(it) {
            register(eventType<TestEvent>(), testEvent)
        }
    }

    return producer to publisher
}
