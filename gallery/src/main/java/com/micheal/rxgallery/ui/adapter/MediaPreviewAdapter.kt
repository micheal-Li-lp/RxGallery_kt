package com.micheal.rxgallery.ui.adapter

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.MediaEntity
import uk.co.senab.photoview.PhotoView

class MediaPreviewAdapter (private val mMediaList:List<MediaEntity>, private val mScreenWidth :Int, private val mScreenHeight:Int,
                           private val mConfiguration: Configuration, private val mPageColor:Int,
                           private val mDefaultImage: Drawable?)
    :RecyclingPagerAdapter(){

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var view = convertView

        val mediaBean = mMediaList[position]
        if (convertView == null) {
            view =
                View.inflate(container.context, R.layout.gallery_media_image_preview_item, null)
        }
        val ivImage = view?.findViewById<View>(R.id.iv_media_image) as PhotoView
        var path: String? = null
        if (mediaBean.width > 1200 || mediaBean.height > 1200) {
            path = mediaBean.thumbnailBigPath
        }
        if (TextUtils.isEmpty(path)) {
            path = mediaBean.originalPath
        }
        ivImage.setBackgroundColor(mPageColor)
        mConfiguration.getImageLoader().displayImage(
            container.context,
            path!!,
            ivImage,
            mDefaultImage,
            mConfiguration.getImageConfig1(),
            false,
            mConfiguration.isPlayGif,
            mScreenWidth,
            mScreenHeight,
            mediaBean.orientation
        )
        return view
    }
}