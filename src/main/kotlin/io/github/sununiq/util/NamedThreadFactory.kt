package io.github.sununiq.util

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.LongAdder

/**
 * 定制线程名
 */
class NamedThreadFactory constructor(private val prefix: String, private val isDaemon: Boolean = false) : ThreadFactory {
    private val threadNumber = LongAdder()

    override fun newThread(runnable: Runnable): Thread {
        this.threadNumber.increment()
        val t = Thread(runnable, prefix + "@thread-" + threadNumber.toInt())
        if (isDaemon) {
            t.isDaemon = true
        }
        return t
    }
}