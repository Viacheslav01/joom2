package ru.smityukh.joom2.giphy.api

import android.util.JsonReader
import java.io.InputStream
import java.io.InputStreamReader

class GiphyApiJsonParserProd : GiphyApiJsonParser {

    override fun readTrending(inputStrem: InputStream): GiphyApiTrendingObject {
        InputStreamReader(inputStrem).use {
            JsonReader(it).use {
                return readGiphyTrendingObject(it)
            }
        }
    }

    override fun readGifById(inputStrem: InputStream): GiphyApiGifByIdObject {
        InputStreamReader(inputStrem).use {
            JsonReader(it).use {
                return readGiphyApiGifByIdObject(it)
            }
        }
    }

    private fun readGiphyApiGifByIdObject(json: JsonReader): GiphyApiGifByIdObject {
        json.beginObject()

        var data: GiphyApiGifObject? = null
        var meta: GiphyApiMetaObject? = null

        while (json.hasNext()) {
            val fieldName = json.nextName()
            when (fieldName) {
                "data" -> data = readGiphyGifObject(json)
                "meta" -> meta = readGiphyMetaObject(json)
            }
        }

        json.endObject()

        return GiphyApiGifByIdObject(data!!, meta!!)
    }

    private fun readGiphyTrendingObject(json: JsonReader): GiphyApiTrendingObject {
        json.beginObject()

        var data: Array<GiphyApiGifObject>? = null
        var meta: GiphyApiMetaObject? = null
        var pagination: GiphyApiPaginationObject? = null

        while (json.hasNext()) {
            val fieldName = json.nextName()
            when (fieldName) {
                "data" -> data = readArrayOfGiphyGifObject(json)
                "pagination" -> pagination = readGiphyPaginationObject(json)
                "meta" -> meta = readGiphyMetaObject(json)
            }
        }

        json.endObject()

        return GiphyApiTrendingObject(data!!, pagination!!, meta!!)
    }

    private fun readArrayOfGiphyGifObject(json: JsonReader): Array<GiphyApiGifObject> {
        json.beginArray()

        val list = mutableListOf<GiphyApiGifObject>()

        while (json.hasNext()) {
            list.add(readGiphyGifObject(json))
        }

        json.endArray()

        return list.toTypedArray()
    }

    private fun readGiphyGifObject(json: JsonReader): GiphyApiGifObject {
        json.beginObject()

        var type: String? = null
        var id: String? = null
        var url: String? = null
        var user: GiphyApiUserObject? = null
        var createDatetime: String? = null
        var images: GiphyApiImagesObject? = null
        var title: String? = null

        while (json.hasNext()) {
            when (json.nextName()) {
                "type" -> type = json.nextString()
                "id" -> id = json.nextString()
                "url" -> url = json.nextString()
                "user" -> user = readGiphyUserObject(json)
                "create_datetime" -> createDatetime = json.nextString()
                "images" -> images = readGiphyImagesObject(json)
                "title" -> title = json.nextString()
                else -> json.skipValue()
            }
        }

        json.endObject()

        return GiphyApiGifObject(type!!, id!!, url, user, createDatetime, images!!, title!!)
    }

    private fun readGiphyUserObject(json: JsonReader): GiphyApiUserObject {
        json.beginObject()

        var avatarUrl: String? = null
        var bannerUrl: String? = null
        var profileUrl: String? = null
        var username: String? = null
        var displayName: String? = null

        while (json.hasNext()) {
            when (json.nextName()) {
                "avatar_url" -> avatarUrl = json.nextString()
                "banner_url" -> bannerUrl = json.nextString()
                "profile_url" -> profileUrl = json.nextString()
                "username" -> username = json.nextString()
                "display_name" -> displayName = json.nextString()
                else -> json.skipValue()
            }
        }

        json.endObject()

        return GiphyApiUserObject(avatarUrl!!, bannerUrl!!, profileUrl!!, username!!, displayName!!)
    }

    private fun readGiphyImagesObject(json: JsonReader): GiphyApiImagesObject {
        json.beginObject()

        var downsized: GiphyApiRenditionObject? = null
        var still480W: GiphyApiRenditionObject? = null

        while (json.hasNext()) {
            when (json.nextName()) {
                "downsized" -> downsized = readGiphyRenditionObject(json)
                "480w_still" -> still480W = readGiphyRenditionObject(json)
                else -> json.skipValue()
            }
        }

        json.endObject()

        return GiphyApiImagesObject(downsized!!, still480W)
    }

    private fun readGiphyRenditionObject(json: JsonReader): GiphyApiRenditionObject {
        json.beginObject()

        var url: String? = null
        var width: String? = null
        var height: String? = null
        var size: String? = null

        while (json.hasNext()) {
            when (json.nextName()) {
                "url" -> url = json.nextString()
                "width" -> width = json.nextString()
                "height" -> height = json.nextString()
                "size" -> size = json.nextString()
                else -> json.skipValue()
            }
        }

        json.endObject()

        return GiphyApiRenditionObject(url!!, width, height, size)
    }

    private fun readGiphyPaginationObject(json: JsonReader): GiphyApiPaginationObject {
        json.beginObject()

        var offset: Int? = null
        var count: Int? = null
        var totalCount: Int? = null

        while (json.hasNext()) {
            when (json.nextName()) {
                "offset" -> offset = json.nextInt()
                "total_count" -> totalCount = json.nextInt()
                "count" -> count = json.nextInt()
                else -> json.skipValue()
            }
        }

        json.endObject()

        return GiphyApiPaginationObject(offset!!, count!!, totalCount!!)
    }

    private fun readGiphyMetaObject(json: JsonReader): GiphyApiMetaObject {
        json.beginObject()

        val builder = builder<GiphyApiMetaObject>()

        while (json.hasNext()) {
            val fieldName = json.nextName()
            when (fieldName) {
                "msg" -> builder.setField(fieldName, json.nextString())
                "status" -> builder.setField(fieldName, json.nextInt())
                "response_id" -> builder.setField(fieldName, json.nextString())
                else -> json.skipValue()
            }
        }

        json.endObject()

        return builder.build()
    }

    private inline fun <reified T> builder(): Builder<T> {
        return Builder(T::class.java)
    }

    private class Builder<T>(private val clz: Class<T>) {

        private val values: MutableMap<String, Any> = mutableMapOf()

        fun setField(name: String, value: Any) {
            values[name] = value
        }

        fun build(): T {
            val constructor = clz.getConstructor(Map::class.java)
            return constructor.newInstance(values)
        }
    }
}