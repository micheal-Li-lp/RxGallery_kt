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
import com.micheal.rxgallery.utils.DeviceUtils
import com.micheal.rxgallery.utils.Logger
import com.micheal.rxgallery.utils.ThemeUtils
import kotlinx.android.synthetic.main.gallery_fragment_media_page.*
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

    private lateinit var mScreenSize: DisplayMetrics
    private lateinit var mMediaPreviewAdapter: MediaPreviewAdapter
    private var mMediaEntityList =  ArrayList<MediaEntity>()
    private lateinit var mMediaActivity: MediaActivity
    private var mItemClickPosition: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MediaActivity) {
            mMediaActivity = context
        }
    }

    override fun getContentView() = R.layout.gallery_fragment_media_page

    override fun onViewCreatedOk(view: View, savedInstanceState: Bundle?) {
        mScreenSize = DeviceUtils.getScreenSize(context!!)

        if (savedInstanceState != null) {
            val mediaList = savedInstanceState.getParcelableArrayList<MediaEntity>(EXTRA_MEDIA_LIST)
            mItemClickPosition = savedInstanceState.getInt(EXTRA_ITEM_CLICK_POSITION)

            if (mediaList != null) {
                mMediaEntityList.addAll(mediaList)
            }
        }
        mMediaPreviewAdapter = MediaPreviewAdapter(
            mMediaEntityList,
            mScreenSize.widthPixels,
            mScreenSize.heightPixels,
            mConfiguration!!,
            ThemeUtils.resolveColor(
                activity,
                R.attr.gallery_page_bg,
                R.color.gallery_default_page_bg
            ),
            ContextCompat.getDrawable(
                context!!,
                ThemeUtils.resolveDrawableRes(
                    context,
                    R.attr.gallery_default_image,
                    R.drawable.gallery_default_image
                )
            )
        )
        view_pager_page.run {
            adapter = mMediaPreviewAdapter
            currentItem = mItemClickPosition
            addOnPageChangeListener(this@MediaPageFragment)
        }
        mMediaPreviewAdapter.notifyDataSetChanged()
        cb_page_check.setOnClickListener(this)
    }

    override fun onFirstTimeLaunched() {}

    override fun onRestoreState(savedInstanceState: Bundle) {

        val mediaList = savedInstanceState.getParcelableArrayList<MediaEntity>(EXTRA_MEDIA_LIST)
        mItemClickPosition = savedInstanceState.getInt(EXTRA_ITEM_CLICK_POSITION)
        if (mediaList != null) {
            mMediaEntityList.clear()
            Logger.i("恢复数据:${mediaList.size}  d=${mediaList[0].originalPath}")
            mMediaEntityList.addAll(mediaList)
        }
        view_pager_page.currentItem = mItemClickPosition
        mMediaPreviewAdapter.notifyDataSetChanged()
    }

    override fun onSaveState(outState: Bundle) = outState.run {
        putParcelableArrayList(EXTRA_MEDIA_LIST, mMediaEntityList)
        putInt(EXTRA_ITEM_CLICK_POSITION, mItemClickPosition)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        mItemClickPosition = position

        val mediaBean = mMediaEntityList[position]
        //判断是否选择
        cb_page_check.isChecked = if (!mMediaActivity.mCheckedList.isNullOrEmpty()) {
            mMediaActivity.mCheckedList.contains(mediaBean)
        } else {
            false
        }

        RxBus.post(MediaViewPagerChangedEvent(position, mMediaEntityList.size, false))
    }

    override fun onClick(p0: View?) {
        if (mMediaEntityList.isNullOrEmpty()) {
            return
        }

        val position = view_pager_page.currentItem
        val mediaBean = mMediaEntityList[position]
        if (mConfiguration?.maxSize == mMediaActivity.mCheckedList.size && !mMediaActivity.mCheckedList.contains(
                mediaBean
            )
        ) {
            Toast.makeText(
                context,
                resources
                    .getString(R.string.gallery_image_max_size_tip, mConfiguration!!.maxSize),
                Toast.LENGTH_SHORT
            ).show()
            cb_page_check.isChecked = false
        } else {
            RxBus.post(MediaCheckChangeEvent(mediaBean))
        }
    }
}