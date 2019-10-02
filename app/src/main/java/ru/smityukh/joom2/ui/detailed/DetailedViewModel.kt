package ru.smityukh.joom2.ui.detailed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.smityukh.joom2.data.GifInfo
import ru.smityukh.joom2.data.GiphyStore
import ru.smityukh.joom2.map
import ru.smityukh.joom2.navigation.Navigation
import ru.smityukh.joom2.switchMap

class DetailedViewModel(private val giphyStore: GiphyStore, private val navigation: Navigation) : ViewModel() {

    private val gifId = MutableLiveData<String>()
    private val storeObject: LiveData<GiphyStore.StoreData<GifInfo, GiphyStore.StoreDataState>> = gifId.map { it?.run { giphyStore.getGif(it) } }

    val gif: LiveData<GifInfo> = storeObject.switchMap { it?.value }
    val isLoading: LiveData<Boolean> = storeObject.switchMap { it?.state }.map { it == GiphyStore.StoreDataState.LOADING }
    val error: LiveData<GiphyStore.StoreDataError> = storeObject.switchMap { it?.error }

    fun setSelectedGifId(gifId: String) {
        this.gifId.value = gifId
    }

    fun openProfile() {
        gif.value?.user?.profileUrl?.let {
            navigation.openUrl(it)
        }
    }
}