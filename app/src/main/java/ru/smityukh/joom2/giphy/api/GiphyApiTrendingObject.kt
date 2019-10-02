package ru.smityukh.joom2.giphy.api

data class GiphyApiTrendingObject(
    val data: Array<GiphyApiGifObject>,
    val pagination: GiphyApiPaginationObject,
    val meta: GiphyApiMetaObject
)