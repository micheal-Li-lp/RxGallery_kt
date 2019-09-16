package com.micheal.rxgallery.rxbus.event

class RequestStorageReadAccessPermissionEvent(val success:Boolean,val type :Int) {
    companion object{
        const val TYPE_CAMERA = 0
        const val TYPE_WRITE = 1
    }
}