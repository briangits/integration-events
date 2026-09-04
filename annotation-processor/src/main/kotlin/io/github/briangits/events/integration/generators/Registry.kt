package io.github.briangits.events.integration.generators

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.withIndent
import io.github.briangits.events.integration.EventType
import io.github.briangits.events.integration.IntegrationEventRegistry

val REGISTRY = IntegrationEventRegistry::class.asClassName()

private val typeDeriver = MemberName(EventType::class.asClassName().packageName, "eventType")

fun generateRegistry(
    definitions: List<GeneratedDefinition>,
    targetPackage: String,
    loaderFunctionName: String
): FileSpec {
    val file = FileSpec.builder(targetPackage, "GeneratedRegistry")

    val registryInitializer =
        FunSpec.builder(loaderFunctionName)
            .addModifiers(KModifier.INTERNAL)
            .receiver(REGISTRY)
            .apply {
                for ((definitionClass, codeBlock) in definitions) {
                    val eventType = definitionClass.toClassName()

                    val registration =
                        CodeBlock.builder()
                            .apply {
                                add("register(\n")
                                withIndent {
                                    add("type = %M<%T>(),\n", typeDeriver, eventType)
                                    add("definition = %L\n", codeBlock)
                                }
                                add(")\n")
                            }.build()

                    addCode(registration)
                }
            }.build()

    file.addFunction(registryInitializer)

    return file.build()
}