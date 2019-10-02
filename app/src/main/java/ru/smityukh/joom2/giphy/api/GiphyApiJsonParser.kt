package ru.smityukh.joom2.giphy.api

import java.io.InputStream

/*
Парсер ответов API Giphy.

Объекты сокращены, т.к. многие данные из ответа API не нужны в рамках тестового задания.
В то же время сокращены не все поля, финальный список это комбинация того, что нужно по заданию и того, что может понадобиться мне.
Привести объекты как в полное состояние, так и сокращенное не сложно.

Вся эта радость могла быть реализована с помошью любой из библиотек парсинга JSON, например моши, что было бы гораздо компактнее.
 */

interface GiphyApiJsonParser {
    fun readTrending(inputStrem: InputStream): GiphyApiTrendingObject
    fun readGifById(inputStrem: InputStream): GiphyApiGifByIdObject
}