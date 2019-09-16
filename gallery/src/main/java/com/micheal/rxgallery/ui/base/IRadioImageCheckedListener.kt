package com.micheal.rxgallery.ui.base

interface IRadioImageCheckedListener {

    /**
     * 裁剪之后
     */
    fun cropAfter(t:Any)

    /**
     * 返回true则关闭，false默认不关闭
     */
    fun isActivityFinished():Boolean
}