package com.micheal.rxgallery.rxbus.event

import com.micheal.rxgallery.entity.MediaEntity

class OpenMediaPageFragmentEvent(val list:List<MediaEntity>, val position :Int)