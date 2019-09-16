package com.micheal.rxgallery.interactor

import com.micheal.rxgallery.entity.BucketEntity

interface MediaBucketFactoryInteractor {
    fun generateBuckets()

    interface OnGenerateBucketListener{
        fun onFinished(list: List<BucketEntity>?)
    }
}