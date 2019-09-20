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
import com.micheal.rxgallery.entity.ImageCropEntity
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.presenter.impl.MediaGridPresenterImpl
import com.micheal.rxgallery.rxbus.RxBus
import com.micheal.rxgallery.rxbus.RxBusDisposable
import com.micheal.rxgallery.rxbus.event.*
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
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import com.yalantis.ucrop.model.AspectRatio
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.gallery_fragment_media_grid.*
import kotlinx.android.synthetic.main.gallery_fragment_media_grid.rl_root_view
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MediaGridFragment :BaseFragment(), MediaGridView,RecyclerViewFinal.OnLoadMoreListener,
    FooterAdapter.OnItemClickListener,View.OnClickListener, MediaScanner.ScanCallback {

    companion object{
        //接口-单选-是否裁剪
        var iListenerRadio: IRadioImageCheckedListener? = null

        private const val IMAGE_TYPE = "image/jpeg"
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

        /**
         * getImageStoreDir
         *
         * @return 存储路径
         */
        @JvmStatic
        fun getImageStoreDirByFile() = mImageStoreDir

        /**
         * getImageStoreDir
         *
         * @return 存储路径
         */
        @JvmStatic
        fun  getImageStoreDirByStr() =  mImageStoreDir?.path

        /**
         * 设置路径
         */
        @JvmStatic
        fun setImageStoreDir(imgFile :File){
            Logger.i("设置图片保存路径为：" + imgFile.absolutePath)
            mImageStoreDir = imgFile
        }

        /**
         * 设置路径
         */
        @JvmStatic
        fun setImageStoreDir(imgFile :String){
            mImageStoreDir = File(
                BaseUtils.getExternalDirectory(),
                "/DCIM" + File.separator + imgFile + File.separator
            )
            Logger.i("设置图片保存路径为：${mImageStoreDir?.absolutePath}" )
        }

        /**
         * getImageStoreDir裁剪
         *
         * @return 裁剪存储路径
         */
        @JvmStatic
        fun  getImageStoreCropDirByFile() = mImageStoreCropDir

        /**
         * getImageStoreDir
         *
         * @return 存储路径
         */
        @JvmStatic
        fun  getImageStoreCropDirByStr() = mImageStoreCropDir?.path

        /**
         * 设置裁剪路径
         */
        @JvmStatic
        fun setImageStoreCropDir(imgFile :File){
            mImageStoreCropDir = imgFile
            Logger.i("设置图片裁剪保存路径为：${mImageStoreCropDir?.absolutePath}" )
        }

        /**
         * 设置裁剪路径
         *
         * @param imgFile 裁剪
         */
        @JvmStatic
        fun setImageStoreCropDir(imgFile :String){
            mImageStoreCropDir = File(
                BaseUtils.getExternalDirectory(),
                "/DCIM${File.separator}$imgFile${File.separator}"
            )
            if (!mImageStoreCropDir!!.exists()) {
                mImageStoreCropDir!!.mkdirs()
            }
            Logger.i("设置图片裁剪保存路径为：${mImageStoreCropDir?.absolutePath}" )
        }

        @JvmStatic
        fun newInstance(configuration: Configuration?) = MediaGridFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_CONFIGURATION,configuration)
            }
        }
    }

    lateinit var mMediaGridPresenter: MediaGridPresenterImpl
    private lateinit var mScreenSize: DisplayMetrics
    private var mMediaEntityList  = ArrayList<MediaEntity>()
    private lateinit var mMediaGridAdapter: MediaGridAdapter
    private lateinit var mBucketAdapter: BucketAdapter
    private var mBucketEntityList  = ArrayList<BucketEntity>()
    //扫描
    private lateinit var mMediaScanner: MediaScanner
    private var mPage = 1
    private var mImagePath: String? = null

    private var mBucketId: String? = Integer.MIN_VALUE.toString()

    private var mMediaCheckChangeDisposable: Disposable? = null
    private var mCloseMediaViewPageFragmentDisposable: Disposable? = null

    private var slideInUnderneathAnimation: SlideInUnderneathAnimation? = null
    private var slideOutUnderneathAnimation: SlideOutUnderneathAnimation? = null

    private var uCropStatusColor: Int = 0
    private var uCropToolbarColor: Int = 0
    private var uCropActivityWidgetColor: Int = 0
    private var uCropToolbarWidgetColor: Int = 0
    private var uCropTitle: String? = null
    private var requestStorageAccessPermissionTips: String? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mMediaScanner = MediaScanner(context)
    }

    override fun getContentView() = R.layout.gallery_fragment_media_grid

    override fun onViewCreatedOk(view: View, savedInstanceState: Bundle?) {
        rv_media.run {
            setEmptyView(ll_empty_view)
            addItemDecoration(MarginDecoration(context!!))
            GridLayoutManager(context, 3).run {
                orientation = GridLayoutManager.VERTICAL
                layoutManager=this
            }
            setOnLoadMoreListener(this@MediaGridFragment)
            setFooterViewHide(true)
        }

        tv_folder_name.setOnClickListener(this)
        tv_preview.run {
            setOnClickListener(this@MediaGridFragment)
            isEnabled = false
            visibility = if (mConfiguration!!.radio) {
                view.findViewById<View>(R.id.tv_preview_vr).visibility = View.GONE
                View.GONE
            } else {
                if (mConfiguration!!.hidePreview) {
                    view.findViewById<View>(R.id.tv_preview_vr).visibility = View.GONE
                    View.GONE
                } else {
                    view.findViewById<View>(R.id.tv_preview_vr).visibility = View.VISIBLE
                    View.VISIBLE
                }
            }
        }

        mScreenSize = DeviceUtils.getScreenSize(context!!)

        MediaGridAdapter(
            context as MediaActivity,
            mMediaEntityList,
            mScreenSize.widthPixels,
            mConfiguration!!
        ).run {
             mMediaGridAdapter = this
             rv_media.adapter = this
        }
        mMediaGridPresenter = MediaGridPresenterImpl(context!!, mConfiguration!!.image).apply {
            setMediaGridView(this@MediaGridFragment)
        }

        LinearLayoutManager(context).apply {
            orientation = GridLayoutManager.VERTICAL
        }.run {
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
            rv_bucket.layoutManager = this
        }

        BucketAdapter(
            mBucketEntityList, mConfiguration!!, ContextCompat.getColor(
                context!!, R.color.gallery_bucket_list_item_normal_color
            )
        ).apply {
            setOnItemClickListener { _, _, entity ->
                val bucketId = entity.bucketId
                rl_bucket_overview.visibility = View.GONE
                if (TextUtils.equals(mBucketId, bucketId)) {
                    return@setOnItemClickListener
                }
                mBucketId = bucketId
                EmptyViewUtils.showLoading(ll_empty_view)
                rv_media.setHasLoadMore(false)
                mMediaEntityList.clear()
                mMediaGridAdapter.notifyDataSetChanged()
                tv_folder_name.text = entity.bucketName
                mBucketAdapter.setSelectedBucket(entity)
                rv_media.setFooterViewHide(true)
                mPage = 1
                mMediaGridPresenter.getMediaList(mBucketId!!, mPage, LIMIT)
            }
        }.run {
            mBucketAdapter = this
            rv_bucket.adapter =this
        }
        rv_media.setOnItemClickListener(this)
        mMediaGridPresenter.getBucketList()

        rl_bucket_overview.visibility = View.INVISIBLE

        if (slideInUnderneathAnimation == null) {
            slideInUnderneathAnimation = SlideInUnderneathAnimation(rv_bucket)
        }

        slideInUnderneathAnimation?.run {
            direction = Animation.DIRECTION_DOWN
            animate()
        }

        subscribeEvent()

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
            mMediaGridPresenter.getMediaList(mBucketId!!, mPage, LIMIT)
        }
    }

    override fun setTheme() {
        super.setTheme()
        uCropStatusColor = ThemeUtils.resolveColor(
            activity,
            R.attr.gallery_ucrop_status_bar_color,
            R.color.gallery_default_ucrop_color_widget_active
        )
        uCropToolbarColor = ThemeUtils.resolveColor(
            activity,
            R.attr.gallery_ucrop_toolbar_color,
            R.color.gallery_default_ucrop_color_widget_active
        )
        uCropActivityWidgetColor = ThemeUtils.resolveColor(
            activity,
            R.attr.gallery_ucrop_activity_widget_color,
            R.color.gallery_default_ucrop_color_widget
        )
        uCropToolbarWidgetColor = ThemeUtils.resolveColor(
            activity,
            R.attr.gallery_ucrop_toolbar_widget_color,
            R.color.gallery_default_toolbar_widget_color
        )
        uCropTitle = ThemeUtils.resolveString(
            activity,
            R.attr.gallery_ucrop_toolbar_title,
            R.string.gallery_edit_phote
        )
        val pageColor = ThemeUtils.resolveColor(
            context,
            R.attr.gallery_page_bg,
            R.color.gallery_default_page_bg
        )
        rl_root_view.setBackgroundColor(pageColor)
        requestStorageAccessPermissionTips = ThemeUtils.resolveString(
            context,
            R.attr.gallery_request_camera_permission_tips,
            R.string.gallery_default_camera_access_permission_tips
        )


    }

    /**
     * RxBus
     */
    private fun subscribeEvent() {
        RxBus.getDefault().run {
            toObservable(MediaCheckChangeEvent::class.java)
                .subscribeWith(object : RxBusDisposable<MediaCheckChangeEvent>() {
                    override fun onEvent(t: MediaCheckChangeEvent) {
                        tv_preview.isEnabled = !(context as MediaActivity).mCheckedList.isNullOrEmpty()
                    }
                }).run {
                    mMediaCheckChangeDisposable = this
                    add(this)
                }

            toObservable(CloseMediaViewPageFragmentEvent::class.java)
                .subscribeWith(object : RxBusDisposable<CloseMediaViewPageFragmentEvent>() {
                    @Throws(Exception::class)
                    override fun onEvent(t: CloseMediaViewPageFragmentEvent) {
                        mMediaGridAdapter.notifyDataSetChanged()
                    }
                })
                .run {
                    mCloseMediaViewPageFragmentDisposable = this
                    add(this)
                }

            toObservable(RequestStorageReadAccessPermissionEvent::class.java)
                .subscribeWith(object : RxBusDisposable<RequestStorageReadAccessPermissionEvent>() {
                    @Throws(Exception::class)
                    override fun onEvent(t: RequestStorageReadAccessPermissionEvent) {
                        if (t.type == RequestStorageReadAccessPermissionEvent.TYPE_WRITE) {
                            if (t.success) {
                                mMediaGridPresenter.getMediaList(mBucketId!!, mPage, LIMIT)
                            } else {
                                activity?.finish()
                            }
                        } else {
                            if (t.success) {
                                openCamera(context as MediaActivity)
                            }
                        }
                    }
                }).run {
                    add(this)
                }
        }
    }

    fun openCamera(context: Context?) {
        val image = mConfiguration!!.image

        val captureIntent = if (image) Intent(MediaStore.ACTION_IMAGE_CAPTURE) else
            Intent(MediaStore.ACTION_VIDEO_CAPTURE)

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
        Logger.i("openCamera：${mImageStoreDir?.absolutePath}")
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

    override fun onStart() {
        super.onStart()
        onLoadFile()
    }

    override fun onFirstTimeLaunched() {}

    override fun onRestoreState(savedInstanceState: Bundle) {}

    override fun onSaveState(outState: Bundle) {}

    fun isShowRvBucketView() = rl_bucket_overview != null && rl_bucket_overview.visibility == View.VISIBLE

    private fun showRvBucketView() {
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
        val entity = mMediaEntityList[position]
        if (entity.id == Integer.MIN_VALUE.toLong()) {

            if (!CameraUtils.hasCamera(context!!)) {
                Toast.makeText(context, R.string.gallery_device_no_camera_tips, Toast.LENGTH_SHORT)
                    .show()
                return
            }

            takeIf {
                PermissionCheckUtils.checkCameraPermission(
                    context as MediaActivity,
                    requestStorageAccessPermissionTips!!,
                    MediaActivity.REQUEST_CAMERA_ACCESS_PERMISSION
                )    
            }.run {
                openCamera(context as MediaActivity)
            }
             
        } else {
            if (mConfiguration!!.radio) {
                if (mConfiguration!!.image) {
                    radioNext(entity)
                } else {
                    videoRadioNext(entity)
                }
            } else {
                val firstEntity = mMediaEntityList[0]
                val gridMediaList = ArrayList<MediaEntity>()
                gridMediaList.addAll(mMediaEntityList)
                var pos = position
                if (firstEntity.id == Integer.MIN_VALUE.toLong()) {
                    pos = position - 1
                    gridMediaList.clear()
                    val list = mMediaEntityList.subList(1, mMediaEntityList.size)
                    gridMediaList.addAll(list)
                }
                RxBus.getDefault().post(OpenMediaPageFragmentEvent(gridMediaList, pos))
            }
        }
    }

    private fun radioNext(entity: MediaEntity) = mConfiguration?.run {
        Logger.i("isCrop :" + this.crop)
        if (!this.crop) {
            this@MediaGridFragment.setPostMediaEntity(entity)
            activity?.finish()
        } else {
            //裁剪根据大家需求加上选择完图片后的回调
            this@MediaGridFragment.setPostMediaEntity(entity)
            val originalPath = entity.originalPath
            val file = File(originalPath!!)
            val random = Random()
            val outName = String.format(
                IMAGE_STORE_FILE_NAME,
                SimpleDateUtils.getNowTime() + "_" + random.nextInt(1024)
            )
            Logger.i("--->isCrop:$mImageStoreCropDir")
            Logger.i("--->mediaBean.getOriginalPath():${entity.originalPath}" )
            mCropPath = File(mImageStoreCropDir, outName)
            val outUri = Uri.fromFile(mCropPath)
            if (!mImageStoreCropDir!!.exists()) {
                mImageStoreCropDir!!.mkdirs()
            }
            if (!file.exists()) {
                file.mkdirs()
            }
            val inputUri = Uri.fromFile(File(entity.originalPath!!))
            val intent = Intent(this@MediaGridFragment.context, UCropActivity::class.java)

            // UCrop 参数 start
            Bundle().apply {
                putParcelable(UCrop.EXTRA_OUTPUT_URI, outUri)
                putParcelable(UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS, entity)
                putInt(UCrop.Options.EXTRA_STATUS_BAR_COLOR, uCropStatusColor)
                putInt(UCrop.Options.EXTRA_TOOL_BAR_COLOR, uCropToolbarColor)
                putString(UCrop.Options.EXTRA_UCROP_TITLE_TEXT_TOOLBAR, uCropTitle)
                putInt(UCrop.Options.EXTRA_UCROP_COLOR_WIDGET_ACTIVE, uCropActivityWidgetColor)
                putInt(UCrop.Options.EXTRA_UCROP_WIDGET_COLOR_TOOLBAR, uCropToolbarWidgetColor)
                putBoolean(
                    UCrop.Options.EXTRA_HIDE_BOTTOM_CONTROLS,
                    hideBottomControls
                )
                putIntArray(
                    UCrop.Options.EXTRA_ALLOWED_GESTURES,
                    gestures
                )
                putInt(
                    UCrop.Options.EXTRA_COMPRESSION_QUALITY,
                    compressionQuality
                )
                putInt(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, mConfiguration!!.maxBitmapSize)
                putFloat(
                    UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER,
                    maxScaleMultiplier
                )
                putFloat(UCrop.EXTRA_ASPECT_RATIO_X, aspectRatioX)
                putFloat(UCrop.EXTRA_ASPECT_RATIO_Y, aspectRatioY)
                putInt(UCrop.EXTRA_MAX_SIZE_X, maxResultWidth)
                putInt(UCrop.EXTRA_MAX_SIZE_Y, maxResultHeight)
                putInt(
                    UCrop.Options.EXTRA_ASPECT_RATIO_SELECTED_BY_DEFAULT,
                    selectedByDefault
                )
                putBoolean(
                    UCrop.Options.EXTRA_FREE_STYLE_CROP,
                    freestyleCropEnabled
                )
                putParcelable(UCrop.EXTRA_INPUT_URI, inputUri)
                // UCrop 参数 end

                ArrayList<AspectRatio>().apply {
                    if (aspectRatio!=null){
//                    Logger.i("自定义比例=>" + aspectRatioList[i].aspectRatioX + " " + aspectRatioList[i].aspectRatioY)
                        addAll(aspectRatio!!)
                    }
                }.run {
                    //  AspectRatio[]aspectRatios =  mConfiguration.getAspectRatio();
                    putParcelableArrayList(
                        UCrop.Options.EXTRA_ASPECT_RATIO_OPTIONS,
                        this
                    )//EXTRA_CONFIGURATION
                }
            }.run {
                intent.putExtras(this)
            }



            val bk = FileUtils.existImageDir(inputUri.path!!)
            Logger.i("--->" + inputUri.path!!)
            Logger.i("--->" + outUri.path!!)

            if (bk != -1) {
                //裁剪
                startActivityForResult(intent, CROP_IMAGE_REQUEST_CODE)
            } else {
                Logger.w("点击图片无效")
            }
        }
    }


    private fun videoRadioNext(entity:MediaEntity){
        if (!mConfiguration!!.isVideoPreview) {
            this.setPostMediaEntity(entity)
            activity?.finish()
            return
        }
        try {
            val openVideo = Intent(Intent.ACTION_VIEW)
            openVideo.setDataAndType(Uri.parse(entity.originalPath), "video/*")
            startActivity(openVideo)
        } catch (e: Exception) {
            Toast.makeText(context, "启动播放器失败", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 处理回调
     */
    private fun setPostMediaEntity(mediaBean: MediaEntity) = ImageCropEntity().run{
        copyMediaEntity(mediaBean)
        RxBus.getDefault().post(ImageRadioResultEvent(this))
    }

    override fun loadMore() {
        mMediaGridPresenter.getMediaList(mBucketId!!, mPage, LIMIT)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.i("onActivityResult: requestCode=$requestCode, resultCode=$resultCode")
        if (requestCode == TAKE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Logger.i(String.format("拍照成功,图片存储路径:%s", mImagePath))
            mMediaScanner.scanFile(
                mImagePath!!, if (mConfiguration!!.image) IMAGE_TYPE else "", this
            )
        } else if (requestCode == 222) {
            Toast.makeText(activity, "摄像成功", Toast.LENGTH_SHORT).show()
        } else if (requestCode == CROP_IMAGE_REQUEST_CODE) {
            Logger.i("裁剪成功")
            refreshUI()
            onCropFinished()
        }
    }

    /**
     * Observable刷新图库
     */
    private fun refreshUI() {
        try {
            Logger.i("->getImageStoreDirByFile().getPath().toString()：${getImageStoreDirByFile()?.path}" )
            Logger.i("->getImageStoreCropDirByStr ().toString()：${getImageStoreCropDirByStr()}")
            if (!TextUtils.isEmpty(mImagePath))
                mMediaScanner.scanFile(mImagePath!!, IMAGE_TYPE, this)
            if (mCropPath != null) {
                Logger.i("""->mCropPath:${mCropPath?.path} $IMAGE_TYPE""")
                mMediaScanner.scanFile(mCropPath?.path!!, IMAGE_TYPE, this)
            }
        } catch (e: Exception) {
            Logger.e(e.message)
        }
    }


    /**
     * 裁剪之后
     * setResult(RESULT_OK, new Intent()
     * .putExtra(UCrop.EXTRA_OUTPUT_URI, uri)
     * .putExtra(UCrop.EXTRA_OUTPUT_CROP_ASPECT_RATIO, resultAspectRatio)
     * .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_WIDTH, imageWidth)
     * .putExtra(UCrop.EXTRA_OUTPUT_IMAGE_HEIGHT, imageHeight)
     */
    private fun onCropFinished() {
        if (iListenerRadio != null && mCropPath != null) {
            if (mConfiguration!!.crop) {
                iListenerRadio?.cropAfter(mCropPath!!)
            }
        } else {
            Logger.i("# CropPath is null！# ")
        }
        //裁剪默认会关掉这个界面. 实现接口返回true则不关闭.
        if (iListenerRadio == null) {
            activity?.finish()
        } else {
            val flag = iListenerRadio?.isActivityFinish()
            Logger.i("# crop image is flag # :$flag")
            if (flag==null||flag)
                activity?.finish()
        }
    }

    override fun onScanCompleted(images: Array<String>?) {
        if (images.isNullOrEmpty()) {
            Logger.i("images empty")
            return
        }

        // mediaBean 有可能为Null，onNext 做了处理，在 getMediaBeanWithImage 时候就不处理Null了
        Observable.create(ObservableOnSubscribe<MediaEntity> {
            val entity = if (mConfiguration!!.image)
                MediaUtils.getMediaEntityWithImage(context!!, images[0])
            else
                MediaUtils.getMediaEntityWithVideo(context!!, images[0])
            it.onNext(entity!!)
            it.onComplete()
        })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : DisposableObserver<MediaEntity>() {
                override fun onComplete() {}

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Logger.i("获取MediaEntity异常$e")
                }

                override fun onNext(entity : MediaEntity) {
                    if (!isDetached) {
                        val bk = FileUtils.existImageDir(entity.originalPath)
                        if (bk != -1) {
                            mMediaEntityList.add(1, entity)
                            mMediaGridAdapter.notifyDataSetChanged()
                        } else {
                            Logger.i("获取：无")
                        }
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        RxBus.getDefault().remove(mMediaCheckChangeDisposable)
        RxBus.getDefault().remove(mCloseMediaViewPageFragmentDisposable)
    }

    /**
     * onAttach 转 onStart
     */
    private fun onLoadFile() {
        //没有的话就默认路径
        if (getImageStoreDirByFile() == null && getImageStoreDirByStr() == null) {
            mImageStoreDir = File(BaseUtils.getExternalDirectory(), "/DCIM/IMMQY/")
            setImageStoreCropDir(mImageStoreDir!!)
        }
        if (!mImageStoreDir!!.exists()) {
            mImageStoreDir!!.mkdirs()
        }
        if (getImageStoreCropDirByFile() == null && getImageStoreCropDirByStr() == null) {
            mImageStoreCropDir = File(mImageStoreDir, "crop")
            if (!mImageStoreCropDir!!.exists()) {
                mImageStoreCropDir!!.mkdirs()
            }
            setImageStoreCropDir(mImageStoreCropDir!!)
        }
    }

    override fun onRequestMediaCallback(list: List<MediaEntity>?) {
        if (!mConfiguration!!.hideCamera) {
            if (mPage == 1 && TextUtils.equals(mBucketId, Integer.MIN_VALUE.toString())) {
                MediaEntity().run {
                    id = Integer.MIN_VALUE.toLong()
                    bucketId = Integer.MIN_VALUE.toString()
                    mMediaEntityList.add(this)
                }
            }
        }
        if (!list.isNullOrEmpty()) {
            mMediaEntityList.addAll(list)
            Logger.i(String.format("得到:%s张图片", list.size))
        } else {
            Logger.i("没有更多图片")
        }
        mMediaGridAdapter.notifyDataSetChanged()

        mPage++

        if (list == null || list.size < LIMIT) {
            rv_media.setFooterViewHide(true)
            false
        } else {
            rv_media.setFooterViewHide(false)
            true
        }.run {
            rv_media.setHasLoadMore(this)
        }

        if (mMediaEntityList.isNullOrEmpty()) {
            ThemeUtils.resolveString(
                context,
                R.attr.gallery_media_empty_tips,
                R.string.gallery_default_media_empty_tips
            ).run {
                EmptyViewUtils.showMessage(ll_empty_view, this)
            }
        }

        rv_media.onLoadMoreComplete()
    }

    override fun onRequestBucketCallback(list: List<BucketEntity>?) {
        if (list.isNullOrEmpty()) {
            return
        }

        mBucketEntityList.addAll(list)
        mBucketAdapter.setSelectedBucket(list[0])
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!TextUtils.isEmpty(mImagePath)) {
            outState.putString(TAKE_URL_STORAGE_KEY, mImagePath)
        }
        if (!TextUtils.isEmpty(mBucketId)) {
            outState.putString(BUCKET_ID_KEY, mBucketId)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState == null) {
            return
        }
        mImagePath = savedInstanceState.getString(TAKE_URL_STORAGE_KEY)
        mBucketId = savedInstanceState.getString(BUCKET_ID_KEY)
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaScanner.unScanFile()
    }

}