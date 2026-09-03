package io.github.briangits.events.integration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntegrationEvent(
    val name: String = "",
    val topic: String,
    val key: String = ""
)
