plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.messageBroker.common)
            api(projects.serialization.core)

            implementation(kotlinx.coroutines)
        }
    }
}
