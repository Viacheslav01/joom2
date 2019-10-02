package ru.smityukh.joom2.infra

import java.util.concurrent.Executor

class ExecutorsProd : Executors {
    override val io: Executor by lazy { IoExecutor() }
    override val mainThread: Executor by lazy { MainThreadExecutor() }
}