package com.micheal.rxgallery.ui.activity

import android.content.pm.PackageManager
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.rxbus.RxBus
import com.micheal.rxgallery.rxbus.RxBusDisposable
import com.micheal.rxgallery.rxbus.event.*
import com.micheal.rxgallery.rxjob.RxJob
import com.micheal.rxgallery.ui.fragment.MediaGridFragment
import com.micheal.rxgallery.ui.fragment.MediaPageFragment
import com.micheal.rxgallery.ui.fragment.MediaPreviewFragment
import com.micheal.rxgallery.utils.Logger
import com.micheal.rxgallery.utils.OsCompat
import com.micheal.rxgallery.utils.ThemeUtils
import com.micheal.rxgallery.view.ActivityFragmentView
import kotlinx.android.synthetic.main.gallery_activity_media.*
import java.util.ArrayList

class MediaActivity : BaseActivity() , ActivityFragmentView{
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

    var mCheckedList = ArrayList<MediaEntity>()
    private var mSelectedIndex = 0
    private var mPageMediaList  = ArrayList<MediaEntity>()
    private var mPagePosition: Int = 0
    private var mPreviewPosition: Int = 0

    override fun getContentView() = R.layout.gallery_activity_media

    override fun onCreateOk(savedInstanceState: Bundle?) {
        mMediaGridFragment = MediaGridFragment.newInstance(mConfiguration)

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

        val selectedList = mConfiguration!!.selectedList
        if (!selectedList.isNullOrEmpty()) {
            mCheckedList.addAll(selectedList)
            if (mCheckedList.size > 0) {
                val text = resources.getString(
                    R.string.gallery_over_button_text_checked,
                    mCheckedList.size,
                    mConfiguration!!.maxSize
                )
                tv_over_action.text = text
                tv_over_action.isEnabled = true
            } else {
                tv_over_action.setText(R.string.gallery_over_button_text)
                tv_over_action.isEnabled = false
            }
        }

        showMediaGridFragment()
        subscribeEvent()

    }

    override fun findViews() {
        toolbar.title = ""
    }

    override fun setTheme() {
        val closeDrawable = ThemeUtils.resolveDrawable(
            this,
            R.attr.gallery_toolbar_close_image,
            R.drawable.gallery_default_toolbar_close_image
        )
        val closeColor = ThemeUtils.resolveColor(
            this,
            R.attr.gallery_toolbar_close_color,
            R.color.gallery_default_toolbar_widget_color
        )

        closeDrawable.setColorFilter(closeColor, PorterDuff.Mode.SRC_ATOP)
        toolbar.navigationIcon = closeDrawable

        val overButtonBg =
            ThemeUtils.resolveDrawableRes(this, R.attr.gallery_toolbar_over_button_bg)
        if (overButtonBg != 0) {
            tv_over_action.setBackgroundResource(overButtonBg)
        } else {
            OsCompat.setBackgroundDrawableCompat(tv_over_action, createDefaultOverButtonBgDrawable())
        }

        val overTextSize = ThemeUtils.resolveDimen(
            this,
            R.attr.gallery_toolbar_over_button_text_size,
            R.dimen.gallery_default_toolbar_over_button_text_size
        )
        tv_over_action.setTextSize(TypedValue.COMPLEX_UNIT_PX, overTextSize)

        val overTextColor = ThemeUtils.resolveColor(
            this,
            R.attr.gallery_toolbar_over_button_text_color,
            R.color.gallery_default_toolbar_over_button_text_color
        )
        tv_over_action.setTextColor(overTextColor)

        val titleTextSize = ThemeUtils.resolveDimen(
            this,
            R.attr.gallery_toolbar_text_size,
            R.dimen.gallery_default_toolbar_text_size
        )
        tv_toolbar_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)

        val titleTextColor = ThemeUtils.resolveColor(
            this,
            R.attr.gallery_toolbar_text_color,
            R.color.gallery_default_toolbar_text_color
        )
        tv_toolbar_title.setTextColor(titleTextColor)

        val gravity = ThemeUtils.resolveInteger(
            this,
            R.attr.gallery_toolbar_text_gravity,
            R.integer.gallery_default_toolbar_text_gravity
        )
        tv_toolbar_title.layoutParams = Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.WRAP_CONTENT,
            gravity
        )

        ThemeUtils.resolveColor(
            this,
            R.attr.gallery_toolbar_bg,
            R.color.gallery_default_color_toolbar_bg
        ).run {
            toolbar.setBackgroundColor(this)
        }

        ThemeUtils.resolveDimen(
            this,
            R.attr.gallery_toolbar_height,
            R.dimen.gallery_default_toolbar_height
        ).toInt().run {
            toolbar.minimumHeight = this
        }

        ThemeUtils.resolveColor(
            this,
            R.attr.gallery_color_statusbar,
            R.color.gallery_default_color_statusbar
        ).run {
            ThemeUtils.setStatusBarColor(this, window)
        }

        LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
            ThemeUtils.resolveDimen(this,
                R.attr.gallery_toolbar_divider_height,
                R.dimen.gallery_default_toolbar_divider_height
            ).toInt()
        ).run {
            bottomMargin = ThemeUtils.resolveDimen(
                this@MediaActivity,
                R.attr.gallery_toolbar_bottom_margin,
                R.dimen.gallery_default_toolbar_bottom_margin
            ).toInt()
            toolbar_divider.layoutParams = this
        }

        ThemeUtils.resolveDrawable(
            this,
            R.attr.gallery_toolbar_divider_bg,
            R.color.gallery_default_toolbar_divider_bg
        ).run {
            OsCompat.setBackgroundDrawableCompat(toolbar_divider, this)
        }

        setSupportActionBar(toolbar)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_CHECKED_LIST, mCheckedList)
        outState.putInt(EXTRA_SELECTED_INDEX, mSelectedIndex)
        outState.putParcelableArrayList(EXTRA_PAGE_MEDIA_LIST, mPageMediaList)
        outState.putInt(EXTRA_PAGE_POSITION, mPagePosition)
        outState.putInt(EXTRA_PREVIEW_POSITION, mPreviewPosition)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val list = savedInstanceState.getParcelableArrayList<MediaEntity>(EXTRA_CHECKED_LIST)
        if (!list.isNullOrEmpty()) {
            mCheckedList.clear()
            mCheckedList.addAll(list)
        }
        mPageMediaList.run {
            clear()
            addAll(savedInstanceState.getParcelableArrayList(EXTRA_PAGE_MEDIA_LIST)!!)
        }
        mPagePosition = savedInstanceState.getInt(EXTRA_PAGE_POSITION)
        mPreviewPosition = savedInstanceState.getInt(EXTRA_PREVIEW_POSITION)
        mSelectedIndex = savedInstanceState.getInt(EXTRA_SELECTED_INDEX)
        if (!mConfiguration!!.radio) {
            when (mSelectedIndex) {
                1 -> showMediaPageFragment(mPageMediaList, mPagePosition)
                2 -> showMediaPreviewFragment()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            backAction()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun backAction() {
        if (mMediaGridFragment != null && mMediaGridFragment!!.isShowRvBucketView()) {
            mMediaGridFragment?.hideRvBucketView()
            return
        }
        if (mMediaPreviewFragment != null && mMediaPreviewFragment!!.isVisible || mMediaPageFragment != null
            && mMediaPageFragment!!.isVisible
        ) {
            showMediaGridFragment()
            return
        }
        onBackPressed()
    }

    override fun showMediaGridFragment() {
        mMediaPreviewFragment = null
        mMediaPageFragment = null
        mSelectedIndex = 0

        val ft = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, mMediaGridFragment!!)
        if (mMediaPreviewFragment != null) {
            ft.hide(mMediaPreviewFragment!!)
        }
        if (mMediaPageFragment != null) {
            ft.hide(mMediaPageFragment!!)
        }
        ft.show(mMediaGridFragment!!)
            .commit()

        if (mConfiguration!!.image) {
            tv_toolbar_title.setText(R.string.gallery_media_grid_image_title)
        } else {
            tv_toolbar_title.setText(R.string.gallery_media_grid_video_title)
        }
    }

    private fun subscribeEvent() = RxBus.getDefault().run {
        toObservable(OpenMediaPreviewFragmentEvent::class.java)
            .map { mediaPreviewEvent -> mediaPreviewEvent }
            .subscribeWith(object : RxBusDisposable<OpenMediaPreviewFragmentEvent>() {
                override fun onEvent(t: OpenMediaPreviewFragmentEvent) {
                    mPreviewPosition = 0
                    showMediaPreviewFragment()
                }
            }).run {
                add(this)
            }
        toObservable(MediaCheckChangeEvent::class.java)
            .map { mediaCheckChangeEvent -> mediaCheckChangeEvent }
            .subscribeWith(object : RxBusDisposable<MediaCheckChangeEvent>() {
                override fun onEvent(t: MediaCheckChangeEvent) {
                    val mediaBean = t.mediaEntity
                    if (mCheckedList.contains(mediaBean)) {
                        mCheckedList.remove(mediaBean)
                    } else {
                        mCheckedList.add(mediaBean)
                    }

                    if (mCheckedList.size > 0) {
                        val text = resources.getString(
                            R.string.gallery_over_button_text_checked,
                            mCheckedList.size,
                            mConfiguration!!.maxSize
                        )
                        tv_over_action.text = text
                        tv_over_action.isEnabled = true
                    } else {
                        tv_over_action.setText(R.string.gallery_over_button_text)
                        tv_over_action.isEnabled = false
                    }
                }
            }).run {
                add(this)
            }

        toObservable(MediaViewPagerChangedEvent::class.java)
            .map { mediaViewPagerChangedEvent -> mediaViewPagerChangedEvent }
            .subscribeWith(object : RxBusDisposable<MediaViewPagerChangedEvent>() {
                override fun onEvent(t: MediaViewPagerChangedEvent) {
                    val curIndex = t.curIndex
                    val totalSize = t.totalSize
                    if (t.isPreview) {
                        mPreviewPosition = curIndex
                    } else {
                        mPagePosition = curIndex
                    }
                    val title = getString(R.string.gallery_page_title, curIndex + 1, totalSize)
                    tv_toolbar_title.text = title
                }
            }).run {
                add(this)
            }

        toObservable(CloseRxMediaGridPageEvent::class.java)
            .subscribeWith(object : RxBusDisposable<CloseRxMediaGridPageEvent>() {
                @Throws(Exception::class)
                override fun onEvent(t: CloseRxMediaGridPageEvent) {
                    finish()
                }
            }).run {
                add(this)
            }
        toObservable(OpenMediaPageFragmentEvent::class.java)
            .subscribeWith(object : RxBusDisposable<OpenMediaPageFragmentEvent>() {
                override fun onEvent(t: OpenMediaPageFragmentEvent) {
                    mPageMediaList.run {
                        this.clear()
                        this.addAll(t.list )
                    }
                    mPagePosition = t.position
                    showMediaPageFragment(mPageMediaList, mPagePosition)
                }
            }).run {
                add(this)
            }
    }

    override fun showMediaPageFragment(list: List<MediaEntity>, position: Int) {
        mSelectedIndex = 1
        val ft = supportFragmentManager.beginTransaction()
        mMediaPageFragment = MediaPageFragment.newInstance(mConfiguration, list, position)
        ft.add(R.id.fragment_container, mMediaPageFragment!!)
        mMediaPreviewFragment = null
        ft.hide(mMediaGridFragment!!)
        ft.show(mMediaPageFragment!!)
        ft.commit()

        val title = getString(R.string.gallery_page_title, position + 1, list.size)
        tv_toolbar_title.text = title
    }

    override fun showMediaPreviewFragment() {
        mSelectedIndex = 2
        val ft = supportFragmentManager.beginTransaction()
        mMediaPreviewFragment = MediaPreviewFragment.newInstance(mConfiguration, mPreviewPosition)
        ft.add(R.id.fragment_container, mMediaPreviewFragment!!)
        mMediaPageFragment = null
        ft.hide(mMediaGridFragment!!)
        ft.show(mMediaPreviewFragment!!)
        ft.commit()

        val title = getString(R.string.gallery_page_title, mPreviewPosition, mCheckedList.size)
        tv_toolbar_title.text = title
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBus.getDefault().removeAllStickyEvents()
        RxBus.getDefault().clear()
        RxJob.getDefault().clearJob()
    }

    private fun createDefaultOverButtonBgDrawable(): StateListDrawable {
        val dp12 = ThemeUtils.applyDimensionDp(this, 12f).toInt()
        val dp8 = ThemeUtils.applyDimensionDp(this, 8f).toInt()
        val dp4 = ThemeUtils.applyDimensionDp(this, 4f)
        val round = floatArrayOf(dp4, dp4, dp4, dp4, dp4, dp4, dp4, dp4)
        val pressedDrawable = ShapeDrawable(RoundRectShape(round, null, null))
        pressedDrawable.setPadding(dp12, dp8, dp12, dp8)
        val pressedColor = ThemeUtils.resolveColor(
            this,
            R.attr.gallery_toolbar_over_button_pressed_color,
            R.color.gallery_default_toolbar_over_button_pressed_color
        )
        pressedDrawable.paint.color = pressedColor

        val normalColor = ThemeUtils.resolveColor(
            this,
            R.attr.gallery_toolbar_over_button_normal_color,
            R.color.gallery_default_toolbar_over_button_normal_color
        )
        val normalDrawable = ShapeDrawable(RoundRectShape(round, null, null))
        normalDrawable.setPadding(dp12, dp8, dp12, dp8)
        normalDrawable.paint.color = normalColor

        val stateListDrawable = StateListDrawable()
        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedDrawable)
        stateListDrawable.addState(intArrayOf(), normalDrawable)

        return stateListDrawable
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Logger.i("onRequestPermissionsResult:requestCode=$requestCode permissions=${permissions[0]}" )
        when (requestCode) {
            REQUEST_STORAGE_READ_ACCESS_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RxBus.getDefault().post(
                    RequestStorageReadAccessPermissionEvent(
                        true,
                        RequestStorageReadAccessPermissionEvent.TYPE_WRITE
                    )
                )
            } else {
                finish()
            }
            REQUEST_STORAGE_WRITE_ACCESS_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RxBus.getDefault().post(
                    RequestStorageReadAccessPermissionEvent(
                        true,
                        RequestStorageReadAccessPermissionEvent.TYPE_WRITE
                    )
                )
            } else {
                finish()
            }
            REQUEST_CAMERA_ACCESS_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RxBus.getDefault().post(
                    RequestStorageReadAccessPermissionEvent(
                        true,
                        RequestStorageReadAccessPermissionEvent.TYPE_CAMERA
                    )
                )
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


}