package ru.smityukh.joom2.infra

import dagger.Module
import dagger.Provides
import ru.smityukh.joom2.data.*
import ru.smityukh.joom2.giphy.api.GiphyApiJsonParser
import ru.smityukh.joom2.giphy.api.GiphyApiJsonParserProd
import ru.smityukh.joom2.giphy.GiphyParams
import ru.smityukh.joom2.giphy.GiphyParamsProd
import ru.smityukh.joom2.giphy.GiphyNetwork
import ru.smityukh.joom2.giphy.GiphyNetworkProd
import ru.smityukh.joom2.network.HttpClientFactory

@Module
class GiphyModule {

    @Provides
    @ApplicationScope
    fun providesGiphyParams(): GiphyParams = GiphyParamsProd()

    @Provides
    @ApplicationScope
    fun providesGiphyNetwork(
        giphyParams: GiphyParams,
        httpClientFactory: HttpClientFactory,
        jsonParser: GiphyApiJsonParser
    ): GiphyNetwork = GiphyNetworkProd(giphyParams, httpClientFactory, jsonParser)

    @Provides
    @ApplicationScope
    fun providesGipyStore(giphyNetwork: GiphyNetwork, giphyParams: GiphyParams, executors: Executors): GiphyStore =
        GiphyStoreProd(giphyNetwork, giphyParams, executors)

    @Provides
    @ApplicationScope
    fun providesGiphyApiJsonParser(): GiphyApiJsonParser = GiphyApiJsonParserProd()
}