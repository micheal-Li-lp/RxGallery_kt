package com.micheal.rxgallery.ui.activity

import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.micheal.rxgallery.BuildConfig
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.utils.Logger

abstract class BaseActivity :AppCompatActivity(){
    companion object{
        const val EXTRA_PREFIX = BuildConfig.APPLICATION_ID
        const val EXTRA_CONFIGURATION = "$EXTRA_PREFIX.Configuration"
        private val CLASS_NAME = BaseActivity::class.java.simpleName
    }


    var mConfiguration : Configuration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printActivityLife("onCreate")

        var bundle = intent.extras

        if (savedInstanceState != null) {
            mConfiguration = savedInstanceState.getParcelable<Parcelable>(EXTRA_CONFIGURATION) as Configuration
        }

        if (mConfiguration == null && bundle != null) {
            mConfiguration = bundle.getParcelable<Parcelable>(EXTRA_CONFIGURATION) as Configuration
        }

        if (mConfiguration == null) {
            finish()
        } else {
            if (bundle == null) {
                bundle = savedInstanceState
            }
            setContentView(getContentView())
            findViews()
            setTheme()
            onCreateOk(bundle)
        }
    }
    @LayoutRes
    abstract fun getContentView(): Int


    protected abstract fun onCreateOk(savedInstanceState: Bundle?)

    override fun onStart() {
        super.onStart()
        printActivityLife("onStart")
    }

    override fun onRestart() {
        super.onRestart()
        printActivityLife("onRestart")
    }

    override fun onResume() {
        super.onResume()
        printActivityLife("onRestart")
    }

    override fun onPause() {
        super.onPause()
        printActivityLife("onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        printActivityLife("onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        printActivityLife("onSaveInstanceState")
        outState.putParcelable(EXTRA_CONFIGURATION, mConfiguration)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        super.onRestoreInstanceState(savedInstanceState)
        printActivityLife("onRestoreInstanceState")
        mConfiguration = savedInstanceState.getParcelable<Parcelable>(EXTRA_CONFIGURATION) as Configuration
    }

    abstract fun findViews()

    protected abstract fun setTheme()

    private fun printActivityLife(method: String) {
        Logger.i(String.format("Activity:%s Method:%s", CLASS_NAME, method))
    }
}