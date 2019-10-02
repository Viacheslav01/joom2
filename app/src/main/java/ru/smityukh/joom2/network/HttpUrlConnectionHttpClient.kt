package ru.smityukh.joom2.network

import android.util.Log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpUrlConnectionHttpClient(private var url: String) : HttpClient {

    companion object {
        private const val TAG = "HttpClient"

        private const val CONNECTION_TIMEOUT_MS = 15000
        private const val READ_TIMEOUT_MS = 60000
    }

    private val urlParams = mutableMapOf<String, Any>()

    override fun addUrlParam(name: String, value: Any) {
        urlParams[name] = value
    }

    override fun executeRequest(): HttpClient.RequestResult {
        var requestUrl = url
        if (!urlParams.isEmpty()) {
            requestUrl += urlParams.map { "${it.key}=${URLEncoder.encode(it.value.toString())}" }.joinToString("&", "?")
        }

        Log.d(TAG, "executeRequest(): requestUrl=[$requestUrl]")

        val connection = URL(requestUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.doInput = true
        connection.connectTimeout = CONNECTION_TIMEOUT_MS
        connection.readTimeout = READ_TIMEOUT_MS

        connection.connect()

        return RequestResult(connection)
    }

    private class RequestResult(private val connection: HttpURLConnection) : HttpClient.RequestResult {
        override val responseCode: Int
            get() = connection.responseCode

        override val stream: InputStream
            get() = connection.inputStream

        override fun close() {
            connection.disconnect()
        }
    }
}