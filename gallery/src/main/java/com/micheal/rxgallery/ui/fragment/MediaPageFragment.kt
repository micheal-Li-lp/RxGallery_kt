package com.micheal.rxgallery.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.rxbus.RxBus
import com.micheal.rxgallery.rxbus.event.MediaCheckChangeEvent
import com.micheal.rxgallery.rxbus.event.MediaViewPagerChangedEvent
import com.micheal.rxgallery.ui.activity.MediaActivity
import com.micheal.rxgallery.ui.adapter.MediaPreviewAdapter
import com.micheal.rxgallery.utils.Logger
import com.micheal.rxgallery.utils.ThemeUtils
import kotlinx.android.synthetic.main.gallery_fragment_media_preview.*
import java.util.ArrayList

class MediaPageFragment :BaseFragment(), ViewPager.OnPageChangeListener,
    View.OnClickListener{

    companion object{
        private const val EXTRA_MEDIA_LIST = "$EXTRA_PREFIX.MediaList"
        private const val EXTRA_ITEM_CLICK_POSITION = "$EXTRA_PREFIX.ItemClickPosition"

        @JvmStatic
        fun newInstance(configuration: Configuration?,list: List<MediaEntity>,position: Int) = MediaPageFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_CONFIGURATION,configuration)
                putParcelableArrayList(EXTRA_MEDIA_LIST, arrayListOf<MediaEntity>().apply {
                    addAll(list)
                })
                putInt(EXTRA_ITEM_CLICK_POSITION,position)
            }
        }
    }

    private var mScreenSize: DisplayMetrics? = null
    private var mMediaPreviewAdapter: MediaPreviewAdapter? = null
    private var mMediaBeanList =  ArrayList<MediaEntity>()
    private var mMediaActivity: MediaActivity? = null
    private var mItemClickPosition: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MediaActivity) {
            mMediaActivity = context
        }
    }

    override fun getContentView() = R.layout.gallery_fragment_media_page

    override fun onViewCreatedOk(view: View, savedInstanceState: Bundle?) {
        mMediaBeanList = ArrayList()
        if (savedInstanceState != null) {
            val mediaList = savedInstanceState.getParcelableArrayList<MediaEntity>(EXTRA_MEDIA_LIST)
            mItemClickPosition = savedInstanceState.getInt(EXTRA_ITEM_CLICK_POSITION)

            if (mediaList != null) {
                mMediaBeanList.addAll(mediaList)
            }
        }
        mMediaPreviewAdapter = MediaPreviewAdapter(
            mMediaBeanList,
            mScreenSize!!.widthPixels,
            mScreenSize!!.heightPixels,
            mConfiguration!!,
            ThemeUtils.resolveColor(
                activity,
                R.attr.gallery_page_bg,
                R.color.gallery_default_page_bg
            ),
            ContextCompat.getDrawable(
                activity!!,
                ThemeUtils.resolveDrawableRes(
                    activity,
                    R.attr.gallery_default_image,
                    R.drawable.gallery_default_image
                )
            )
        )
        view_pager.adapter = mMediaPreviewAdapter
        cb_check.setOnClickListener(this)
        view_pager.currentItem = mItemClickPosition
        view_pager.addOnPageChangeListener(this)
    }

    override fun onFirstTimeLaunched() {}

    override fun onRestoreState(savedInstanceState: Bundle) {

        val mediaList = savedInstanceState.getParcelableArrayList<MediaEntity>(EXTRA_MEDIA_LIST)
        mItemClickPosition = savedInstanceState.getInt(EXTRA_ITEM_CLICK_POSITION)
        if (mediaList != null) {
            mMediaBeanList?.clear()
            Logger.i("恢复数据:" + mediaList.size + "  d=" + mediaList[0].originalPath)
            mMediaBeanList?.addAll(mediaList)
        }
        view_pager.currentItem = mItemClickPosition
        mMediaPreviewAdapter?.notifyDataSetChanged()
    }

    override fun onSaveState(outState: Bundle) {
        outState.putParcelableArrayList(EXTRA_MEDIA_LIST, mMediaBeanList)
        outState.putInt(EXTRA_ITEM_CLICK_POSITION, mItemClickPosition)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        mItemClickPosition = position

        val mediaBean = mMediaBeanList.get(position)
        //判断是否选择
        if (!mMediaActivity?.mCheckedList.isNullOrEmpty()) {
            cb_check.isChecked = mMediaActivity!!.mCheckedList.contains(mediaBean)
        } else {
            cb_check.isChecked = false
        }

        RxBus.getDefault().post(MediaViewPagerChangedEvent(position, mMediaBeanList.size, false))
    }

    override fun onClick(p0: View?) {
        if (mMediaBeanList.isNullOrEmpty()) {
            return
        }

        val position = view_pager.currentItem
        val mediaBean = mMediaBeanList.get(position)
        if (mConfiguration?.maxSize == mMediaActivity?.mCheckedList?.size && !mMediaActivity?.mCheckedList!!.contains(
                mediaBean
            )
        ) {
            Toast.makeText(
                context,
                resources
                    .getString(R.string.gallery_image_max_size_tip, mConfiguration!!.maxSize),
                Toast.LENGTH_SHORT
            ).show()
            cb_check.isChecked = false
        } else {
            RxBus.getDefault().post(MediaCheckChangeEvent(mediaBean))
        }
    }
}