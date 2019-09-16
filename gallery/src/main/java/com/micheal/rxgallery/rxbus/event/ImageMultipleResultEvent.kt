package com.micheal.rxgallery.rxbus.event

import com.micheal.rxgallery.entity.MediaEntity

class ImageMultipleResultEvent(val mediaResultList:List<MediaEntity>?) : BaseResultEvent