package com.micheal.rxgallery.ui

class RxGalleryListener {
    companion object{
        private class RxGalleryListenerHolder{
            companion object{
                val RX_GALLERY_LISTENER = RxGalleryListener()
            }
        }

        fun getInstance():RxGalleryListener {
            return RxGalleryListenerHolder.RX_GALLERY_LISTENER
        }

    }

}