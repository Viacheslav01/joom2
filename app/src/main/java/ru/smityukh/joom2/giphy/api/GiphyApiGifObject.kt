package ru.smityukh.joom2.giphy.api

data class GiphyApiGifObject(
    val type: String,
    val id: String,
    val url: String?,
    val user: GiphyApiUserObject?,
    val create_datetime: String?,
    val images: GiphyApiImagesObject,
    val title: String
)