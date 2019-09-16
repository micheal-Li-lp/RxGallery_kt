package com.micheal.rxgallery.view

import com.micheal.rxgallery.entity.MediaEntity

interface ActivityFragmentView {
    fun showMediaGridFragment()
    fun showMediaPageFragment(list: List<MediaEntity>,position:Int)
    fun showMediaPreviewFragment()
}