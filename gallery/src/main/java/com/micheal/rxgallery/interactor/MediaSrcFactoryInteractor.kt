package com.micheal.rxgallery.interactor

import com.micheal.rxgallery.entity.MediaEntity

/**
 * Desction:媒体资源工厂
 * Author:pengjianbo  Dujinyang
 * Date:16/5/14 上午11:06
 */
interface MediaSrcFactoryInteractor {

    /**
     * 生产资源
     */
    fun generateMedias(bucketId:String,page :Int,limit :Int)


    interface OnGenerateMediaListener{
        fun onFinished(bucketId :String?,pageSize :Int,currentOffset:Int,list: List<MediaEntity>?)
    }

}