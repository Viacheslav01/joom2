package ru.smityukh.joom2.network

class HttpClientFactoryProd : HttpClientFactory {
    override fun create(url: String): HttpClient = HttpUrlConnectionHttpClient(url)
}