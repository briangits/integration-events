pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google() }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        google()
    }

    versionCatalogs {
        create("kt").from(files("version-catalogs/kotlin.versions.toml"))
        create("kotlinx").from(files("version-catalogs/kotlinx.versions.toml"))
        create("codegen").from(files("version-catalogs/codegen.versions.toml"))
        create("libutils").from(files("version-catalogs/libutils.versions.toml"))
        create("kafka").from(files("version-catalogs/kafka.versions.toml"))
        create("testcontainers").from(files("version-catalogs/testcontainers.versions.toml"))
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "integration-events"

include(":common")
include(":metadata")

include(":annotations")
include(":annotation-processor")
include(":gradle-plugin")

include(":relay")
include(":relay:common")
include(":relay:publisher")
include(":relay:consumer")

include(":kafka")
include(":kafka:common")
include(":kafka:publisher")
include(":kafka:consumer")

include(":serialization")
include(":serialization:core")
include(":serialization:json")

include(":producer")
include(":consumer")

include(":test")
