package com.micheal.rxgallery.ui

import com.micheal.rxgallery.ui.adapter.MediaGridAdapter
import com.micheal.rxgallery.ui.base.IMultiImageCheckedListener
import com.micheal.rxgallery.ui.base.IRadioImageCheckedListener
import com.micheal.rxgallery.ui.fragment.MediaGridFragment

class RxGalleryListener {
    companion object{
        private class RxGalleryListenerHolder{
            companion object{
                val RX_GALLERY_LISTENER = RxGalleryListener()
            }
        }

        @JvmStatic
        fun getInstance():RxGalleryListener {
            return RxGalleryListenerHolder.RX_GALLERY_LISTENER
        }

    }

    /**
     * 图片多选的事件
     */
    fun setMultiImageCheckedListener(checkedImageListener: IMultiImageCheckedListener) {
        MediaGridAdapter.iMultiImageCheckedListener = checkedImageListener
    }


    /**
     * 图片单选的事件
     */
    fun setRadioImageCheckedListener(checkedImageListener: IRadioImageCheckedListener) {
        MediaGridFragment.iListenerRadio = checkedImageListener
    }

}