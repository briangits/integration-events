package io.github.briangits.events.integration.generators

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.withIndent
import io.github.briangits.events.integration.IntegrationEvent
import io.github.briangits.events.integration.IntegrationEventDefinition

data class GeneratedDefinition(
    val definitionClass: KSClassDeclaration,
    val codeBlock: CodeBlock
)

class DefinitionGenerationException(override val message: String) : Exception(message)

private data class EventDefinition(
    val name: String,
    val topic: String,
    val key: String?
)

private fun KSClassDeclaration.createDefinition(): EventDefinition {
    val annotation =
        this.annotations.first {
            it.annotationType.resolve().declaration.qualifiedName?.asString() ==
                    IntegrationEvent::class.qualifiedName
        }

    fun <T> KSAnnotation.getArgument(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return this.arguments
            .firstOrNull { it.name?.asString() == key }
            ?.value as? T
    }

    val eventQualifiedName = this.toClassName()

    val name = annotation.getArgument("name") ?: "$eventQualifiedName"
    val topic =
        annotation.getArgument<String>("topic").let {
            if(it.isNullOrEmpty()) {
                throw DefinitionGenerationException(
                    "@IntegrationEvent $eventQualifiedName must have a topic"
                )
            }

            return@let it
        }

    val key =
        annotation.getArgument<String>("key").also {
            val keyExists =
                this.getAllProperties().any { prop ->
                    prop.simpleName.asString() == it
                }

            if (!it.isNullOrEmpty() && !keyExists) {
                throw DefinitionGenerationException(
                    "The defined key property '$it' for @IntegrationEvent " +
                            "$eventQualifiedName does not exist"
                )
            }
        }

    return EventDefinition(name, topic, key)
}

internal fun generateDefinition(declaration: KSClassDeclaration): GeneratedDefinition {
    val eventType = declaration.toClassName()
    val definitionType = IntegrationEventDefinition::class.asClassName()

    val definition = declaration.createDefinition()

    val codeBlock = CodeBlock.builder()
        .apply {
            add("%T(\n", definitionType)

            withIndent {
                add("name = %S,\n", definition.name)
                add("topic = %S,\n", definition.topic)
                add("serializer = %T.serializer()\n", eventType)
            }

            add(") ")

            add("{ %L }", definition.key ?: "null")
        }.build()

    return GeneratedDefinition(declaration, codeBlock)
}