package ru.smityukh.joom2.ui.viewmodel

import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.smityukh.joom2.data.GiphyStore
import ru.smityukh.joom2.infra.ApplicationScope
import ru.smityukh.joom2.navigation.Navigation

@Module
class ViewModelModule {

    @Provides
    @ApplicationScope
    fun providesViewModelFactory(giphyStore: GiphyStore, navigation: Navigation): ViewModelProvider.Factory =
        JoomViewModelFactory(giphyStore, navigation)

    @Provides
    @ApplicationScope
    fun providesJoomViewModelProviders(factory: ViewModelProvider.Factory): JoomViewModelProviders =
        JoomViewModelProvidersImpl(factory)
}