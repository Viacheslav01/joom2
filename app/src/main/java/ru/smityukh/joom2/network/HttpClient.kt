package ru.smityukh.joom2.network

import java.io.Closeable
import java.io.InputStream

interface HttpClient {
    fun addUrlParam(name: String, value: Any)
    fun executeRequest(): RequestResult

    interface RequestResult : Closeable {
        val responseCode: Int
        val stream: InputStream
    }
}