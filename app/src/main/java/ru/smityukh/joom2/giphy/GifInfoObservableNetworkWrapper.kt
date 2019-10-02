package ru.smityukh.joom2.giphy

import ru.smityukh.joom2.data.GifInfo

class GifInfoObservableNetworkWrapper(private val network: GiphyNetwork, private val observer: (GifInfo) -> Unit) :
    GiphyNetwork {

    override fun loadTrending(offset: Int, count: Int): GiphyNetwork.PagedRequestResult<List<GifInfo>> {
        val trending = network.loadTrending(offset, count)

        if (trending.isSuccess) {
            trending.value.forEach(observer)
        }

        return trending
    }

    override fun loadGif(key: String): GiphyNetwork.RequestResult<GifInfo> {
        val result = network.loadGif(key)

        if (result.isSuccess) {
            observer(result.value)
        }

        return result
    }
}