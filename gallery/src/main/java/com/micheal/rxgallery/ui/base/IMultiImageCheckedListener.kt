package com.micheal.rxgallery.ui.base

interface IMultiImageCheckedListener {
    fun selectedImg(t :Any , isChecked:Boolean)

    fun selectedImgMax(t:Any,isChecked: Boolean,maxSize:Int)
}