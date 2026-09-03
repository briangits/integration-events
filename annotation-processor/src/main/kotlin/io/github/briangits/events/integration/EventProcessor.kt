package io.github.briangits.events.integration

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.briangits.events.integration.generators.DefinitionGenerationException
import io.github.briangits.events.integration.generators.GeneratedDefinition
import io.github.briangits.events.integration.generators.generateDefinition
import io.github.briangits.events.integration.generators.generateRegistry
import kotlinx.serialization.Serializable

class EventProcessor(
    val environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val targetPackage =
        environment.options["integrationEvents.targetPackage"]
            ?: error("No package configured for generated events output")
    private val loaderFunctionName =
        environment.options["integrationEvents.loaderFunctionName"]
            ?: "registerGeneratedEvents"

    private val definitions = mutableListOf<GeneratedDefinition>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val (declarations, unprocessable) =
            resolver.getSymbolsWithAnnotation(IntegrationEvent::class.qualifiedName!!)
                .partition { it.validate() }

        if (declarations.isNotEmpty()) {
            declarations.forEach {
                if (it !is KSClassDeclaration) {
                    environment.logger.error(
                        message = "Obly classes should be annotated @IntegrationEvent",
                        symbol = it
                    )

                    return@forEach
                }

                val isSerializable =
                    it.annotations.any {
                        it.annotationType.resolve().toClassName() ==
                                Serializable::class.asClassName()
                    }

                if (!isSerializable) {
                    environment.logger.error(
                        message = "Integration events must also be annotated with " +
                                "${Serializable::class.asClassName()}",
                        symbol = it
                    )

                    return@forEach
                }

                runCatching { definitions += generateDefinition(it) }
                    .onFailure { e ->
                        if (e !is DefinitionGenerationException) throw e

                        environment.logger.error(e.message, it)
                    }
            }
        }

        return unprocessable
    }

    override fun finish() {
        val registry = generateRegistry(definitions, targetPackage, loaderFunctionName)
        registry.writeTo(environment.codeGenerator, aggregating = true)
    }
}
