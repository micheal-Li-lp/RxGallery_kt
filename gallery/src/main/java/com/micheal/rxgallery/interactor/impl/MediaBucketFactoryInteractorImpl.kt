package com.micheal.rxgallery.interactor.impl

import android.content.Context
import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.interactor.MediaBucketFactoryInteractor
import com.micheal.rxgallery.utils.MediaUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class MediaBucketFactoryInteractorImpl(
    private val context: Context,private val  isImage:Boolean,
    private val onGenerateBucketListener: MediaBucketFactoryInteractor.OnGenerateBucketListener
) : MediaBucketFactoryInteractor {
    override fun generateBuckets() {
        Observable.create(ObservableOnSubscribe<List<BucketEntity>> {
            val list = if (isImage) {
                MediaUtils.getAllBucketByImage(context)
            } else {
                MediaUtils.getAllBucketByVideo(context)
            }
            it.onNext(list)
            it.onComplete()
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :DisposableObserver<List<BucketEntity>>(){
                override fun onComplete() {}

                override fun onNext(t: List<BucketEntity>) {
                    onGenerateBucketListener.onFinished(t)
                }

                override fun onError(e: Throwable) {
                    onGenerateBucketListener.onFinished(null)
                }

            })
    }
}