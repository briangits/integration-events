import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

subprojects {
    group = "${rootProject.group}.broker.kafka"

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            jvm()

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

            targets.withType<KotlinMultiplatformAndroidLibraryTarget>().configureEach {
                namespace = project.group.toString()
                compileSdk = 37
                minSdk = 21

                compilerOptions {
                    jvmTarget = JvmTarget.JVM_11
                }
            }
        }
    }
}
