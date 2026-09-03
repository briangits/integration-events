plugins {
    alias(kt.plugins.jvm)
}

dependencies {
    implementation(projects.common)
    implementation(projects.annotations)

    implementation(codegen.ksp)

    implementation(codegen.poet)
    implementation(codegen.poet.ksp)

    implementation(kotlinx.serialization)
}
