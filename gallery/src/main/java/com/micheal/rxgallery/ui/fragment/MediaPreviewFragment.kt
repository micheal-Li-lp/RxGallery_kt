package com.micheal.rxgallery.ui.fragment

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.viewpager.widget.ViewPager
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.rxbus.RxBus
import com.micheal.rxgallery.rxbus.event.CloseMediaViewPageFragmentEvent
import com.micheal.rxgallery.rxbus.event.MediaCheckChangeEvent
import com.micheal.rxgallery.rxbus.event.MediaViewPagerChangedEvent
import com.micheal.rxgallery.ui.activity.MediaActivity
import com.micheal.rxgallery.ui.adapter.MediaPreviewAdapter
import com.micheal.rxgallery.utils.DeviceUtils
import com.micheal.rxgallery.utils.ThemeUtils
import kotlinx.android.synthetic.main.gallery_fragment_media_preview.*

class MediaPreviewFragment : BaseFragment(), ViewPager.OnPageChangeListener,
    View.OnClickListener {

    companion object{
        private const val EXTRA_PAGE_INDEX = "$EXTRA_PREFIX.PageIndex"

        @JvmStatic
        fun newInstance(configuration: Configuration?,position: Int) = MediaPreviewFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_CONFIGURATION,configuration)
                putInt(EXTRA_PAGE_INDEX,position)
            }
        }
    }

    private lateinit var mScreenSize: DisplayMetrics
    private var mMediaEntityList = ArrayList<MediaEntity>()
    private lateinit var mMediaActivity: MediaActivity
    private var mPagerPosition: Int = 0


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MediaActivity) {
            mMediaActivity = context
        }
    }

    override fun getContentView() = R.layout.gallery_fragment_media_preview

    override fun onViewCreatedOk(view: View, savedInstanceState: Bundle?) {

        mScreenSize = DeviceUtils.getScreenSize(context!!)
        if (!mMediaActivity.mCheckedList.isNullOrEmpty()) {
            mMediaEntityList.addAll(mMediaActivity.mCheckedList)
        }
        val mMediaPreviewAdapter = MediaPreviewAdapter(
            mMediaEntityList,
            mScreenSize.widthPixels, mScreenSize.heightPixels, mConfiguration!!,
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

        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX)
        }
    }

    override fun onStart() {
        super.onStart()
        view_pager.setCurrentItem(mPagerPosition, false)
        view_pager.addOnPageChangeListener(this)
        //#ADD UI预览数量的BUG
        RxBus.getDefault()
            .post(MediaViewPagerChangedEvent(mPagerPosition, mMediaEntityList.size, true))

    }

    override fun setTheme() {
        super.setTheme()
        val checkTint = ThemeUtils.resolveColor(
            context,
            R.attr.gallery_checkbox_button_tint_color,
            R.color.gallery_default_checkbox_button_tint_color
        )
        CompoundButtonCompat.setButtonTintList(cb_check, ColorStateList.valueOf(checkTint))
        val cbTextColor = ThemeUtils.resolveColor(
            context,
            R.attr.gallery_checkbox_text_color,
            R.color.gallery_default_checkbox_text_color
        )
        cb_check.setTextColor(cbTextColor)

        val pageColor = ThemeUtils.resolveColor(
            context,
            R.attr.gallery_page_bg,
            R.color.gallery_default_page_bg
        )
        rl_root_view.setBackgroundColor(pageColor)
    }

    override fun onFirstTimeLaunched() {}

    override fun onRestoreState(savedInstanceState: Bundle) {
        mPagerPosition = savedInstanceState.getInt(EXTRA_PAGE_INDEX)
    }

    override fun onSaveState(outState: Bundle) {
        outState.putInt(EXTRA_PAGE_INDEX, mPagerPosition)
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        mPagerPosition = position
        val mediaBean = mMediaEntityList[position]
        cb_check.isChecked = false
        //判断是否选择
        if (mMediaActivity != null && !mMediaActivity?.mCheckedList.isNullOrEmpty()) {
            cb_check.isChecked = mMediaActivity?.mCheckedList!!.contains(mediaBean)
        }

        RxBus.getDefault().post(MediaViewPagerChangedEvent(position, mMediaEntityList.size, true))
    }

    override fun onClick(p0: View?) {
        val position = view_pager.currentItem
        val mediaBean = mMediaEntityList[position]
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

    override fun onDestroyView() {
        super.onDestroyView()
        mPagerPosition = 0
        RxBus.getDefault().post(CloseMediaViewPageFragmentEvent())
    }

}