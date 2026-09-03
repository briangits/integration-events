package io.github.briangits.events.integration

import com.google.devtools.ksp.gradle.KspExtension
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private fun artifact(name: String) = "${Artifacts.GROUP}:$name:${Artifacts.VERSION}"

private val COMMON = artifact("common")
private val ANNOTATIONS = artifact("annotations")
private val ANNOTATION_PROCESSOR = artifact("annotation-processor")

class IntegrationEventsPlugin : Plugin<Project> {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    override fun apply(target: Project) = with(target) {
        if (!pluginManager.hasPlugin(KspGradleSubplugin.KSP_GROUP_ID)) {
            apply<KspGradleSubplugin>()
        }

        val extension = extensions.create<IntegrationEventsExtension>("integrationEvents")
            .apply {
                packageName.convention(project.group.toString())
                loaderFunction.convention("registerGeneratedEvents")
            }

        afterEvaluate {
            extensions.configure<KspExtension> {
                val targetPackage = extension.packageName.get()
                val loaderFunctionName = extension.loaderFunction.get()

                arg("integrationEvents.targetPackage", targetPackage)
                arg("integrationEvents.loaderFunctionName", loaderFunctionName)
            }
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            project.dependencies {
                add("ksp", ANNOTATION_PROCESSOR)

                add("implementation", COMMON)
                add("implementation", ANNOTATIONS)
            }
        }

        pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            project.dependencies {
                add("kspCommonMainMetadata", ANNOTATION_PROCESSOR)
            }

            project.extensions.configure<KotlinMultiplatformExtension> {
                sourceSets {
                    it.commonMain {
                        dependencies {
                            implementation(COMMON)
                            implementation(ANNOTATIONS)
                        }

                        generatedKotlin.srcDir(
                            project.layout.buildDirectory
                                .dir("generated/ksp/metadata/commonMain/kotlin")
                        )
                    }
                }
            }

            tasks.withType<KotlinCompile>().all {
                if (it.name != "kspCommonMainKotlinMetadata") {
                    it.dependsOn("kspCommonMainKotlinMetadata")
                }
            }
        }
    }
}
