package com.micheal.rxgallery.view

import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.entity.MediaEntity

interface MediaGridView {
    fun onRequestMediaCallback(list :List<MediaEntity>?)
    fun onRequestBucketCallback(list: List<BucketEntity>?)
}