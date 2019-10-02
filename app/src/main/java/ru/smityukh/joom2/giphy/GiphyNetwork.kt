package ru.smityukh.joom2.giphy

import ru.smityukh.joom2.data.GifInfo

interface GiphyNetwork {
    fun loadTrending(offset: Int, count: Int): PagedRequestResult<List<GifInfo>>
    fun loadGif(key: String): RequestResult<GifInfo>

    interface RequestResult<T> {
        val isSuccess: Boolean
        val error: String?
        val value: T

        companion object {
            fun <T> createFailed(error: String) = object : RequestResult<T> {
                override val isSuccess: Boolean = false
                override val error: String? = error
                override val value: T get() = throw IllegalStateException("Failed result can't provide a value")
            }

            fun <T> createSucceed(value: T) = object : RequestResult<T> {
                override val isSuccess: Boolean = true
                override val error: String? = null
                override val value: T = value
            }
        }
    }

    interface PagedRequestResult<T> : RequestResult<T> {
        val offset: Int
        val count: Int
        val totalCount: Int

        companion object {
            fun <T> createFailed(error: String) = object : PagedRequestResult<T> {
                override val isSuccess: Boolean = false
                override val error: String? = error
                override val offset: Int = 0
                override val count: Int = 0
                override val totalCount: Int = 0
                override val value: T get() = throw IllegalStateException("Failed result can't provide a value")
            }

            fun <T> createSucceed(value: T, offset: Int, count: Int, totalCount: Int) = object : PagedRequestResult<T> {
                override val isSuccess: Boolean get() = true
                override val error: String? = null
                override val value: T = value
                override val offset: Int = offset
                override val count: Int = count
                override val totalCount: Int = totalCount
            }
        }
    }
}