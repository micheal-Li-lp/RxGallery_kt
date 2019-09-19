package com.micheal.rxgallery.ui.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatRadioButton
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
        itemClickListener?.invoke(view,position,list[position])
    }

    fun setSelectedBucket(entity: BucketEntity) {
        this.mSelectedBucket = entity
        notifyDataSetChanged()
    }

    override fun getItemViewType() = 0

    fun setOnItemClickListener(listener:((view:View, position:Int, entity:BucketEntity)->Unit)?){
        this.itemClickListener = listener
    }

    inner class BucketViewHolder(view: View) : BaseHolder<BucketEntity>(view),
        View.OnClickListener{
        override fun setData(data: BucketEntity, position: Int) {
            findViewById<TextView>(R.id.tv_bucket_name).text = if (position==0){
                 data.bucketName
            }else{
                SpannableString("${data.bucketName}\n${data.imageCount}张").apply {
                    setSpan(
                        ForegroundColorSpan(Color.GRAY),
                        data.bucketName!!.length,
                        length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    setSpan(
                        RelativeSizeSpan(0.8f),
                        data.bucketName!!.length,
                        length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            findViewById<AppCompatRadioButton>(R.id.rb_selected).run {
                visibility =  if (data.bucketId==mSelectedBucket?.bucketId){
                    isChecked=true
                    View.VISIBLE
                }else View.GONE
            }

            configuration.getImageLoader().displayImage(
                itemView.context,
                data.cover!!,
                findViewById(R.id.iv_bucket_cover),
                mDefaultImage,
                configuration.getImageConfig(),
                true,
                configuration.isPlayGif,
                100,
                100,
                data.orientation
            )
//                configuration.getImageLoader().displayImage()
        }

    }

}