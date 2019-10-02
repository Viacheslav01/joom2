package ru.smityukh.joom2.navigation

import dagger.Module
import dagger.Provides
import ru.smityukh.joom2.infra.ApplicationScope
import ru.smityukh.joom2.infra.Executors

@Module
class NavigationModule {

    @Provides
    @ApplicationScope
    fun providesNavigationDispatcher(executors: Executors): NavigationDispatcher = NavigationDispatcherProd(executors)

    @Provides
    @ApplicationScope
    fun providesNavigation(navigationDispatcher: NavigationDispatcher) = navigationDispatcher.getNavigation()

    @Provides
    @ApplicationScope
    fun providesDeeplinkHandler(navigation: Navigation): DeeplinkHandler = DeeplinkHandlerProd(navigation)
}