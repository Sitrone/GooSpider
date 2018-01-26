package io.github.sununiq.util

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.LongAdder

/**
 * 定制线程名
 */
class NamedThreadFactory constructor(private val prefix: String) : ThreadFactory {
    private val threadNumber = LongAdder()

    override fun newThread(runnable: Runnable): Thread {
        this.threadNumber.increment()
        return Thread(runnable, prefix + "@thread-" + threadNumber.toInt())
    }
}