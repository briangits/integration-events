
import io.github.briangits.events.integration.IntegrationEventDefinition
import kotlinx.serialization.Serializable

@Serializable
data class TestEvent(
    val id: Long
)

val testEvent = IntegrationEventDefinition<TestEvent>(
    topic = "test",
    name = "test",
    key = { id }
)
