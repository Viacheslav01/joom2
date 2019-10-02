package ru.smityukh.joom2.navigation

import android.net.Uri

interface DeeplinkHandler {
    fun handle(uri: Uri): Boolean
}

class DeeplinkHandlerProd(private val navigation: Navigation) : DeeplinkHandler {

    private val httpExpression = Regex("^https?://giphy.com/gifs/(\\w+)\\s*$")
    private val joomExpression = Regex("^joom://gifs/(\\w+)\\s*$")

    override fun handle(uri: Uri): Boolean = when (uri.scheme) {
        "joom" -> handleHttp(uri)
        "http", "https" -> handleJoom(uri)
        else -> false
    }

    private fun handleHttp(uri: Uri): Boolean = handleCommon(uri, joomExpression)

    private fun handleJoom(uri: Uri): Boolean = handleCommon(uri, httpExpression)

    private fun handleCommon(uri: Uri, expression: Regex): Boolean {
        val result = expression.find(uri.toString())
        if (result == null) {
            return false
        }

        navigation.navigateToDetails(result.groupValues[1])

        return true
    }
}