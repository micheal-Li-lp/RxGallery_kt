package com.micheal.rxgallery.ui.fragment

import android.os.Bundle
import android.view.View
import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.view.MediaGridView

class MediaGridFragment :BaseFragment(), MediaGridView{
    override fun onRequestMediaCallback(list: List<MediaEntity>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequestBucketCallback(list: List<BucketEntity>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onViewCreatedOk(view: View, savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentView(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFirstTimeLaunched() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRestoreState(savedInstanceState: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSaveState(outState: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}