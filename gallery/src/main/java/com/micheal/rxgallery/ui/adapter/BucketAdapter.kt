package com.micheal.rxgallery.ui.adapter

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.R
import com.micheal.rxgallery.entity.BucketEntity
import com.micheal.rxgallery.ui.base.BaseAdapter
import com.micheal.rxgallery.ui.base.BaseHolder

class BucketAdapter(private val list: List<BucketEntity> ,private val configuration: Configuration, @ColorInt color :Int)
    :BaseAdapter<BucketEntity>(list){

    private var mDefaultImage: Drawable?=null
    private var mSelectedBucket: BucketEntity? = null
    private var itemClickListener :((view:View,position:Int,entity:BucketEntity)->Unit)?=null

    init {
        this.mDefaultImage = ColorDrawable(color)
    }

    override fun getData() = list

    override fun getHolder(view: View, viewType: Int) = BucketViewHolder(view)

    override fun getLayoutId(viewType: Int) = R.layout.gallery_adapter_bucket_item

    override fun onViewClick(view: View, position: Int) {

    }

    fun setSelectedBucket(bucketBean: BucketEntity) {
        this.mSelectedBucket = bucketBean
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener:((view:View, position:Int, entity:BucketEntity)->Unit)?){
        this.itemClickListener=listener
    }

    inner class BucketViewHolder(view: View) : BaseHolder<BucketEntity>(view)
        ,View.OnClickListener{
        override fun setData(data: BucketEntity, position: Int) {

        }

    }

}