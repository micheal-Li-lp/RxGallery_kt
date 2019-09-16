package com.micheal.rxgallery.ui.fragment

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.anim.Animation
import com.micheal.rxgallery.anim.AnimationListener
import com.micheal.rxgallery.anim.SlideInUnderneathAnimation
import com.micheal.rxgallery.anim.SlideOutUnderneathAnimation
import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.presenter.impl.MediaGridPresenterImpl
import com.micheal.rxgallery.rxbus.RxBus
import com.micheal.rxgallery.rxbus.RxBusDisposable
import com.micheal.rxgallery.rxbus.event.CloseMediaViewPageFragmentEvent
import com.micheal.rxgallery.rxbus.event.MediaCheckChangeEvent
import com.micheal.rxgallery.rxbus.event.OpenMediaPreviewFragmentEvent
import com.micheal.rxgallery.rxbus.event.RequestStorageReadAccessPermissionEvent
import com.micheal.rxgallery.ui.activity.MediaActivity
import com.micheal.rxgallery.ui.adapter.BucketAdapter
import com.micheal.rxgallery.ui.adapter.MediaGridAdapter
import com.micheal.rxgallery.ui.base.IRadioImageCheckedListener
import com.micheal.rxgallery.ui.widget.FooterAdapter
import com.micheal.rxgallery.ui.widget.HorizontalDividerItemDecoration
import com.micheal.rxgallery.ui.widget.MarginDecoration
import com.micheal.rxgallery.ui.widget.RecyclerViewFinal
import com.micheal.rxgallery.utils.*
import com.micheal.rxgallery.view.MediaGridView
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.gallery_fragment_media_grid.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MediaGridFragment :BaseFragment(), MediaGridView,RecyclerViewFinal.OnLoadMoreListener,
    FooterAdapter.OnItemClickListener,View.OnClickListener, MediaScanner.ScanCallback {

    companion object{
        //接口-单选-是否裁剪
        var iListenerRadio: IRadioImageCheckedListener? = null

        private val IMAGE_TYPE = "image/jpeg"
        //预留公开命名接口
        private var mImageStoreDir: File? = null
        private var mImageStoreCropDir: File? = null //裁剪目录
        //裁剪后+name
        private var mCropPath: File? = null
        private const val IMAGE_STORE_FILE_NAME = "IMG_%s.jpg"
        private const val VIDEO_STORE_FILE_NAME = "IMG_%s.mp4"
        private const val TAKE_IMAGE_REQUEST_CODE = 1001
        private const val CROP_IMAGE_REQUEST_CODE = 1011
        private const val TAKE_URL_STORAGE_KEY = "take_url_storage_key"
        private const val BUCKET_ID_KEY = "bucket_id_key"
        private const val LIMIT = 23

        @JvmStatic
        fun newInstance(configuration: Configuration) = MediaGridFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_CONFIGURATION,configuration)
            }
        }
    }

    var mMediaGridPresenter: MediaGridPresenterImpl?=null
    var mScreenSize: DisplayMetrics?=null
    private var mMediaBeanList  = ArrayList<MediaEntity>()
    private var mMediaGridAdapter: MediaGridAdapter? = null
    private var mRvMedia: RecyclerViewFinal? = null
    private var mBucketAdapter: BucketAdapter? = null
    private var mBucketBeanList  = ArrayList<BucketEntity>()
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
                mMediaBeanList.add(takePhotoBean)
            }
        }
        if (!list.isNullOrEmpty()) {
            mMediaBeanList.addAll(list)
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
        if (list.isNullOrEmpty()) {
            return
        }

        mBucketBeanList.addAll(list)
        mBucketAdapter?.setSelectedBucket(list[0])
    }

    override fun onViewCreatedOk(view: View, savedInstanceState: Bundle?) {

        rv_media.setEmptyView(ll_empty_view)
        val gridLayoutManager = GridLayoutManager(context, 3)
        gridLayoutManager.orientation = GridLayoutManager.VERTICAL
        rv_media.addItemDecoration(MarginDecoration(context!!))
        rv_media.layoutManager = gridLayoutManager
        rv_media.setOnLoadMoreListener(this)
        rv_media.setFooterViewHide(true)

        tv_folder_name.setOnClickListener(this)
        tv_preview.setOnClickListener(this)
        tv_preview.isEnabled = false
        if (mConfiguration!!.radio) {
            view.findViewById<View>(R.id.tv_preview_vr).visibility = View.GONE
            tv_preview.visibility = View.GONE
        } else {
            if (mConfiguration!!.hidePreview) {
                view.findViewById<View>(R.id.tv_preview_vr).visibility = View.GONE
                tv_preview.visibility = View.GONE
            } else {
                view.findViewById<View>(R.id.tv_preview_vr).visibility = View.VISIBLE
                tv_preview.visibility = View.VISIBLE
            }
        }

        mMediaBeanList = java.util.ArrayList()
        mScreenSize = DeviceUtils.getScreenSize(context!!)
        mMediaGridAdapter = MediaGridAdapter(
            mMediaActivity!!,
            mMediaBeanList,
            mScreenSize!!.widthPixels,
            mConfiguration!!
        )
        rv_media.adapter = mMediaGridAdapter
        mMediaGridPresenter = MediaGridPresenterImpl(context!!, mConfiguration!!.image).apply {
            setMediaGridView(this@MediaGridFragment)
        }

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = GridLayoutManager.VERTICAL
        rv_bucket.addItemDecoration(
            HorizontalDividerItemDecoration.Builder(context!!)
                .color(resources.getColor(R.color.gallery_bucket_list_decoration_color))
                .size(resources.getDimensionPixelSize(R.dimen.gallery_divider_decoration_height))
                .margin(
                    resources.getDimensionPixelSize(R.dimen.gallery_bucket_margin),
                    resources.getDimensionPixelSize(R.dimen.gallery_bucket_margin)
                )
                .build()
        )
        rv_bucket.layoutManager = linearLayoutManager
        mBucketBeanList = java.util.ArrayList()
        mBucketAdapter = BucketAdapter(
            mBucketBeanList, mConfiguration!!, ContextCompat.getColor(
                context!!, R.color.gallery_bucket_list_item_normal_color
            )
        )
        rv_bucket.adapter = mBucketAdapter
        rv_media.setOnItemClickListener(this)
        mMediaGridPresenter!!.getBucketList()
//        TODO()
//        mBucketAdapter.setOnRecyclerViewItemClickListener(this)

        rl_bucket_overview.visibility = View.INVISIBLE

        if (slideInUnderneathAnimation == null) {
            slideInUnderneathAnimation = SlideInUnderneathAnimation(rv_bucket)
        }

        slideInUnderneathAnimation?.run {
            direction = Animation.DIRECTION_DOWN
            animate()
        }

        subscribeEvent()

        var activity: Activity? = mMediaActivity
        if (activity == null) {
            activity = getActivity()
        }

        if (mConfiguration!!.image) {
            tv_folder_name.setText(R.string.gallery_all_image)
        } else {
            tv_folder_name.setText(R.string.gallery_all_video)
        }

        val requestStorageAccessPermissionTips = ThemeUtils.resolveString(
            context,
            R.attr.gallery_request_storage_access_permission_tips,
            R.string.gallery_default_request_storage_access_permission_tips
        )
        val success = PermissionCheckUtils.checkReadExternalPermission(
            activity!!, requestStorageAccessPermissionTips,
            MediaActivity.REQUEST_STORAGE_READ_ACCESS_PERMISSION
        )
        if (success) {
            mMediaGridPresenter!!.getMediaList(mBucketId!!, mPage, LIMIT)
        }

    }


    /**
     * RxBus
     */
    private fun subscribeEvent() {
        mMediaCheckChangeDisposable =
            RxBus.getDefault().toObservable(MediaCheckChangeEvent::class.java)
                .subscribeWith(object : RxBusDisposable<MediaCheckChangeEvent>() {
                    override fun onEvent(t: MediaCheckChangeEvent) {
                        tv_preview.isEnabled = !mMediaActivity?.mCheckedList.isNullOrEmpty()

                    }
                })
        RxBus.getDefault().add(mMediaCheckChangeDisposable)

        mCloseMediaViewPageFragmentDisposable =
            RxBus.getDefault().toObservable(CloseMediaViewPageFragmentEvent::class.java)
                .subscribeWith(object : RxBusDisposable<CloseMediaViewPageFragmentEvent>() {
                    @Throws(Exception::class)
                    override fun onEvent(t: CloseMediaViewPageFragmentEvent) {
                        mMediaGridAdapter?.notifyDataSetChanged()
                    }
                })
        RxBus.getDefault().add(mCloseMediaViewPageFragmentDisposable)

        mRequestStorageReadAccessPermissionDisposable =
            RxBus.getDefault().toObservable(RequestStorageReadAccessPermissionEvent::class.java)
                .subscribeWith(object : RxBusDisposable<RequestStorageReadAccessPermissionEvent>() {
                    @Throws(Exception::class)
                    override fun onEvent(t: RequestStorageReadAccessPermissionEvent) {
                        if (t.type == RequestStorageReadAccessPermissionEvent.TYPE_WRITE) {
                            if (t.success) {
                                mMediaGridPresenter?.getMediaList(mBucketId!!, mPage, LIMIT)
                            } else {
                                activity?.finish()
                            }
                        } else {
                            if (t.success) {
                                openCamera(mMediaActivity)
                            }
                        }
                    }
                })
        RxBus.getDefault().add(mRequestStorageReadAccessPermissionDisposable)

    }


    fun openCamera(context: Context?) {


        val image = mConfiguration!!.image

        val captureIntent =
            if (image) Intent(MediaStore.ACTION_IMAGE_CAPTURE) else Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        if (captureIntent.resolveActivity(context!!.packageManager) == null) {
            Toast.makeText(getContext(), R.string.gallery_device_camera_unable, Toast.LENGTH_SHORT)
                .show()
            return
        }

        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
        val filename = String.format(
            if (image) IMAGE_STORE_FILE_NAME else VIDEO_STORE_FILE_NAME, dateFormat.format(
                Date()
            )
        )
        Logger.i("openCamera：" + mImageStoreDir!!.absolutePath)
        val fileImagePath = File(mImageStoreDir, filename)
        mImagePath = fileImagePath.absolutePath

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagePath))
        } else {
            val contentValues = ContentValues(1)
            contentValues.put(MediaStore.Images.Media.DATA, mImagePath)
            val uri = context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        }
        // video : 1: 高质量  0 低质量
        //        captureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(captureIntent, TAKE_IMAGE_REQUEST_CODE)
    }

    override fun getContentView() = R.layout.gallery_fragment_media_grid

    override fun onFirstTimeLaunched() {}

    override fun onRestoreState(savedInstanceState: Bundle) {}

    override fun onSaveState(outState: Bundle) {}


    fun isShowRvBucketView() = rl_bucket_overview != null && rl_bucket_overview.visibility == View.VISIBLE


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


    override fun onItemClick(holder: RecyclerView.ViewHolder?, position: Int) {
        val bucketEntity = mBucketBeanList[position]
        val bucketId = bucketEntity.bucketId
        rl_bucket_overview.visibility = View.GONE
        if (TextUtils.equals(mBucketId, bucketId)) {
            return
        }
        mBucketId = bucketId
        EmptyViewUtils.showLoading(ll_empty_view)
        rv_media.setHasLoadMore(false)
        mMediaBeanList.clear()
        mMediaGridAdapter?.notifyDataSetChanged()
        tv_folder_name.text = bucketEntity.bucketName
        mBucketAdapter?.setSelectedBucket(bucketEntity)
        rv_media.setFooterViewHide(true)
        mPage = 1
        mMediaGridPresenter?.getMediaList(mBucketId!!, mPage, LIMIT)
    }

    override fun loadMore() {
        mMediaGridPresenter?.getMediaList(mBucketId!!, mPage, LIMIT)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.tv_preview) {
            RxBus.getDefault().post(OpenMediaPreviewFragmentEvent())
        } else if (id == R.id.tv_folder_name) {
            v.isEnabled = false
            if (isShowRvBucketView()) {
                hideRvBucketView()
            } else {
                showRvBucketView()
            }
        }
    }

    override fun onScanCompleted(images: Array<String>?) {
        if (images == null || images.size == 0) {
            Logger.i("images empty")
            return
        }

        // mediaBean 有可能为Null，onNext 做了处理，在 getMediaBeanWithImage 时候就不处理Null了
        Observable.create(ObservableOnSubscribe<MediaEntity> {
            val mediaBean = if (mConfiguration!!.image)
                MediaUtils.getMediaEntityWithImage(context!!, images[0])
            else
                MediaUtils.getMediaEntityWithVideo(context!!, images[0])
            it.onNext(mediaBean!!)
            it.onComplete()
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<MediaEntity>() {
                override fun onComplete() {}

                override fun onError(e: Throwable) {
                    Logger.i("获取MediaBean异常$e")
                }

                override fun onNext(mediaBean: MediaEntity) {
                    if (!isDetached) {
                        val bk = FileUtils.existImageDir(mediaBean.originalPath!!)
                        if (bk != -1) {
                            mMediaBeanList.add(1, mediaBean)
                            mMediaGridAdapter!!.notifyDataSetChanged()
                        } else {
                            Logger.i("获取：无")
                        }
                    }

                }
            })
    }

}