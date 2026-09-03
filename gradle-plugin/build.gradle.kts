
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.gradleKotlinDsl
import org.gradle.kotlin.dsl.`java-gradle-plugin`

plugins {
    alias(kt.plugins.jvm)
    `java-gradle-plugin`

    alias(libutils.plugins.buildKonfig)
}

buildkonfig {
    packageName = project.group.toString()
    objectName = "Artifacts"

    defaultConfigs {
        buildConfigField(STRING, "GROUP", project.group.toString(), const = true)
        buildConfigField(STRING, "VERSION", project.version.toString(), const = true)
    }
}

gradlePlugin {
    plugins {
        create(rootProject.name) {
            id = project.group.toString()
            implementationClass = "${project.group}.IntegrationEventsPlugin"
        }
    }
}

fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) {
    val artifact =
        plugin.get().let {
            "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
        }

    implementation(artifact)
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    plugin(kt.plugins.jvm)
    plugin(kt.plugins.multiplatform.asProvider())
    plugin(codegen.plugins.ksp)
}
