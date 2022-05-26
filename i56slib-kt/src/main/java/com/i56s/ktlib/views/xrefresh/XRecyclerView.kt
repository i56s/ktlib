package com.i56s.ktlib.views.xrefresh

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.RecyclerView
import com.i56s.ktlib.utils.LogUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-13 15:01
 * ### 描述：带RecyclerView的刷新控件
 */
class XRecyclerView constructor(
    context: Context,
    attrs: AttributeSet?,
    defstyleAttr: Int
) : MaterialRefreshLayout(context, attrs, defstyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    /**空滑动布局*/
    private val mEmptyScrollView = ScrollView(context).apply {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    val recyclerView = object : RecyclerView(context) {
        override fun setAdapter(adapter: Adapter<*>?) {
            super.setAdapter(adapter)
            val mDataObserver = DataObserver(adapter)
            adapter?.registerAdapterDataObserver(mDataObserver)
            mDataObserver.onChanged()
        }
    }.apply {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    var emptyView: View? = null
        set(value) {
            mEmptyScrollView.removeAllViews()
            value?.let(mEmptyScrollView::addView)
            field = value
        }

    init {
        addView(recyclerView)
    }

    private inner class DataObserver(adapter: RecyclerView.Adapter<*>?) :
        RecyclerView.AdapterDataObserver() {
        private val mAdapter = adapter
        override fun onChanged() {
            if (emptyView == null) return
            if (mAdapter?.itemCount == 0) {
                removeView(recyclerView)
                addView(mEmptyScrollView)
            } else if (getChildAt(0) != recyclerView) {
                removeView(mEmptyScrollView)
                addView(recyclerView)
            }
        }
    }
}