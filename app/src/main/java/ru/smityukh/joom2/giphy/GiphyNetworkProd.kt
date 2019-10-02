package ru.smityukh.joom2.giphy

import android.util.Log
import ru.smityukh.joom2.data.GifInfo
import ru.smityukh.joom2.data.UserInfo
import ru.smityukh.joom2.giphy.api.GiphyApiGifObject
import ru.smityukh.joom2.giphy.api.GiphyApiJsonParser
import ru.smityukh.joom2.giphy.api.GiphyApiUserObject
import ru.smityukh.joom2.network.HttpClientFactory
import javax.net.ssl.HttpsURLConnection

class GiphyNetworkProd(
    private val params: GiphyParams,
    private val httpClientFactory: HttpClientFactory,
    private val jsonParser: GiphyApiJsonParser
) : GiphyNetwork {
    companion object {
        private const val TAG = "GiphyNetworkProd"

        private const val TRENDING_URL = "https://api.giphy.com/v1/gifs/trending"
        private const val GIF_BY_ID_URL = "https://api.giphy.com/v1/gifs/"
    }
    override fun loadTrending(offset: Int, count: Int): GiphyNetwork.PagedRequestResult<List<GifInfo>> {

        val client = httpClientFactory.create(TRENDING_URL)

        client.addUrlParam("api_key", params.apiKey)
        client.addUrlParam("offset", offset)
        client.addUrlParam("limit", count)

        try {
            client.executeRequest().use { requestResult ->
                if (requestResult.responseCode != HttpsURLConnection.HTTP_OK) {
                    return GiphyNetwork.PagedRequestResult.createFailed("HTTP error code: ${requestResult.responseCode}")
                }

                val data = jsonParser.readTrending(requestResult.stream)
                val gifs = data.data.map { it.toGifInfo() }.toList()

                return GiphyNetwork.PagedRequestResult.createSucceed(
                    gifs,
                    data.pagination.offset,
                    data.pagination.count,
                    data.pagination.totalCount
                )
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "client.executeRequest failed with exception", ex)
            return GiphyNetwork.PagedRequestResult.createFailed("Error: ${ex.message}")
        }
    }

    override fun loadGif(key: String): GiphyNetwork.RequestResult<GifInfo> {
        val client = httpClientFactory.create(GIF_BY_ID_URL + key)

        client.addUrlParam("api_key", params.apiKey)

        try {
            client.executeRequest().use { requestResult ->
                if (requestResult.responseCode != HttpsURLConnection.HTTP_OK) {
                    return GiphyNetwork.PagedRequestResult.createFailed("HTTP error code: ${requestResult.responseCode}")
                }

                val data = jsonParser.readGifById(requestResult.stream)
                val gif = data.data.toGifInfo()

                return GiphyNetwork.RequestResult.createSucceed(gif)
            }
        } catch (ex: Throwable) {
            Log.e(TAG, "client.executeRequest failed with exception", ex)
            return GiphyNetwork.RequestResult.createFailed("Error: ${ex.message}")
        }
    }

    private fun GiphyApiGifObject.toGifInfo(): GifInfo {
        return GifInfo(id, images.downsized.url, images.still480W?.url, user.toUserInfo())
    }

    private fun GiphyApiUserObject?.toUserInfo(): UserInfo {
        if (this == null) {
            return UserInfo.EMPTY
        }

        return UserInfo(username, display_name, profile_url, avatar_url)
    }
}