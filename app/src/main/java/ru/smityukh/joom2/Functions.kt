package ru.smityukh.joom2

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <T> (T?).ifNull(action: () -> Unit): T? {
    if (this == null) {
        action()
    }
    return this
}

fun <T> (T?).ifNotNull(action: (T) -> Unit): T? {
    if (this != null) {
        action(this)
    }
    return this
}

fun <T, R> LiveData<T>.map(mapFunction: (T?) -> R?): LiveData<R> {
    return Transformations.map(this, mapFunction)
}

fun <T, R> LiveData<T>.switchMap(mapFunction: (T?) -> LiveData<R>?): LiveData<R> {
    return Transformations.switchMap(this, mapFunction)
}