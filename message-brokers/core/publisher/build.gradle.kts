plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.messageBrokers.core.common)

            implementation(kotlinx.serialization)
        }
    }
}
