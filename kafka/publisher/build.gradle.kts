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

        commonTest.dependencies {
            implementation(kt.test)

            // Coroutines
            implementation(kotlinx.coroutines.test)
        }

        jvmMain.dependencies {
            api(kafka.clients)
        }

        jvmTest.dependencies {
            implementation(testcontainers.core)
            implementation(testcontainers.kafka)
        }
    }
}
