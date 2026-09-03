package io.github.briangits.events.integration

import org.gradle.api.provider.Property

abstract class IntegrationEventsExtension {
    abstract val packageName: Property<String>
    abstract val loaderFunction: Property<String>
}
