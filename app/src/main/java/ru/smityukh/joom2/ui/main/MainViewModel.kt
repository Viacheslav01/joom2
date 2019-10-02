package ru.smityukh.joom2.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import ru.smityukh.joom2.data.GifInfo
import ru.smityukh.joom2.data.GiphyStore
import ru.smityukh.joom2.map
import ru.smityukh.joom2.navigation.Navigation

class MainViewModel(private val giphyStore: GiphyStore, private val navigation: Navigation) : ViewModel() {

    private val trending: GiphyStore.StoreData<PagedList<GifInfo>, GiphyStore.StoreDataState> by lazy {
        giphyStore.getTrendingGifs()
    }

    val trendingGifs: LiveData<PagedList<GifInfo>>
        get() = trending.value

    val isTrendingGifsLoadeing: LiveData<Boolean> by lazy {
        trending.state.map { it == GiphyStore.StoreDataState.LOADING }
    }

    val error: LiveData<GiphyStore.StoreDataError> by lazy {
        trending.error
    }

    fun reloadTrendingGifs() {
        trending.reload()
    }

    fun selectGif(gifInfo: GifInfo) {
        navigation.navigateToDetails(gifInfo.key)
    }
}