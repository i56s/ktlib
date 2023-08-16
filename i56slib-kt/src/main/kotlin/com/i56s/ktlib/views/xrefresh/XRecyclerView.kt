package com.i56s.ktlib.views.xrefresh

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i56s.ktlib.R
import com.i56s.ktlib.utils.LogUtils
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-13 15:01
 * ### 描述：带RecyclerView的刷新控件
 */
class XRecyclerView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defstyleAttr: Int = 0) :
    MaterialRefreshLayout(context, attrs, defstyleAttr) {

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

    var layoutManager: RecyclerView.LayoutManager? = null
        set(value) {
            recyclerView.layoutManager = value
            field = value
        }

    var adapter: RecyclerView.Adapter<*>? = null
        set(value) {
            recyclerView.adapter = value
            field = value
        }

    init {

        addView(recyclerView)
        val t = context.obtainStyledAttributes(
            attrs, R.styleable.XRecyclerView, defstyleAttr, 0
        )
        isOverlay = t.getBoolean(R.styleable.XRecyclerView_isOverlay, true)
        isRefreshEnable = t.getBoolean(R.styleable.XRecyclerView_refreshEnable, true)
        isLoadMoreEnable = t.getBoolean(R.styleable.XRecyclerView_loadMoreEnable, false)
        val layoutManagerName = t.getString(R.styleable.XRecyclerView_layoutManager)
        t.recycle()
        layoutManagerName?.let {
            var className = it.trim()
            if (className.isNotEmpty()) {
                className = if (className.toCharArray()[0] == '.') context.packageName + className
                else if (className.contains(".")) className
                else RecyclerView::class.java.`package`?.name + '.' + className

                try {
                    val classLoader: ClassLoader =
                        if (isInEditMode) this.javaClass.classLoader!! else context.classLoader
                    val layoutManagerClass = Class.forName(className, false, classLoader)
                        .asSubclass(RecyclerView.LayoutManager::class.java)
                    var constructor: Constructor<*>
                    val constructorArgs = arrayOf(context, attrs, defstyleAttr, 0)
                    try {
                        constructor = layoutManagerClass.getConstructor(
                            Context::class.java, AttributeSet::class.java, Int::class.java,
                            Int::class.java
                        )
                    } catch (e: NoSuchMethodException) {
                        try {
                            constructor = layoutManagerClass.getConstructor()
                        } catch (e1: NoSuchMethodException) {
                            e1.initCause(e)
                            throw IllegalStateException(
                                "${attrs?.positionDescription}: Error creating LayoutManager $className",
                                e1
                            )
                        }
                    }
                    constructor.isAccessible = true
                    layoutManager =
                        constructor.newInstance(constructorArgs) as RecyclerView.LayoutManager
                } catch (e: ClassNotFoundException) {
                    throw IllegalStateException(
                        "${attrs?.positionDescription}: Unable to find LayoutManager $className", e
                    )
                } catch (e: InvocationTargetException) {
                    throw IllegalStateException(
                        "${attrs?.positionDescription}: Could not instantiate the LayoutManager: $className",
                        e
                    )
                } catch (e: InstantiationException) {
                    throw IllegalStateException(
                        "${attrs?.positionDescription}: Could not instantiate the LayoutManager: $className",
                        e
                    )
                } catch (e: IllegalAccessException) {
                    throw IllegalStateException(
                        "${attrs?.positionDescription}: Cannot access non-public constructor $className",
                        e
                    )
                } catch (e: ClassCastException) {
                    throw IllegalStateException(
                        "${attrs?.positionDescription}: Class is not a LayoutManager $className", e
                    )
                }
            }
        }
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