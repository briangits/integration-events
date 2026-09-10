package io.github.briangits.events.integration

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class IntegrationEvent(
    val topic: String,
    val name: String = "",
    val key: String = ""
)
