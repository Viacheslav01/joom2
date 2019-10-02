package ru.smityukh.joom2.network

interface HttpClientFactory {
    fun create(url: String): HttpClient
}