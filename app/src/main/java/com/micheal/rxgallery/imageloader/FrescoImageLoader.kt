package com.micheal.rxgallery.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MotionEvent
import android.widget.RelativeLayout
import com.facebook.common.util.UriUtil
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.generic.GenericDraweeHierarchy
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.facebook.drawee.view.DraweeHolder
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.common.RotationOptions
import com.facebook.imagepipeline.request.ImageRequest
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.micheal.rxgallery.ui.widget.FixImageView
import com.micheal.rxgallery.ui.widget.SquareRelativeLayout

class FrescoImageLoader :AbsImageLoader{

    private var draweeHolder : DraweeHolder<GenericDraweeHierarchy>?=null

    companion object{
        fun setImageSmall(url :String,simpleDraweeView: SimpleDraweeView,width: Int,height: Int,
                          relativeLayout: SquareRelativeLayout,playGif :Boolean
        ){
            Uri.parse(url).run {
                ImageRequestBuilder
                    .newBuilderWithSource(this)
                    .setRotationOptions(RotationOptions.autoRotate())
                    .setResizeOptions(ResizeOptions(width, height))
                    .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                    .build()
            }.run {
                Fresco.newDraweeControllerBuilder()
                    .setTapToRetryEnabled(true)
                    .setImageRequest(this)
                    .setAutoPlayAnimations(playGif)
                    .setOldController(simpleDraweeView.controller)
                    .build()
            }.run {
                simpleDraweeView.controller = this
            }
            relativeLayout.layoutParams = RelativeLayout.LayoutParams(width - 5, height)
        }
    }

    private fun init(ctx :Context , defaultDrawable : Drawable){
        if (draweeHolder == null){
            val resources = ctx.resources
            val hierarchy = GenericDraweeHierarchyBuilder(resources)
                .setPlaceholderImage(defaultDrawable)
                .setFailureImage(defaultDrawable)
                .build()
            draweeHolder = DraweeHolder.create(hierarchy,ctx)
        }
    }

    override fun displayImage(
        context: Context,
        path: String,
        imageView: FixImageView,
        defaultDrawable: Drawable,
        config: Bitmap.Config,
        resize: Boolean,
        isGif: Boolean,
        width: Int,
        height: Int,
        rotate: Int
    ) {
        init(context,defaultDrawable)
        imageView.mImageViewListener = object : FixImageView.OnImageViewListener{
            override fun onDetach() {
                draweeHolder?.onDetach()
            }

            override fun onAttach() {
                draweeHolder?.onAttach()
            }

            override fun verifyDrawable(dr: Drawable): Boolean {
                return dr === draweeHolder!!.hierarchy.topLevelDrawable
            }

            override fun onDraw(canvas: Canvas?) {
                imageView.setImageDrawable(
                    draweeHolder?.hierarchy?.topLevelDrawable.run { this }
                        ?: defaultDrawable
                )
            }

            override fun onTouchEvent(event: MotionEvent?): Boolean {
                return draweeHolder!!.onTouchEvent(event)
            }

        }

        val uri = Uri.Builder()
            .scheme(UriUtil.LOCAL_FILE_SCHEME)
            .path(path)
            .build()


        val builder = ImageRequestBuilder.newBuilderWithSource(uri)
            .setAutoRotateEnabled(true)
        if (resize) {
            builder.resizeOptions = ResizeOptions(width, height)
        }
        val request = builder.build()
        val controller = Fresco.newDraweeControllerBuilder()
            .setOldController(draweeHolder?.controller)
            .setImageRequest(request)
            .build()
        draweeHolder?.controller = controller

    }
}