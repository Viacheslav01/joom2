package ru.smityukh.joom2.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import ru.smityukh.joom2.giphy.GiphyNetwork
import ru.smityukh.joom2.infra.Executors
import java.time.Year
import kotlin.concurrent.thread

class TrendingGifsDataSource(private val network: GiphyNetwork, private val executors: Executors) : PositionalDataSource<GifInfo>() {

    private val stateInternal = MutableLiveData<DataSourceState>()
    val state: LiveData<DataSourceState> = stateInternal

    init {
        setState(DataSourceState.INACTIVE)
    }

    private fun setState(state: DataSourceState) {
        executors.mainThread.execute { stateInternal.value = state }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<GifInfo>) {
        // Реализвация довольно странная но вызвана тем, что PositionalDataSource требует размер списка должен быть кратным размеру страницы
        // В то же время, за время разработки встречал случаи когда Giphy возвращает список короче требуемого

        executors.io.execute {
            setState(DataSourceState.RANGE_LOADING)

            val list = ArrayList<GifInfo>(params.loadSize)

            while (list.size < params.loadSize) {
                val result = network.loadTrending(params.startPosition + list.size, params.loadSize - list.size)

                if (!result.isSuccess) {
                    setState(DataSourceState.ERROR)
                    return@execute
                }

                list.addAll(result.value)
            }

            if (list.size > params.loadSize) {
                for (index in list.size - 1 downTo params.loadSize) {
                    list.removeAt(index)
                }
            }

            callback.onResult(list)
            setState(DataSourceState.INACTIVE)
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<GifInfo>) {
        // Реализвация довольно странная но вызвана тем, что PositionalDataSource требует размер списка должен быть кратным размеру страницы
        // В то же время, за время разработки встречал случаи когда Giphy возвращает список короче требуемого

        executors.io.execute {
            setState(DataSourceState.INITIAL_LOADING)

            val list = ArrayList<GifInfo>(params.requestedLoadSize)
            var totalCount = 0

            while (list.size < params.requestedLoadSize) {
                val result = network.loadTrending(params.requestedStartPosition + list.size, params.requestedLoadSize - list.size)

                if (!result.isSuccess) {
                    setState(DataSourceState.ERROR)
                    return@execute
                }

                list.addAll(result.value)
                totalCount = result.totalCount
            }

            if (list.size > params.requestedLoadSize) {
                for (index in list.size - 1 downTo params.requestedLoadSize) {
                    list.removeAt(index)
                }
            }

            callback.onResult(list, params.requestedStartPosition, totalCount)
            setState(DataSourceState.INACTIVE)
        }
    }
}