package io.github.briangits.events.integration

import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

data class EventType<out T : Any>(
    val eventClass: KClass<out T>,
    val type: KType
)

inline fun <reified T : Any> eventType(): EventType<T> =
    EventType(eventClass = T::class, type =  typeOf<T>())
