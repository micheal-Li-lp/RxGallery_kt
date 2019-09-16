package com.micheal.rxgallery.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView

open class FixImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    var mImageViewListener : OnImageViewListener?=null

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mImageViewListener?.onDetach()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mImageViewListener?.onAttach()
    }

    override fun verifyDrawable(dr: Drawable): Boolean {
        if (mImageViewListener!=null){
            if (mImageViewListener!!.verifyDrawable(dr)){
                return true
            }
        }
        return super.verifyDrawable(dr)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mImageViewListener?.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mImageViewListener == null){
            return super.onTouchEvent(event)
        }

        return mImageViewListener!!.onTouchEvent(event) || super.onTouchEvent(event)
    }

    interface OnImageViewListener{
        fun onDetach()

        fun onAttach()

        fun verifyDrawable(dr: Drawable):Boolean

        fun onDraw(canvas: Canvas?)

        fun onTouchEvent(event:MotionEvent?) :Boolean

    }

}