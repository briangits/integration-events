import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

plugins {
    // Platforms
    alias(kt.plugins.jvm) apply false
    alias(kt.plugins.multiplatform) apply false
    alias(kt.plugins.multiplatform.android) apply false
}

group = "io.github.briangits.events.integration"
version = "0.0.1"

allprojects {
    group = rootProject.group
    version = rootProject.version
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmExtension> {
            jvmToolchain(21)
        }
    }

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            jvmToolchain(21)

            targets.withType<KotlinJvmTarget>().configureEach {
                compilerOptions {
                    jvmTarget = JvmTarget.JVM_21
                }
            }
        }
    }
}
