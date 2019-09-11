package com.micheal.rxgallery.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.entity.MediaEntity

class MediaPageFragment :BaseFragment(), ViewPager.OnPageChangeListener,
    View.OnClickListener{

    companion object{
        private const val EXTRA_MEDIA_LIST = "$EXTRA_PREFIX.MediaList"
        private const val EXTRA_ITEM_CLICK_POSITION = "$EXTRA_PREFIX.ItemClickPosition"

        @JvmStatic
        fun newInstance(configuration: Configuration,list: ArrayList<MediaEntity>,position: Int) = MediaGridFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_CONFIGURATION,configuration)
                putParcelableArrayList(EXTRA_MEDIA_LIST,list)
                putInt(EXTRA_ITEM_CLICK_POSITION,position)
            }
        }
    }


    override fun onViewCreatedOk(view: View, savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentView(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onFirstTimeLaunched() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRestoreState(savedInstanceState: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSaveState(outState: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageScrollStateChanged(state: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageSelected(position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(p0: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}