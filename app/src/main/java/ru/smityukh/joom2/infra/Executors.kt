package ru.smityukh.joom2.infra

import java.util.concurrent.Executor

interface Executors {
    val mainThread: Executor
    val io: Executor
}