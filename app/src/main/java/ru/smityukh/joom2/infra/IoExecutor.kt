package ru.smityukh.joom2.infra

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class IoExecutor :
    ThreadPoolExecutor(
        THREAD_POOL_SIZE,
        THREAD_POOL_SIZE,
        KEEP_ALIVE_SECONDS,
        TimeUnit.SECONDS,
        LinkedBlockingQueue<Runnable>(),
        NamedThreadFactory()
    ) {

    companion object {
        private const val THREAD_POOL_SIZE = 4
        private const val KEEP_ALIVE_SECONDS = 60L
    }

    init {
        allowCoreThreadTimeOut(true)
    }

    private class NamedThreadFactory : ThreadFactory {

        private val counter = AtomicInteger(0)

        override fun newThread(runnable: Runnable): Thread = Thread(runnable).apply {
            name = createThreadName()
            priority = Thread.NORM_PRIORITY
        }

        private fun createThreadName() = "IO-Executor-" + counter.getAndIncrement()
    }
}