package ru.smityukh.joom2.navigation

interface NavigationHost {
    fun navigateToTrendingList(): Boolean
    fun navigateToDetails(gifId: String): Boolean
    fun openUrl(url: String): Boolean
}