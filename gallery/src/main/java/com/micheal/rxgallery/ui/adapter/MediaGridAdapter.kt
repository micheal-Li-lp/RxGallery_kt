package com.micheal.rxgallery.ui.adapter

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import com.micheal.rxgallery.ui.activity.MediaActivity
import com.micheal.rxgallery.ui.base.BaseAdapter
import com.micheal.rxgallery.ui.base.BaseHolder
import com.micheal.rxgallery.ui.base.IMultiImageCheckedListener
import com.micheal.rxgallery.utils.ThemeUtils

class MediaGridAdapter(private val mMediaActivity :MediaActivity,
                       private val list: List<MediaEntity>,
                       screenWidth :Int,
                       private val configuration: Configuration)
    : BaseAdapter<MediaEntity>(list){

    private var iMultiImageCheckedListener: IMultiImageCheckedListener? = null
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

    override fun getHolder(view: View, viewType: Int): BaseHolder<MediaEntity> {
        return GridViewHolder(view)
    }

    override fun getLayoutId(viewType: Int): Int {
        return if (viewType!=3){
            R.layout.item_gallery_media_grid
        }else{
            R.layout.item_gallery_media_grid_fresco
        }
    }

    override fun getItemViewType(position: Int): Int {
        return imageLoaderType
    }

    override fun onViewClick(view: View, position: Int) {

    }


    inner class GridViewHolder(itemView :View) : BaseHolder<MediaEntity>(itemView){
        override fun setData(data: MediaEntity, position: Int) {

        }

    }
}