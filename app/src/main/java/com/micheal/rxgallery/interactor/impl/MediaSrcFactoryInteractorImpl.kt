package com.micheal.rxgallery.interactor.impl

import android.content.Context
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.interactor.MediaSrcFactoryInteractor
import com.micheal.rxgallery.utils.MediaUtils
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers

class MediaSrcFactoryInteractorImpl(
    private val context: Context,private val onGenerateMediaListener: MediaSrcFactoryInteractor.OnGenerateMediaListener,
    private val isImage :Boolean
)
    : MediaSrcFactoryInteractor {
    override fun generateMedias(bucketId: String, page: Int, limit: Int) {
        Observable.create(ObservableOnSubscribe<List<MediaEntity>> {

            val mediaBeanList :List<MediaEntity> = if (isImage) {
                MediaUtils.getMediaWithImageList(context, bucketId, page, limit)
            } else {
                MediaUtils.getMediaWithVideoList(context, bucketId, page, limit)
            }
            it.onNext(mediaBeanList)
            it.onComplete()
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object :DisposableObserver<List<MediaEntity>>(){
                override fun onComplete() {

                }

                override fun onNext(t: List<MediaEntity>) {
                    onGenerateMediaListener.onFinished(bucketId, page, limit, t)
                }

                override fun onError(e: Throwable) {
                    onGenerateMediaListener.onFinished(bucketId, page, limit, null)
                }

            })

    }
}