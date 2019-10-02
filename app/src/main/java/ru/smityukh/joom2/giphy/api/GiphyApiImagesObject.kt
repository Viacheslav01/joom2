package ru.smityukh.joom2.giphy.api

data class GiphyApiImagesObject(
    val downsized: GiphyApiRenditionObject,
    val still480W: GiphyApiRenditionObject?
)