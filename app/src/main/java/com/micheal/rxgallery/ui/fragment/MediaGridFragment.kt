package com.micheal.rxgallery.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.anim.Animation
import com.micheal.rxgallery.anim.AnimationListener
import com.micheal.rxgallery.anim.SlideInUnderneathAnimation
import com.micheal.rxgallery.anim.SlideOutUnderneathAnimation
import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.presenter.impl.MediaGridPresenterImpl
import com.micheal.rxgallery.ui.activity.MediaActivity
import com.micheal.rxgallery.ui.adapter.BucketAdapter
import com.micheal.rxgallery.ui.adapter.MediaGridAdapter
import com.micheal.rxgallery.ui.base.IRadioImageCheckedListener
import com.micheal.rxgallery.ui.widget.RecyclerViewFinal
import com.micheal.rxgallery.utils.EmptyViewUtils
import com.micheal.rxgallery.utils.Logger
import com.micheal.rxgallery.utils.MediaScanner
import com.micheal.rxgallery.utils.ThemeUtils
import com.micheal.rxgallery.view.MediaGridView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.gallery_fragment_media_grid.*
import java.io.File

class MediaGridFragment :BaseFragment(), MediaGridView{
    companion object{
        //接口-单选-是否裁剪
        var iListenerRadio: IRadioImageCheckedListener? = null

        private val IMAGE_TYPE = "image/jpeg"
        //预留公开命名接口
        private var mImageStoreDir: File? = null
        private var mImageStoreCropDir: File? = null //裁剪目录
        //裁剪后+name
        private var mCropPath: File? = null
        private val IMAGE_STORE_FILE_NAME = "IMG_%s.jpg"
        private val VIDEO_STORE_FILE_NAME = "IMG_%s.mp4"
        private val TAKE_IMAGE_REQUEST_CODE = 1001
        private val CROP_IMAGE_REQUEST_CODE = 1011
        private val TAKE_URL_STORAGE_KEY = "take_url_storage_key"
        private val BUCKET_ID_KEY = "bucket_id_key"
        private val LIMIT = 23

        @JvmStatic
        fun newInstance(configuration: Configuration) = MediaGridFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_CONFIGURATION,configuration)
            }
        }
    }

    var mMediaGridPresenter: MediaGridPresenterImpl?=null
    var mScreenSize: DisplayMetrics?=null
    private var mMediaBeanList: MutableList<MediaEntity>? = null
    private var mMediaGridAdapter: MediaGridAdapter? = null
    private var mRvMedia: RecyclerViewFinal? = null
    private var mBucketAdapter: BucketAdapter? = null
    private var mBucketBeanList: MutableList<BucketEntity>? = null
    //扫描
    private var mMediaScanner: MediaScanner? = null
    private var mPage = 1
    private var mImagePath: String? = null

    private var mBucketId: String? = Integer.MIN_VALUE.toString()

    private var mMediaActivity: MediaActivity? = null
    private var mMediaCheckChangeDisposable: Disposable? = null
    private var mCloseMediaViewPageFragmentDisposable: Disposable? = null
    private var mRequestStorageReadAccessPermissionDisposable: Disposable? = null

    private var slideInUnderneathAnimation: SlideInUnderneathAnimation? = null
    private var slideOutUnderneathAnimation: SlideOutUnderneathAnimation? = null

    private var uCropStatusColor: Int = 0
    private var uCropToolbarColor: Int = 0
    private var uCropActivityWidgetColor: Int = 0
    private var uCropToolbarWidgetColor: Int = 0
    private var uCropTitle: String? = null
    private var requestStorageAccessPermissionTips: String? = null


    override fun onRequestMediaCallback(list: List<MediaEntity>?) {
        if (!mConfiguration!!.hideCamera) {
            if (mPage == 1 && TextUtils.equals(mBucketId, Integer.MIN_VALUE.toString())) {
                val takePhotoBean = MediaEntity()
                takePhotoBean.id = Integer.MIN_VALUE.toLong()
                takePhotoBean.bucketId = Integer.MIN_VALUE.toString()
                mMediaBeanList?.add(takePhotoBean)
            }
        }
        if (!list.isNullOrEmpty()) {
            mMediaBeanList?.addAll(list)
            Logger.i(String.format("得到:%s张图片", list.size))
        } else {
            Logger.i("没有更多图片")
        }
        mMediaGridAdapter?.notifyDataSetChanged()

        mPage++

        if (list == null || list.size < LIMIT) {
            rv_media.setFooterViewHide(true)
            rv_media.setHasLoadMore(false)
        } else {
            rv_media.setFooterViewHide(false)
            rv_media.setHasLoadMore(true)
        }

        if (mMediaBeanList.isNullOrEmpty()) {
            val mediaEmptyTils = ThemeUtils.resolveString(
                context,
                R.attr.gallery_media_empty_tips,
                R.string.gallery_default_media_empty_tips
            )
            EmptyViewUtils.showMessage(ll_empty_view, mediaEmptyTils)
        }

        rv_media.onLoadMoreComplete()
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


    fun isShowRvBucketView(): Boolean {
        return rl_bucket_overview != null && rl_bucket_overview.visibility == View.VISIBLE
    }


    fun showRvBucketView() {
        if (slideInUnderneathAnimation==null){
            slideInUnderneathAnimation = SlideInUnderneathAnimation(rl_bucket_overview)
        }
        rl_bucket_overview.visibility = View.VISIBLE
        slideInUnderneathAnimation?.run {
            direction = Animation.DIRECTION_DOWN
            duration = Animation.DURATION_DEFAULT.toLong()
            listener = object :AnimationListener{
                override fun onAnimationEnd(animation: Animation) {
                    tv_folder_name.isEnabled = true
                }
            }
            animate()
        }
    }

    fun hideRvBucketView() {
        if (slideOutUnderneathAnimation == null) {
            slideOutUnderneathAnimation = SlideOutUnderneathAnimation(rv_bucket)
        }
        slideOutUnderneathAnimation?.run {
            direction = Animation.DIRECTION_DOWN
            duration = Animation.DURATION_DEFAULT.toLong()
            listener = object : AnimationListener{
                override fun onAnimationEnd(animation: Animation) {
                    tv_folder_name.isEnabled = true
                    rl_bucket_overview.visibility = View.GONE
                }

            }
            animate()
        }
    }

}