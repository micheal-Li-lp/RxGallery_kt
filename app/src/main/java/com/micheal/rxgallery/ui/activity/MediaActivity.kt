package com.micheal.rxgallery.ui.activity

import android.os.Bundle
import android.view.View
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.rxbus.RxBus
import com.micheal.rxgallery.rxbus.event.ImageMultipleResultEvent
import com.micheal.rxgallery.ui.fragment.MediaGridFragment
import com.micheal.rxgallery.ui.fragment.MediaPageFragment
import com.micheal.rxgallery.ui.fragment.MediaPreviewFragment
import com.micheal.rxgallery.view.ActivityFragmentView
import kotlinx.android.synthetic.main.gallery_activity_media.*
import java.util.ArrayList

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

    private var mCheckedList: ArrayList<MediaEntity>? = null
    private var mSelectedIndex = 0
    private var mPageMediaList: ArrayList<MediaEntity>? = null
    private var mPagePosition: Int = 0
    private var mPreviewPosition: Int = 0

    override fun getContentView() = R.layout.gallery_activity_media

    override fun onCreateOk(savedInstanceState: Bundle?) {
        mMediaGridFragment = MediaGridFragment.newInstance(mConfiguration!!)

        tv_over_action.visibility = if (!mConfiguration!!.radio) {
            tv_over_action.setOnClickListener {
                if (mMediaGridFragment != null && mMediaGridFragment!!.isShowRvBucketView()) {
                    mMediaGridFragment!!.hideRvBucketView()
                } else {
                    if (!mCheckedList.isNullOrEmpty()) {
                        val event = ImageMultipleResultEvent(mCheckedList)
                        RxBus.getDefault().post(event)
                        finish()
                    }
                }
            }
            View.VISIBLE
        } else {
            View.GONE
        }

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