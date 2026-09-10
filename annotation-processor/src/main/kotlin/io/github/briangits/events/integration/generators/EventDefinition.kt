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

private fun KSClassDeclaration.propertyPathIsResolvable(
    path: String
): Boolean {
    var currentClass = this

    for (segment in path.split(".")) {
        val property = currentClass
            .getAllProperties()
            .firstOrNull { it.simpleName.asString() == segment }
            ?: return false

        val nextClass = property.type.resolve().declaration
                as? KSClassDeclaration
            ?: return false

        currentClass = nextClass
    }

    return true
}

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

    val name = annotation.getArgument("name") ?: simpleName.asString()
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
        annotation.getArgument<String>("key")?.also {
            val keyExists = propertyPathIsResolvable(it)

            if (!it.isEmpty() && !keyExists) {
                throw DefinitionGenerationException(
                    "The defined key property '$it' for @IntegrationEvent " +
                            "$eventQualifiedName does not exist"
                )
            }
        }

    return EventDefinition(name, topic, key)
}

internal fun generateDefinition(declaration: KSClassDeclaration): GeneratedDefinition {
    val definitionType = IntegrationEventDefinition::class.asClassName()

    val definition = declaration.createDefinition()

    val codeBlock = CodeBlock.builder()
        .apply {
            add("%T(\n", definitionType)

            withIndent {
                add("topic = %S,\n", definition.topic)
                add("name = %S,\n", definition.name)
                add("key = { %L }\n", definition.key ?: "null")
            }

            add(")")
        }.build()

    return GeneratedDefinition(declaration, codeBlock)
}