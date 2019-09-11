package com.micheal.rxgallery.rxbus

import com.micheal.rxgallery.utils.Logger
import io.reactivex.observers.DisposableObserver

abstract class RxBusDisposable<T> :DisposableObserver<T>() {
    override fun onComplete() {}

    override fun onNext(t: T) {
        try {
            onEvent(t)
        }catch (e : Exception){
            e.printStackTrace()
            onError(e)
        }
    }

    override fun onError(e: Throwable) {
        Logger.e(e.message)
    }

    @Throws(Exception::class)
    protected abstract fun onEvent(t: T)
}