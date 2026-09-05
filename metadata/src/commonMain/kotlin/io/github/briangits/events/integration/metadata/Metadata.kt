package io.github.briangits.events.integration.metadata

import io.github.briangits.events.integration.serialization.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer

class Metadata(private val serializer: Serializer) {
    constructor(serializer: Serializer, entries: Map<String, ByteArray>) : this(serializer) {
        this.entries.putAll(entries)
    }

    val entries: Map<String, ByteArray>
    field = mutableMapOf<String, ByteArray>()

    private val cached = mutableMapOf<String, Any?>()

    operator fun <T> get(key: String, serializer: KSerializer<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return cached.getOrPut(key) {
            entries[key]?.let { this.serializer.deserialize(it, serializer) }
        } as T
    }

    inline operator fun <reified T> get(key: String): T? = get(key, serializer<T>())

    operator fun <T> set(key: String, value: T, serializer: KSerializer<T>) {
        entries.set(key, this.serializer.serialize(value, serializer))
            .also { cached[key] = value }
    }

    inline operator fun <reified T> set(key: String, value: T) = set(key, value, serializer<T>())
    inline infix fun <reified T> String.to(value: T) = set(this, value)

    fun clear(key: String) = entries.remove(key).also { cached.remove(key) }
    operator fun minusAssign(key: String) { clear(key) }
}
