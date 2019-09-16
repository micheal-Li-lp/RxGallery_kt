package com.micheal.rxgallery.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.micheal.rxgallery.BuildConfig
import com.micheal.rxgallery.Configuration
import com.micheal.rxgallery.utils.Logger
import java.util.*

abstract class BaseFragment : Fragment() {
    companion object{
        const val EXTRA_PREFIX = BuildConfig.APPLICATION_ID
        const val EXTRA_CONFIGURATION = "$EXTRA_PREFIX.Configuration"
        private val fragmentStack = Stack<BaseFragment>()
        private val CLASS_NAME = BaseFragment::class.java.simpleName
        private val BUNDLE_KEY = "KEY_$CLASS_NAME"
    }

    var mSaveDataBundle: Bundle? = null
    var mConfiguration: Configuration? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        printFragmentLife("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        printFragmentLife("onCreate")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        printFragmentLife("onViewCreated")

        var argsBundle = arguments

        if (savedInstanceState != null) {
            mConfiguration = savedInstanceState.getParcelable(EXTRA_CONFIGURATION)
        }
        if (mConfiguration == null && argsBundle != null) {
            mConfiguration = argsBundle.getParcelable(EXTRA_CONFIGURATION)
        }

        if (mConfiguration != null) {
            if (argsBundle == null) {
                argsBundle = savedInstanceState
            }
            onViewCreatedOk(view, argsBundle)
            setTheme()
        } else {
            val activity = activity
            if (activity != null && !activity.isFinishing) {
                activity.finish()
            }
        }
    }


    abstract fun onViewCreatedOk(view: View,savedInstanceState: Bundle?)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        printFragmentLife("onCreateView")
        return inflater.inflate(getContentView(), container, false)
    }

    override fun startActivityForResult(intent: Intent, requestCode: Int) {
        Logger.i("startActivityForResult")
        val parentFragment = parentFragment
        if (null != parentFragment) {
            fragmentStack.push(this)
            parentFragment.startActivityForResult(intent, requestCode)
        } else {
            super.startActivityForResult(intent, requestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Logger.i("onActivityResult")
        val fragment = if (fragmentStack.isEmpty()) null else fragmentStack.pop()
        if (null != fragment) {
            fragment.onActivityResult(requestCode, resultCode, data)
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        printFragmentLife("onStart")
    }

    override fun onResume() {
        super.onResume()
        printFragmentLife("onResume")
    }

    override fun onPause() {
        super.onPause()
        printFragmentLife("onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        printFragmentLife("onDestroyView")
        saveStateToArguments()
    }

    override fun onDestroy() {
        super.onDestroy()
        printFragmentLife("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        printFragmentLife("onDetach")
    }
    abstract fun getContentView(): Int

    open fun setTheme(){

    }

    private fun printFragmentLife(method: String) {
        Logger.i(String.format("Fragment:%s Method:%s", CLASS_NAME, method))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        printFragmentLife("onActivityCreated")
        if (!restoreStateFromArguments()) {
            onFirstTimeLaunched()
        }
    }

    protected abstract fun onFirstTimeLaunched()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        printFragmentLife("onSaveInstanceState")
        saveStateToArguments()
    }

    private fun saveStateToArguments() {
        if (view != null) {
            mSaveDataBundle = saveState()
        }

        if (mSaveDataBundle != null) {
            val b = arguments
            b?.putBundle(BUNDLE_KEY, mSaveDataBundle)
        }
    }

    private fun restoreStateFromArguments(): Boolean {
        val b = arguments
        if (b != null) {
            mSaveDataBundle = b.getBundle(BUNDLE_KEY)
            if (mSaveDataBundle != null) {
                restoreState()
                return true
            }
        }
        return false
    }

    /**
     * Restore Instance State Here
     */
    private fun restoreState() {
        if (mSaveDataBundle != null) {
            mConfiguration = mSaveDataBundle!!.getParcelable<Parcelable>(EXTRA_CONFIGURATION) as Configuration
            onRestoreState(mSaveDataBundle!!)
        }
    }

    /**
     * 恢复数据
     */
    protected abstract fun onRestoreState(savedInstanceState: Bundle)

    /**
     * Save Instance State Here
     */
    private fun saveState(): Bundle {
        val state = Bundle()
        state.putParcelable(EXTRA_CONFIGURATION, mConfiguration)
        onSaveState(state)
        return state
    }

    protected abstract fun onSaveState(outState: Bundle)

}