package io.github.briangits.events.integration.producer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val defaultDispatcher: kotlinx.coroutines.CoroutineDispatcher =
    Dispatchers.IO
