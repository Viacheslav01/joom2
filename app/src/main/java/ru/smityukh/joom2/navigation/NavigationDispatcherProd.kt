package ru.smityukh.joom2.navigation

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import ru.smityukh.joom2.infra.Executors

class NavigationDispatcherProd(private val executors: Executors) : NavigationDispatcher {

    private val navigationInstance: Navigation by lazy {
        NavigationInstance()
    }

    private val hosts = mutableMapOf<NavigationHost, StatefulHost>()

    override fun getNavigation(): Navigation = navigationInstance

    @MainThread
    override fun addNavigationHost(host: NavigationHost) {
        if (hosts.contains(host)) {
            return
        }

        val statefulHost = StatefulHost(host, HostState.ACTIVE)
        hosts[host] = statefulHost
    }

    @MainThread
    override fun addNavigationHost(lifecycleOwner: LifecycleOwner, host: NavigationHost) {
        if (hosts.contains(host)) {
            return
        }

        val lifecycle = lifecycleOwner.lifecycle
        val state = if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) HostState.ACTIVE else HostState.INACTIVE

        val statefulHost = StatefulHost(host, state)
        hosts[host] = statefulHost

        val observer = Observer(statefulHost)
        lifecycle.addObserver(observer)
    }

    @MainThread
    override fun removeNavigationHost(host: NavigationHost) {
        hosts.remove(host)
    }

    @MainThread
    private fun dispatchNavigation(handler: NavigationHost.() -> Boolean) {
        for (statefulHost in hosts.values) {
            if (statefulHost.hostState != HostState.ACTIVE) {
                continue
            }

            if (handler(statefulHost.host)) {
                return
            }
        }
    }

    private class StatefulHost(val host: NavigationHost, var hostState: HostState)

    private enum class HostState {
        ACTIVE,
        INACTIVE
    }

    private inner class NavigationInstance : Navigation {
        override fun navigateToTrendingList() {
            executors.mainThread.execute { dispatchNavigation { navigateToTrendingList() } }
        }

        override fun openUrl(url: String) {
            executors.mainThread.execute { dispatchNavigation { openUrl(url) } }
        }

        override fun navigateToDetails(gifId: String) {
            executors.mainThread.execute { dispatchNavigation { navigateToDetails(gifId) } }
        }
    }

    private inner class Observer(private val hostWithState: StatefulHost) : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun onResume(owner: LifecycleOwner) {
            hostWithState.hostState = HostState.ACTIVE
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun onPause(owner: LifecycleOwner) {
            hostWithState.hostState = HostState.INACTIVE
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(owner: LifecycleOwner) {
            hosts.remove(hostWithState.host)
        }
    }
}