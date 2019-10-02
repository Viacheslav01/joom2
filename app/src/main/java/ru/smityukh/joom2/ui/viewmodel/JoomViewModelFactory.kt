package ru.smityukh.joom2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.smityukh.joom2.data.GiphyStore
import ru.smityukh.joom2.navigation.Navigation
import ru.smityukh.joom2.ui.detailed.DetailedFragment
import ru.smityukh.joom2.ui.detailed.DetailedViewModel
import ru.smityukh.joom2.ui.main.MainViewModel

@Suppress("UNCHECKED_CAST")
class JoomViewModelFactory(private val giphyStore: GiphyStore, private val navigation: Navigation) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when (modelClass) {
        MainViewModel::class.java -> MainViewModel(giphyStore, navigation) as T
        DetailedViewModel::class.java -> DetailedViewModel(giphyStore, navigation) as T
        else -> super.create(modelClass)
    }
}