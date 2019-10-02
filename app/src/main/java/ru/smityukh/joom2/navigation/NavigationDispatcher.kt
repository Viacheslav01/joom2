package ru.smityukh.joom2.navigation

import androidx.lifecycle.LifecycleOwner

interface NavigationDispatcher {
    fun getNavigation(): Navigation

    fun addNavigationHost(host: NavigationHost)
    fun addNavigationHost(lifecycleOwner: LifecycleOwner, host: NavigationHost)
    fun removeNavigationHost(host: NavigationHost)
}