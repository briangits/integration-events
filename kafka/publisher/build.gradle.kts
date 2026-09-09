plugins {
    alias(kt.plugins.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.kafka.common)
            api(projects.relay.publisher)

            implementation(kotlinx.coroutines)
        }

        jvmMain.dependencies {
            api(kafka.clients)
        }
    }
}
