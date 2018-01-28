package io.github.sununiq.spider

import java.util.concurrent.atomic.AtomicBoolean

object SpiderState {
    val isRunning : AtomicBoolean = AtomicBoolean(true)
}