package com.micheal.rxgallery.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.imageloader.FrescoImageLoader
import com.micheal.rxgallery.rxbus.RxBus
import com.micheal.rxgallery.rxbus.event.MediaCheckChangeEvent
import com.micheal.rxgallery.rxjob.RxJob
import com.micheal.rxgallery.rxjob.job.ImageThmbnailJobCreate
import com.micheal.rxgallery.ui.activity.MediaActivity
import com.micheal.rxgallery.ui.base.BaseAdapter
import com.micheal.rxgallery.ui.base.BaseHolder
import com.micheal.rxgallery.ui.base.IMultiImageCheckedListener
import com.micheal.rxgallery.ui.widget.SquareLinearLayout
import com.micheal.rxgallery.utils.Logger
import com.micheal.rxgallery.utils.OsCompat
import com.micheal.rxgallery.utils.ThemeUtils
import java.io.File

class MediaGridAdapter(private val mMediaActivity :MediaActivity,
                       private val list: List<MediaEntity>,
                       screenWidth :Int,
                       private val configuration: Configuration)
    : BaseAdapter<MediaEntity>(list){

    companion object{
        var iMultiImageCheckedListener: IMultiImageCheckedListener? = null
    }

    private var mImageSize: Int = screenWidth / 3

    private var mDefaultImage = ThemeUtils.resolveDrawableRes(
        mMediaActivity,
        R.attr.gallery_default_image,
        R.drawable.gallery_default_image
    ).run {
        ContextCompat.getDrawable(mMediaActivity, this)
    }

    private var mImageViewBg = ThemeUtils.resolveDrawable(
        mMediaActivity,
        R.attr.gallery_imageview_bg,
        R.drawable.gallery_default_image
    )

    private var mCameraImage = ThemeUtils.resolveDrawable(
        mMediaActivity,
        R.attr.gallery_camera_image,
        R.drawable.gallery_ic_camera
    )

    private var mCameraImageBgColor = ThemeUtils.resolveColor(
        mMediaActivity,
        R.attr.gallery_camera_bg,
        R.color.gallery_default_camera_bg_color
    )

    private var mCameraTextColor = ThemeUtils.resolveColor(
        mMediaActivity,
        R.attr.gallery_take_image_text_color,
        R.color.gallery_default_take_image_text_color
    )

    private var imageLoaderType = configuration.imageLoaderType

    override fun getData() = list

    override fun getHolder(view: View, viewType: Int) = GridViewHolder(view)

    override fun getLayoutId(viewType: Int) = viewType

    override fun getItemViewType(position: Int) = getItemViewType()

    override fun getItemViewType() = if (imageLoaderType!=3){
        R.layout.item_gallery_media_grid
    }else{
        R.layout.item_gallery_media_grid_fresco
    }

    override fun onViewClick(view: View, position: Int) {

    }

    inner class GridViewHolder(itemView :View) : BaseHolder<MediaEntity>(itemView){
        override fun setData(data: MediaEntity, position: Int) {

            if (data.id == Integer.MIN_VALUE.toLong()) {
                findViewById<AppCompatCheckBox>(R.id.cb_check).visibility = View.GONE
                findViewById<View>(R.id.iv_media_image).visibility = View.GONE
                findViewById<SquareLinearLayout>(R.id.ll_camera).visibility = View.VISIBLE
                findViewById<ImageView>(R.id.iv_camera_image).setImageDrawable(mCameraImage)
                findViewById<TextView>(R.id.tv_camera_txt).run {
                    setTextColor(mCameraTextColor)
                    text = if (configuration.image) mMediaActivity.getString(R.string.gallery_take_image) else mMediaActivity.getString(
                        R.string.gallery_video
                    )
                    setBackgroundColor(mCameraImageBgColor)
                }
            } else {
                findViewById<AppCompatCheckBox>(R.id.cb_check).run {
                    visibility = if (configuration.radio) {
                        View.GONE
                    } else {
                        setOnClickListener { view->
                            if (configuration.maxSize == mMediaActivity.mCheckedList.size && !mMediaActivity.mCheckedList.contains(
                                    data
                                )
                            ) {
                                val checkBox = view as AppCompatCheckBox
                                checkBox.isChecked = false
                                Logger.i(
                                    "=>" + mMediaActivity.resources.getString(
                                        R.string.gallery_image_max_size_tip,
                                        configuration.maxSize
                                    )
                                )
                            } else {
                                RxBus.post(MediaCheckChangeEvent(data))
                            }
                        }
                        setOnCheckedChangeListener{view,isCheck->
                            if (configuration.maxSize == mMediaActivity.mCheckedList.size && !mMediaActivity.mCheckedList.contains(
                                    data
                                )
                            ) {
                                val checkBox = view as AppCompatCheckBox
                                checkBox.isChecked = false
                                Logger.i(
                                    "选中：" + mMediaActivity.resources.getString(
                                        R.string.gallery_image_max_size_tip,
                                        configuration.maxSize
                                    )
                                )
                                if (iMultiImageCheckedListener != null) {
                                    iMultiImageCheckedListener?.selectedImgMax(
                                        view,
                                        isCheck,
                                        configuration.maxSize
                                    )
                                }
                            } else {
                                if (iMultiImageCheckedListener != null)
                                    iMultiImageCheckedListener?.selectedImg(view, isCheck)
                            }
                        }
                        View.VISIBLE
                    }
                }

                findViewById<View>(R.id.iv_media_image).visibility = View.VISIBLE
                findViewById<SquareLinearLayout>(R.id.ll_camera).visibility = View.GONE
                findViewById<AppCompatCheckBox>(R.id.cb_check).isChecked = mMediaActivity.mCheckedList.contains(
                    data
                )

                if (!File(data.thumbnailBigPath!!).exists() || !File(data.thumbnailSmallPath!!).exists()) {
                    val job = ImageThmbnailJobCreate(mMediaActivity, data).create()
                    RxJob.addJob(job)
                }
                val path = if (configuration.isPlayGif && (imageLoaderType == 3 || imageLoaderType == 2)) {
                   data.originalPath
                } else {
                    if (!data.thumbnailSmallPath.isNullOrEmpty()){
                        data.thumbnailSmallPath
                    }else if (!data.thumbnailBigPath.isNullOrEmpty()){
                        data.thumbnailBigPath
                    }else{
                        data.originalPath
                    }
                }
                Logger.w("提示path：$path")
                if (imageLoaderType != 3) {
                    OsCompat.setBackgroundDrawableCompat(findViewById(R.id.iv_media_image), mImageViewBg)
                    configuration.getImageLoader()
                        .displayImage(
                            mMediaActivity,
                            path!!,
                            findViewById(R.id.iv_media_image),
                            mDefaultImage,
                            configuration.getImageConfig(),
                            true,
                            configuration.isPlayGif,
                            mImageSize,
                            mImageSize,
                            data.orientation
                        )
                } else {
                    OsCompat.setBackgroundDrawableCompat(findViewById(R.id.iv_media_image), mImageViewBg)
                    FrescoImageLoader.setImageSmall(
                        "file://$path", findViewById(R.id.iv_media_image),
                        mImageSize, mImageSize, findViewById(R.id.rootView), configuration.isPlayGif
                    )
                }
            }
        }
    }
}