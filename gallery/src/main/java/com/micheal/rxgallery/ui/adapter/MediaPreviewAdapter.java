package com.micheal.rxgallery.ui.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.micheal.rxgallery.Configuration;
import com.micheal.rxgallery.R;
import com.micheal.rxgallery.entity.MediaEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Desction:
 * Author:pengjianbo  Dujinyang
 * Date:16/7/21 下午10:12
 */
public class MediaPreviewAdapter extends RecyclingPagerAdapter {

    private final List<MediaEntity> mMediaList;
    private final Configuration mConfiguration;
    private final Drawable mDefaultImage;
    private final int mScreenWidth;
    private final int mScreenHeight;
    private final int mPageColor;

    public MediaPreviewAdapter(List<MediaEntity> list,
                               int screenWidth,
                               int screenHeight,
                               Configuration configuration,
                               int pageColor,
                               Drawable drawable) {
        this.mMediaList = list;
        this.mScreenWidth = screenWidth;
        this.mScreenHeight = screenHeight;
        this.mConfiguration = configuration;
        this.mPageColor = pageColor;
        this.mDefaultImage = drawable;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup container) {
        MediaEntity mediaBean = mMediaList.get(position);
        if (convertView == null) {
            convertView = View.inflate(container.getContext(), R.layout.gallery_media_image_preview_item, null);
        }
        PhotoView ivImage = convertView.findViewById(R.id.iv_media_image);
        String path = null;
        if (mediaBean.getWidth() > 1200 || mediaBean.getHeight() > 1200) {
            path = mediaBean.getThumbnailBigPath();
        }
        if (TextUtils.isEmpty(path)) {
            path = mediaBean.getOriginalPath();
        }
        ivImage.setBackgroundColor(mPageColor);
        mConfiguration.getImageLoader().displayImage(container.getContext(), path, ivImage, mDefaultImage, mConfiguration.getImageConfig(),
                false, mConfiguration.isPlayGif(), mScreenWidth, mScreenHeight, mediaBean.getOrientation());
        return convertView;
    }

    @Override
    public int getCount() {
        return mMediaList.size();
    }
}
