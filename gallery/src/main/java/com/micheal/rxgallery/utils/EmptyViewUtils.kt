package com.micheal.rxgallery.utils

import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

object EmptyViewUtils {

    @JvmStatic
    fun showLoading(emptyView: ViewGroup?){
        if (emptyView == null) {
            return
        }
        val pbLoading = emptyView.getChildAt(0) as ProgressBar
        pbLoading.visibility = View.VISIBLE
        val tvEmptyMsg = emptyView.getChildAt(1) as TextView
        tvEmptyMsg.visibility = View.GONE
    }

    @JvmStatic
    fun showMessage(emptyView : ViewGroup?, msg : String?){
        if (emptyView == null) {
            return
        }
        val pbLoading = emptyView.getChildAt(0) as ProgressBar
        pbLoading.visibility = View.GONE
        val tvEmptyMsg = emptyView.getChildAt(1) as TextView
        tvEmptyMsg.visibility = View.VISIBLE
        tvEmptyMsg.text = msg
    }

}