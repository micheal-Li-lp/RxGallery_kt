package com.micheal.rxgallery.anim

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.annotation.TargetApi
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout


@TargetApi(14)
class SlideInUnderneathAnimation(view: View) : Animation(){
    var direction :Int? = 0
    var interpolator : TimeInterpolator
    var duration :Long? = 0
    var listener : AnimationListener?=null

    init {
        this.view=view
        direction = DIRECTION_LEFT
        interpolator = AccelerateDecelerateInterpolator()
        duration = DURATION_LONG

    }

    override fun animate() {
        val parentView = view.parent as ViewGroup
        val slideFrame = FrameLayout(view.context)
        val positionView = parentView.indexOfChild(view)
        slideFrame.layoutParams = view.layoutParams
        slideFrame.clipChildren = true
        parentView.removeView(view)
        slideFrame.addView(view)
        parentView.addView(slideFrame,positionView)

        val viewWidth = view.width.toFloat()
        val viewHeight = view.height.toFloat()
        val slideInAnim = when(direction){
            DIRECTION_LEFT -> {
                view.translationX = -viewWidth
                ObjectAnimator.ofFloat(view,View.TRANSLATION_X,slideFrame.x)
            }
            DIRECTION_RIGHT->{
                view.translationX = viewWidth
                ObjectAnimator.ofFloat(view,View.TRANSLATION_X,slideFrame.x)
            }
            DIRECTION_UP->{
                view.translationY = -viewHeight
                ObjectAnimator.ofFloat(view,View.TRANSLATION_Y,slideFrame.y)
            }
            DIRECTION_DOWN->{
                view.translationY = viewHeight
                ObjectAnimator.ofFloat(view,View.TRANSLATION_Y,slideFrame.y)
            }
            else ->null
        } ?: return

        slideInAnim.interpolator = this.interpolator
        slideInAnim.duration = this.duration!!
        slideInAnim.addListener(object :AnimatorListenerAdapter(){
            override fun onAnimationStart(animation: Animator?) {
                view.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animator?) {
                slideFrame.removeAllViews()
                view.layoutParams = slideFrame.layoutParams
                parentView.addView(view,positionView)
                listener?.onAnimationEnd(this@SlideInUnderneathAnimation)
            }
        })

        slideInAnim.start()
    }
}