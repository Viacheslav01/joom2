package ru.smityukh.joom2.data

import androidx.lifecycle.MutableLiveData
import ru.smityukh.joom2.infra.Executors

abstract class StoreDataBase<TValue, TState>(private val executors: Executors) : GiphyStore.StoreData<TValue, TState> {
    override val value: MutableLiveData<TValue> = MutableLiveData()
    override val state: MutableLiveData<TState> = MutableLiveData()
    override val error: MutableLiveData<GiphyStore.StoreDataError> = MutableLiveData()

    protected fun setState(state: TState) {
        executors.mainThread.execute { this.state.value = state }
    }

    protected fun setValue(value: TValue?) {
        executors.mainThread.execute { this.value.value = value }
    }

    protected fun setError(error: GiphyStore.StoreDataError?) {
        executors.mainThread.execute { this.error.value = error }
    }
}