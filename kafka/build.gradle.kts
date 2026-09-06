
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

subprojects {
    group = "${rootProject.group}.broker.kafka"

    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            jvm()
        }
    }
}
