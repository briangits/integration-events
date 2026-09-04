plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.messageBrokers.core.common)

            implementation(kotlinx.serialization)
        }
    }
}
