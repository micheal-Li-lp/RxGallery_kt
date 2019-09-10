package com.micheal.rxgallery.anim

import android.animation.*
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout

class SlideOutUnderneathAnimation(view: View) : Animation(){
    var direction :Int = 0
    var interpolator : TimeInterpolator
    var duration :Long = 0
    var listener : AnimationListener?=null
    private lateinit var slideAnim: ValueAnimator

    init {
        this.view=view
        direction = DIRECTION_LEFT
        interpolator = AccelerateDecelerateInterpolator()
        duration = DURATION_LONG
    }

    override fun animate() {
        val parentView = view.parent as ViewGroup
        val slideOutFrame = FrameLayout(view.context)
        val positionView = parentView.indexOfChild(view)
        slideOutFrame.layoutParams = view.layoutParams
        slideOutFrame.clipChildren = true
        parentView.removeView(view)
        slideOutFrame.addView(view)
        parentView.addView(slideOutFrame, positionView)

        val viewWidth = view.width.toFloat()
        val viewHeight = view.height.toFloat()
        slideAnim = when(direction){
            DIRECTION_LEFT -> {
                view.translationX = -viewWidth
                ObjectAnimator.ofFloat(
                    view, View.TRANSLATION_X,
                    view.translationX - view.width
                )
            }
            DIRECTION_RIGHT->{
                view.translationX = viewWidth
                ObjectAnimator.ofFloat(
                    view, View.TRANSLATION_X,
                    view.translationX + view.width
                )
            }
            DIRECTION_UP->{
                view.translationY = -viewHeight
                ObjectAnimator.ofFloat(
                    view, View.TRANSLATION_Y,
                    view.translationY - view.height
                )
            }
            DIRECTION_DOWN->{
                view.translationY = viewHeight
                ObjectAnimator.ofFloat(
                    view, View.TRANSLATION_Y,
                    view.translationY + view.height
                )
            }
            else ->null
        } ?: return

        AnimatorSet().apply {
            play(slideAnim)
            interpolator = this@SlideOutUnderneathAnimation.interpolator
            duration = this@SlideOutUnderneathAnimation.duration
            addListener(object :AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.INVISIBLE
                    slideAnim.reverse()
                    slideOutFrame.removeAllViews()
                    parentView.removeView(slideOutFrame)
                    parentView.addView(view, positionView)
                    listener?.onAnimationEnd(
                        this@SlideOutUnderneathAnimation
                    )
                }
            })
        }.run {
            start()
        }
    }

}