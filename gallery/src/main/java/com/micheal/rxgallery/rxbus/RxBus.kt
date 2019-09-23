package com.micheal.rxgallery.rxbus

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.HashMap

object RxBus {

    private val mBus: Subject<Any> = PublishSubject.create<Any>().toSerialized()
    private val mDisposable = CompositeDisposable()
    private val mStickyEventMap = HashMap<Class<*>, Any>()

    /**
     * 发送事件
     */
    @JvmStatic
    fun post(event: Any) {
        mBus.onNext(event)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    @JvmStatic
    fun <T> toObservable(eventType: Class<T>): Observable<T> {
        return mBus.ofType(eventType)
    }

    /**
     * 判断是否有订阅者
     */
    @JvmStatic
    fun hasObservers(): Boolean {
        return mBus.hasObservers()
    }

    /**
     * 是否被取消订阅
     */
    @JvmStatic
    fun isUnsubscribed(): Boolean {
        return mDisposable.isDisposed
    }

    /**
     * 添加订阅
     */
    @JvmStatic
    fun add(s: Disposable?) {
        if (s != null) {
            mDisposable.add(s)
        }
    }

    /**
     * 移除订阅
     */
    @JvmStatic
    fun remove(s: Disposable?) {
        if (s != null) {
            mDisposable.remove(s)
        }
    }

    /**
     * 清除所有订阅
     */
    @JvmStatic
    fun clear() {
        mDisposable.clear()
    }


    /**
     * 取消订阅
     */
    @JvmStatic
    fun unsubscribe() {
        mDisposable.dispose()
    }

    /**
     * 发送一个新Sticky事件
     */
    @JvmStatic
    fun postSticky(event: Any) {
        synchronized(mStickyEventMap) {
            mStickyEventMap.put(event.javaClass, event)
        }
        post(event)
    }

    /**
     * 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
     */
    @JvmStatic
    fun <T> toObservableSticky(eventType: Class<T>): Observable<T> {
        synchronized(mStickyEventMap) {
            val observable = mBus.ofType(eventType)
            val event = mStickyEventMap[eventType]

            return if (event != null) {
                Observable.merge(
                    observable,
                    Observable.create {
                            subscriber -> subscriber.onNext(eventType.cast(event)!!)
                    })
            } else {
                observable
            }
        }
    }

    /**
     * 根据eventType获取Sticky事件
     */
    @JvmStatic
    fun <T> getStickyEvent(eventType: Class<T>): T? {
        synchronized(mStickyEventMap) {
            return eventType.cast(mStickyEventMap[eventType])
        }
    }

    /**
     * 移除指定eventType的Sticky事件
     */
    @JvmStatic
    fun <T> removeStickyEvent(eventType: Class<T>): T? {
        synchronized(mStickyEventMap) {
            return eventType.cast(mStickyEventMap.remove(eventType))
        }
    }

    /**
     * 移除所有的Sticky事件
     */
    @JvmStatic
    fun removeAllStickyEvents() {
        synchronized(mStickyEventMap) {
            mStickyEventMap.clear()
        }
    }
}