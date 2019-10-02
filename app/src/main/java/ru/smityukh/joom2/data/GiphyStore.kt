package ru.smityukh.joom2.data

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

interface GiphyStore {
    fun getTrendingGifs(): StoreData<PagedList<GifInfo>, StoreDataState>
    fun getGif(key: String): StoreData<GifInfo, StoreDataState>

    interface StoreData<TValue, TState> {
        val value: LiveData<TValue>
        val state: LiveData<TState>
        val error: LiveData<StoreDataError>

        fun reload()
    }

    enum class StoreDataState {
        UNDEFINED,
        LOADED,
        LOADING,
        ERROR
    }

    interface StoreDataError {
        val message: String

        val action: (() -> Unit)?
        val actionName: String?
    }
}