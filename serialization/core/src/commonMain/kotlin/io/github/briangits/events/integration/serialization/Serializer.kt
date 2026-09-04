package io.github.briangits.events.integration.serialization

import kotlinx.serialization.KSerializer

interface Serializer {
    fun <T> serialize(value: T, serializer: KSerializer<T>): ByteArray
    
    fun <T> deserialize(bytes: ByteArray, serializer: KSerializer<T>): T
}
