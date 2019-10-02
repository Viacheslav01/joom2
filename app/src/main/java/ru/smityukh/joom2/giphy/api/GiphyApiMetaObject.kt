package ru.smityukh.joom2.giphy.api

/**
 * Этот класс используется с билдером, что позволит писать более простой код чтения объектов,
 * но довольно громоздко и уже больше походит на свою библиотеку персинга JSON.
 *
 * В развитии идеи гораздо практичне было бы дописать свой обработчик аннотаций с генерацией кода билдеров, для JSON классов.
 */
data class GiphyApiMetaObject(val status: Int, val message: String, val responseId: String) {
    @Suppress("unused")
    constructor(params: Map<String, Any?>) : this(params["status"] as Int, params["msg"] as String, params["response_id"] as String)
}