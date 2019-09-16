package com.micheal.rxgallery.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.micheal.rxgallery.Configuration

class MediaPreviewFragment : BaseFragment(), ViewPager.OnPageChangeListener,
    View.OnClickListener {

    companion object{
        private const val EXTRA_PAGE_INDEX = "$EXTRA_PREFIX.PageIndex"

        @JvmStatic
        fun newInstance(configuration: Configuration?,position: Int) = MediaPreviewFragment().apply {
            arguments = Bundle().apply {
                putParcelable(EXTRA_CONFIGURATION,configuration)
                putInt(EXTRA_PAGE_INDEX,position)
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