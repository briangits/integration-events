plugins {
    alias(kt.plugins.multiplatform)
    alias(kt.plugins.multiplatform.android)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(kotlinx.serialization)
        }
    }
}
