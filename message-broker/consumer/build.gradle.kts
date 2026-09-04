plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.messageBroker.common)

            implementation(kotlinx.coroutines)
            implementation(kotlinx.serialization)
        }
    }
}
