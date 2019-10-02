package ru.smityukh.joom2.navigation

interface Navigation {
    fun navigateToTrendingList()
    fun navigateToDetails(gifId: String)
    fun openUrl(url: String)
}