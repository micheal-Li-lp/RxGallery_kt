package com.micheal.rxgallery.ui.activity

import android.os.Bundle
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.ui.fragment.MediaGridFragment
import com.micheal.rxgallery.ui.fragment.MediaPageFragment
import com.micheal.rxgallery.ui.fragment.MediaPreviewFragment
import com.micheal.rxgallery.view.ActivityFragmentView

class MediaActivity :BaseActivity(),ActivityFragmentView{
    companion object{
        const val REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101
        const val REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102
        const val REQUEST_CAMERA_ACCESS_PERMISSION = 103

        private const val EXTRA_CHECKED_LIST = "$EXTRA_PREFIX.CheckedList"
        private const val EXTRA_SELECTED_INDEX = "$EXTRA_PREFIX.SelectedIndex"
        private const val EXTRA_PAGE_MEDIA_LIST = "$EXTRA_PREFIX.PageMediaList"
        private const val EXTRA_PAGE_POSITION = "$EXTRA_PREFIX.PagePosition"
        private const val EXTRA_PREVIEW_POSITION = "$EXTRA_PREFIX.PreviewPosition"
    }

    private var mMediaGridFragment: MediaGridFragment? = null
    private var mMediaPageFragment: MediaPageFragment? = null
    private var mMediaPreviewFragment: MediaPreviewFragment? = null

    override fun getContentView() = R.layout.gallery_activity_media

    override fun onCreateOk(savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findViews() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setTheme() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMediaGridFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMediaPageFragment(list: List<MediaEntity>, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showMediaPreviewFragment() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}