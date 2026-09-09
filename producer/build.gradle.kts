import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)
}

kotlin {
    jvm()

    android {
        namespace = project.group.toString()
        compileSdk = 37
        minSdk = 21

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }

    js {
        browser()
        nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        commonMain.dependencies {
            api(projects.common)

            api(projects.serialization.core)
            implementation(projects.serialization.json)

            api(projects.metadata)
            api(projects.relay.publisher)

            implementation(kotlinx.coroutines)
        }
    }
}
