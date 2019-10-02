package ru.smityukh.joom2.data

import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import ru.smityukh.joom2.giphy.GifInfoObservableNetworkWrapper
import ru.smityukh.joom2.giphy.GiphyNetwork
import ru.smityukh.joom2.giphy.GiphyParams
import ru.smityukh.joom2.infra.Executors
import ru.smityukh.joom2.map
import ru.smityukh.joom2.switchMap

class GiphyStoreProd(
    network: GiphyNetwork,
    private val params: GiphyParams,
    private val executors: Executors
) : GiphyStore {

    // Кеш не будет очищаться, что может быть проблеммой на больших наборах данных.
    // По хорошему нужна реализация с сохранением на диск и вытеснением из памяти не используемых элементов.
    // Кроме этого необходима потокобезопасная коллекция сечас выполняется принудительная синхронизация.
    private val allGifs: MutableMap<String, GifInfo> = hashMapOf()

    private val networkWrapper = GifInfoObservableNetworkWrapper(network, this::onGifInfoLoaded)

    private val trendingStoreData by lazy { TrendingStoreObject() }
    override fun getTrendingGifs(): GiphyStore.StoreData<PagedList<GifInfo>, GiphyStore.StoreDataState> {
        return trendingStoreData
    }

    override fun getGif(key: String): GiphyStore.StoreData<GifInfo, GiphyStore.StoreDataState> {
        return GifStoreData(key).apply {
            reload()
        }
    }

    private fun onGifInfoLoaded(gifInfo: GifInfo) {
        synchronized(allGifs) {
            if (!allGifs.containsKey(gifInfo.key)) {
                allGifs[gifInfo.key] = gifInfo
            }
        }
    }

    private inner class TrendingStoreObject : GiphyStore.StoreData<PagedList<GifInfo>, GiphyStore.StoreDataState> {

        override val value: LiveData<PagedList<GifInfo>>
        override val state: LiveData<GiphyStore.StoreDataState>
        override val error: LiveData<GiphyStore.StoreDataError>

        private val lastDataSource: LiveData<TrendingGifsDataSource>

        init {
            val dataSourceFactory = object : DataSource.Factory<Int, GifInfo>() {
                override fun create(): DataSource<Int, GifInfo> {
                    return TrendingGifsDataSource(networkWrapper, executors)
                }
            }

            value = dataSourceFactory.toLiveData(params.trendingPageSize)
            lastDataSource = value.map { it?.dataSource as TrendingGifsDataSource? }

            val dataSourceState = lastDataSource.switchMap { it?.state }

            state = dataSourceState.map {
                when (it) {
                    DataSourceState.INITIAL_LOADING -> GiphyStore.StoreDataState.LOADING
                    DataSourceState.ERROR -> GiphyStore.StoreDataState.ERROR
                    else -> GiphyStore.StoreDataState.UNDEFINED
                }
            }

            error = dataSourceState.map {
                if (it != DataSourceState.ERROR) {
                    return@map null
                }

                return@map object : GiphyStore.StoreDataError {
                    override val message = "ВОЗНИКЛА ОШИБКА ВО ВРЕМЯ ЗАГРУЗКИ"
                    override val action = null
                    override val actionName = null
                }
            }
        }

        override fun reload() {
            lastDataSource.value?.invalidate()
        }
    }

    private inner class GifStoreData(private val key: String) : StoreDataBase<GifInfo, GiphyStore.StoreDataState>(executors) {

        init {
            setState(GiphyStore.StoreDataState.UNDEFINED)
        }

        override fun reload() {
            setState(GiphyStore.StoreDataState.LOADING)
            setError(null)

            synchronized(allGifs) {
                val gifInfo = allGifs[key]
                if (gifInfo != null) {
                    setState(GiphyStore.StoreDataState.LOADED)
                    setValue(gifInfo)
                    return
                }
            }

            executors.io.execute {
                Thread.sleep(1500)

                val result = networkWrapper.loadGif(key)

                if (result.isSuccess) {
                    setState(GiphyStore.StoreDataState.LOADED)
                    setValue(result.value)
                } else {
                    setState(GiphyStore.StoreDataState.ERROR)
                    setError(object : GiphyStore.StoreDataError {
                        override val message = "ВОЗНИКЛА ОШИБКА ВО ВРЕМЯ ЗАГРУЗКИ"
                        override val action = { reload() }
                        override val actionName = "ПОВТОРИТЬ"
                    })
                }
            }
        }
    }
}