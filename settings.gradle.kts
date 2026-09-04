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
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "integration-events"

include(":common")
include(":metadata")

include(":annotations")
include(":annotation-processor")
include(":gradle-plugin")

include(":message-broker:common")
include(":message-broker:publisher")
include(":message-broker:consumer")

include(":producer")