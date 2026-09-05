package io.github.briangits.events.integration.producer

import kotlinx.coroutines.Dispatchers

internal actual val defaultDispatcher: kotlinx.coroutines.CoroutineDispatcher =
    Dispatchers.IO
