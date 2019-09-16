package com.micheal.rxgallery.ui.widget

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class HorizontalDividerItemDecoration(builder:Builder) :FlexibleDividerDecoration(builder){
    private val mMarginProvider = builder.mMarginProvider



    override fun getDividerBound(position: Int, parent: RecyclerView?, child: View?): Rect {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setItemOffsets(outRect: Rect?, position: Int, parent: RecyclerView?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    /**
     * Interface for controlling divider margin
     */
    interface MarginProvider {

        /**
         * Returns left margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return left margin
         */
        fun dividerLeftMargin(position: Int, parent: RecyclerView): Int

        /**
         * Returns right margin of divider.
         *
         * @param position Divider position (or group index for GridLayoutManager)
         * @param parent   RecyclerView
         * @return right margin
         */
        fun dividerRightMargin(position: Int, parent: RecyclerView): Int
    }


    class Builder(context: Context) : FlexibleDividerDecoration.Builder<Builder>(context) {

        internal var mMarginProvider: MarginProvider = object : MarginProvider {
            override fun dividerLeftMargin(position: Int, parent: RecyclerView): Int {
                return 0
            }

            override fun dividerRightMargin(position: Int, parent: RecyclerView): Int {
                return 0
            }
        }

        fun margin(leftMargin: Int, rightMargin: Int): Builder {
            return marginProvider(object : MarginProvider {
                override fun dividerLeftMargin(position: Int, parent: RecyclerView): Int {
                    return leftMargin
                }

                override fun dividerRightMargin(position: Int, parent: RecyclerView): Int {
                    return rightMargin
                }
            })
        }

        fun margin(horizontalMargin: Int): Builder {
            return margin(horizontalMargin, horizontalMargin)
        }

        fun marginResId(@DimenRes leftMarginId: Int, @DimenRes rightMarginId: Int): Builder {
            return margin(
                mResources.getDimensionPixelSize(leftMarginId),
                mResources.getDimensionPixelSize(rightMarginId)
            )
        }

        fun marginResId(@DimenRes horizontalMarginId: Int): Builder {
            return marginResId(horizontalMarginId, horizontalMarginId)
        }

        fun marginProvider(provider: MarginProvider): Builder {
            mMarginProvider = provider
            return this
        }

        fun build(): HorizontalDividerItemDecoration {
            checkBuilderParams()
            return HorizontalDividerItemDecoration(this)
        }
    }
}