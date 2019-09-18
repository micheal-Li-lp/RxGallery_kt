package com.micheal.rxgallery.ui.adapter

import android.os.Build
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.viewpager.widget.PagerAdapter

abstract class RecyclingPagerAdapter(private val recycleBin: RecycleBin) :PagerAdapter(){
    companion object{
        const val IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;
    }

    constructor():this(RecycleBin())

    init {
        this.recycleBin.setViewTypeCount(getViewTypeCount())
    }

    override fun notifyDataSetChanged() {
        recycleBin.scrapActiveViews()
        super.notifyDataSetChanged()
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val viewType = getItemViewType(position)
        var view: View? = null
        if (viewType != IGNORE_ITEM_VIEW_TYPE) {
            view = recycleBin.getScrapView(position, viewType)
        }
        view = getView(position, view, container)
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }


    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val view = obj as View
        container.removeView(view)
        val viewType = getItemViewType(position)
        if (viewType != IGNORE_ITEM_VIEW_TYPE) {
            recycleBin.addScrapView(view, position, viewType)
        }
    }



    override fun getCount() = 0

    /**
     *
     *
     * Returns the number of types of Views that will be created by
     * [.getView]. Each type represents a set of views that can be
     * converted in [.getView]. If the adapter always returns the same
     * type of View for all items, this method should return 1.
     *
     *
     *
     * This method will only be called when when the adapter is set on the
     * the [AdapterView].
     *
     *
     * @return The number of types of Views that will be created by this adapter
     */
    private fun getViewTypeCount() = 1

    /**
     * Get the type of View that will be created by [.getView] for the specified item.
     *
     * @param position The position of the item within the adapter's data set whose view type we
     * want.
     * @return An integer representing the type of View. Two views should share the same type if one
     * can be converted to the other in [.getView]. Note: Integers must be in the
     * range 0 to [.getViewTypeCount] - 1. [.IGNORE_ITEM_VIEW_TYPE] can
     * also be returned.
     * @see .IGNORE_ITEM_VIEW_TYPE
     */
    private// Argument potentially used by subclasses.
    fun getItemViewType(position: Int) = 0

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * [android.view.LayoutInflater.inflate]
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     * we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     * is non-null and of an appropriate type before using. If it is not possible to convert
     * this view to display the correct data, this method can create a new view.
     * Heterogeneous lists can specify their number of view types, so that this View is
     * always of the right type (see [.getViewTypeCount] and
     * [.getItemViewType]).
     * @param container   The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    abstract fun getView(position: Int, convertView: View?, container: ViewGroup): View

    /**
     * The RecycleBin facilitates reuse of views across layouts. The RecycleBin has two levels of
     * storage: ActiveViews and ScrapViews. ActiveViews are those views which were onscreen at the
     * start of a layout. By construction, they are displaying current information. At the end of
     * layout, all views in ActiveViews are demoted to ScrapViews. ScrapViews are old views that
     * could potentially be used by the adapter to avoid allocating views unnecessarily.
     *
     *
     * This class was taken from Android's implementation of [android.widget.AbsListView] which
     * is copyrighted 2006 The Android Open Source Project.
     */
    class RecycleBin {
        /**
         * Views that were on screen at the start of layout. This array is populated at the start of
         * layout, and at the end of layout all view in activeViews are moved to scrapViews.
         * Views in activeViews represent a contiguous range of Views, with position of the first
         * view store in mFirstActivePosition.
         */
        private val activeViews = arrayOfNulls<View>(0)
        private val activeViewTypes = IntArray(0)

        /**
         * Unsorted views that can be used by the adapter as a convert view.
         */
        private var scrapViews: Array<SparseArray<View>?>? = null

        private var viewTypeCount: Int = 0

        private var currentScrapViews: SparseArray<View>? = null

        fun setViewTypeCount(viewTypeCount: Int) {
            if (viewTypeCount < 1) {
                throw IllegalArgumentException("Can't have a viewTypeCount < 1")
            }

            val scrapViews = arrayOfNulls<SparseArray<View>>(viewTypeCount)
            for (i in 0 until viewTypeCount) {
                scrapViews[i] = SparseArray<View>()
            }
            this.viewTypeCount = viewTypeCount
            currentScrapViews = scrapViews[0]
            this.scrapViews = scrapViews
        }

        internal fun shouldRecycleViewType(viewType: Int): Boolean {
            return viewType >= 0
        }

        /**
         * @return A view from the ScrapViews collection. These are unordered.
         */
        internal fun getScrapView(position: Int, viewType: Int): View? {
            if (viewTypeCount == 1) {
                return retrieveFromScrap(currentScrapViews!!, position)
            } else if (viewType >= 0 && viewType < scrapViews!!.size) {
                return retrieveFromScrap(scrapViews!![viewType]!!, position)
            }
            return null
        }

        /**
         * Put a view into the ScrapViews list. These views are unordered.
         *
         * @param scrap The view to add
         */
        internal fun addScrapView(scrap: View, position: Int, viewType: Int) {
            if (viewTypeCount == 1) {
                currentScrapViews!!.put(position, scrap)
            } else {
                scrapViews!![viewType]!!.put(position, scrap)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                scrap.accessibilityDelegate = null
            }
        }

        /**
         * Move all views remaining in activeViews to scrapViews.
         */
        internal fun scrapActiveViews() {
            val activeViews = this.activeViews
            val activeViewTypes = this.activeViewTypes
            val multipleScraps = viewTypeCount > 1

            var scrapViews = currentScrapViews
            val count = activeViews.size
            for (i in count - 1 downTo 0) {
                val victim = activeViews[i]
                if (victim != null) {
                    val whichScrap = activeViewTypes[i]

                    activeViews[i] = null
                    activeViewTypes[i] = -1

                    if (!shouldRecycleViewType(whichScrap)) {
                        continue
                    }

                    if (multipleScraps) {
                        scrapViews = this.scrapViews!![whichScrap]
                    }
                    scrapViews!!.put(i, victim)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        victim.accessibilityDelegate = null
                    }
                }
            }

            pruneScrapViews()
        }

        /**
         * Makes sure that the size of scrapViews does not exceed the size of activeViews.
         * (This can happen if an adapter does not recycle its views).
         */
        private fun pruneScrapViews() {
            val maxViews = activeViews.size
            val viewTypeCount = this.viewTypeCount
            val scrapViews = this.scrapViews
            for (i in 0 until viewTypeCount) {
                val scrapPile = scrapViews!![i]
                var size = scrapPile!!.size()
                val extras = size - maxViews
                size--
                for (j in 0 until extras) {
                    scrapPile.remove(scrapPile.keyAt(size--))
                }
            }
        }

        companion object {
            internal fun retrieveFromScrap(scrapViews: SparseArray<View>, position: Int): View? {
                val size = scrapViews.size()
                if (size > 0) {
                    // See if we still have a view for this position.
                    for (i in 0 until size) {
                        val fromPosition = scrapViews.keyAt(i)
                        val view = scrapViews.get(fromPosition)
                        if (fromPosition == position) {
                            scrapViews.remove(fromPosition)
                            return view
                        }
                    }
                    val index = size - 1
                    val r = scrapViews.valueAt(index)
                    scrapViews.remove(scrapViews.keyAt(index))
                    return r
                } else {
                    return null
                }
            }
        }
    }

}