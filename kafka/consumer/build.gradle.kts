plugins {
    alias(kt.plugins.multiplatform)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.kafka.common)
            api(projects.relay.consumer)

            implementation(kotlinx.coroutines)
        }

        jvmMain.dependencies {
            api(kafka.clients)
        }

        commonTest.dependencies {
            implementation(kt.test)

            // Coroutines
            implementation(kotlinx.coroutines.test)
        }

        jvmTest.dependencies {
            // Test Containers
            implementation(testcontainers.core)
            implementation(testcontainers.kafka)
        }
    }
}
