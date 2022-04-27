package com.jcodecraeer.xrecyclerview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.AppBarLayout

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-11-19 10:19
 * ### 描述：下拉刷新，上拉加载
 */
class XRecyclerView3(context: Context, attrs: AttributeSet?, defStyle: Int) : RecyclerView(
    context, attrs, defStyle
) {
    private val TYPE_REFRESH_HEADER = 10000 //设置一个很大的数字,尽可能避免和用户的adapter冲突
    private val TYPE_FOOTER = 10001
    private val HEADER_INIT_INDEX = 10002

    var mRefreshHeader: ArrowRefreshHeader? = null
    var emptyView: View? = null //adapter没有数据的时候显示,类似于listView的emptyView
        set(value) {
            field = value
            mDataObserver.onChanged()
        }
    var mFootView: View
    var limitNumberToCallLoadMore = 1 //控制多出多少条的时候调用 onLoadMore
    var pullRefreshEnabled = true
    var loadingMoreEnabled = true
        set(value) {
            field = value
            if (!value && mFootView is LoadingMoreFooter) {
                (mFootView as LoadingMoreFooter).setState(LoadingMoreFooter.STATE_COMPLETE)
            }
        }
    var mRefreshProgressStyle = ProgressStyle.SysProgress
        set(value) {
            field = value
            mRefreshHeader?.setProgressStyle(value)
        }
    var mLoadingMoreProgressStyle = ProgressStyle.SysProgress
        set(value) {
            field = value
            if (mFootView is LoadingMoreFooter) {
                (mFootView as LoadingMoreFooter).setProgressStyle(value)
            }
        }

    /**
     * 下拉刷新的触发距离过大，导致用户下拉刷新操作经常达不到触发点，无法刷新，必须用力向下拉一段距离才可以。
     * 设置下拉时候的偏移计量因子。y = deltaY/dragRate
     * dragRate 越大，意味着，用户要下拉滑动更久来触发下拉刷新。相反越小，就越短距离
     */
    var dragRate = 3f
        set(value) {
            if (value <= 0.5) return
            field = value
        }

    private var isLoadingData = false
    private var isNoMore = false

    /** add by LinGuanHong below */
    private var scrollDyCounter = 0

    private var mLastY = -1f

    private var appbarState: AppBarStateChangeListener.State =
        AppBarStateChangeListener.State.EXPANDED

    private val mHeaderViews = mutableListOf<View>()
    private val sHeaderTypes = mutableListOf<Int>() //每个header必须有不同的type,不然滚动的时候顺序会变化
    private val mDataObserver: AdapterDataObserver = DataObserver()

    private var mWrapAdapter: WrapAdapter? = null
    private var mLoadingListener: LoadingListener? = null
    private var footerViewCallBack: CustomFooterViewCallBack? = null
    private var scrollAlphaChangeListener: ScrollAlphaChangeListener? = null

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    init {
        if (pullRefreshEnabled) { //若支持下拉刷新则加入Headerview列表，设置加载图标
            mRefreshHeader = ArrowRefreshHeader(context)
            mRefreshHeader?.setProgressStyle(mRefreshProgressStyle)
        }
        val footerView = LoadingMoreFooter(context)
        footerView.setProgressStyle(mLoadingMoreProgressStyle)
        footerView.visibility = GONE
        mFootView = footerView
    }

    /**
     * call it when you finish the activity,
     * when you call this,better don't call some kind of functions like
     * RefreshHeader,because the reference of mHeaderViews is NULL.
     */
    fun destroy() {
        mHeaderViews.clear()
        if (mFootView is LoadingMoreFooter) (mFootView as LoadingMoreFooter).destroy()
        mRefreshHeader?.destroy()
    }

    /**
     * 设置底部加载更多文本
     * @param loading 加载中的文本
     * @param noMore 没有更多数据的文本
     */
    fun setFootViewText(loading: String, noMore: String) {
        if (mFootView is LoadingMoreFooter) {
            val view = mFootView as LoadingMoreFooter
            view.setLoadingHint(loading)
            view.setNoMoreHint(noMore)
        }
    }

    /**添加头部控件*/
    @SuppressLint("NotifyDataSetChanged")
    fun addHeaderView(view: View) {
        sHeaderTypes.add(HEADER_INIT_INDEX + mHeaderViews.size)
        mHeaderViews.add(view)
        mWrapAdapter?.notifyDataSetChanged()
    }

    /**移除头部控件*/
    fun removeHeaderView(view: View) {
        mHeaderViews.forEachIndexed { index, v ->
            if (v == view) {
                mHeaderViews.remove(view)
                mWrapAdapter?.notifyItemRemoved(index)
                return@forEachIndexed
            }
        }
    }

    /**移除所有头部控件*/
    @SuppressLint("NotifyDataSetChanged")
    fun removeAllHeaderView() {
        mHeaderViews.clear()
        mWrapAdapter?.notifyDataSetChanged()
    }

    /**设置刷新或加载更多监听*/
    fun setLoadingListener(listener: LoadingListener?) {
        mLoadingListener = listener
    }

    /**设置底部加载布局*/
    fun setFootView(view: View, callBack: CustomFooterViewCallBack) {
        mFootView = view
        footerViewCallBack = callBack
    }

    /**多个地方使用该控件的时候，所有刷新时间都相同*/
    fun setRefreshTimeSpKeyName(keyName: String) = mRefreshHeader?.setXrRefreshTimeKey(keyName)

    /**完成加载更多*/
    fun loadMoreComplete() {
        isLoadingData = false
        if (mFootView is LoadingMoreFooter) {
            (mFootView as LoadingMoreFooter).setState(LoadingMoreFooter.STATE_COMPLETE)
        } else {
            footerViewCallBack?.onLoadMoreComplete(mFootView)
        }
    }

    /**完成刷新*/
    fun refreshComplete() {
        mRefreshHeader?.refreshComplete()
        setNoMore(false)
    }

    /**设置没更多数据*/
    fun setNoMore(isNoMore: Boolean) {
        isLoadingData = false
        this.isNoMore = isNoMore
        if (mFootView is LoadingMoreFooter) {
            (mFootView as LoadingMoreFooter).setState(
                if (isNoMore) LoadingMoreFooter.STATE_NOMORE else LoadingMoreFooter.STATE_COMPLETE
            )
        } else {
            footerViewCallBack?.onSetNoMore(mFootView, isNoMore)
        }
    }

    /**刷新*/
    fun refresh() {
        if (pullRefreshEnabled) {
            mRefreshHeader?.state = BaseRefreshHeader.STATE_REFRESHING
            mLoadingListener?.onRefresh()
        }
    }

    /**重置*/
    fun reset() {
        setNoMore(false)
        loadMoreComplete()
        refreshComplete()
    }

    /**设置箭头图片*/
    fun setArrowImageView(resId: Int) = mRefreshHeader?.setArrowImageView(resId)

    /**设置适配器*/
    override fun setAdapter(adapter: Adapter<ViewHolder>?) { //对传入的adapter做封装
        mWrapAdapter = WrapAdapter(adapter = adapter!!)
        super.setAdapter(adapter)
        adapter.registerAdapterDataObserver(mDataObserver)
        mDataObserver.onChanged()
    }

    /**避免用户自己调用getAdapter() 引起的ClassCastException*/
    override fun getAdapter(): Adapter<*>? = mWrapAdapter?.getOriginalAdapter()

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        if (mWrapAdapter != null && layout is GridLayoutManager) {
            layout.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    if ((mWrapAdapter!!.isHeader(position) || mWrapAdapter!!.isFooter(
                            position
                        )) || mWrapAdapter!!.isRefreshHeader(position)
                    ) layout.spanCount else 1
            }
        }
    }

    /** ===================== try to adjust the position for XR when you call those functions below ====================== */

    // which cause "Called attach on a child which is not detached" exception info.
    // {reason analyze @link:http://www.cnblogs.com/linguanh/p/5348510.html}
    // by lgh on 2017-11-13 23:55

    // example: listData.remove(position); You can also see a demo on LinearActivity
    fun <T> notifyItemRemoved(listData: List<T>, position: Int) {
        val headerSize = getHeaders_includingRefreshCount()
        val adjPos = position + headerSize
        mWrapAdapter?.adapter?.notifyItemRemoved(adjPos)
        mWrapAdapter?.adapter?.notifyItemRangeChanged(headerSize, listData.size, Any())
    }

    fun <T> notifyItemInserted(listData: List<T>, position: Int) {
        val headerSize = getHeaders_includingRefreshCount()
        val adjPos = position + headerSize
        mWrapAdapter?.adapter?.notifyItemInserted(adjPos)
        mWrapAdapter?.adapter?.notifyItemRangeChanged(headerSize, listData.size, Any())
    }

    fun notifyItemChanged(position: Int) = mWrapAdapter?.adapter?.notifyItemChanged(
        position + getHeaders_includingRefreshCount()
    )

    fun notifyItemChanged(position: Int, any: Any) = mWrapAdapter?.adapter?.notifyItemChanged(
        position + getHeaders_includingRefreshCount(), any
    )

    private fun getHeaders_includingRefreshCount(): Int =
        if (mWrapAdapter == null) 0 else mWrapAdapter!!.getHeadersCount() + 1

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (layoutManager == null) return
        if (state == SCROLL_STATE_IDLE && !isLoadingData && loadingMoreEnabled) {
            var lastVisibleItemPosition: Int
            if (layoutManager is GridLayoutManager) {
                lastVisibleItemPosition =
                    (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
            } else if (layoutManager is StaggeredGridLayoutManager) {
                val into = IntArray((layoutManager as StaggeredGridLayoutManager).spanCount)
                (layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(into)
                lastVisibleItemPosition = findMax(into)
            } else {
                lastVisibleItemPosition =
                    (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
            val adjAdapterItemCount = layoutManager!!.itemCount + getHeaders_includingRefreshCount()
            var status = BaseRefreshHeader.STATE_DONE
            if (mRefreshHeader != null) status = mRefreshHeader!!.state
            if (layoutManager!!.childCount > 0 && lastVisibleItemPosition >= adjAdapterItemCount - limitNumberToCallLoadMore && adjAdapterItemCount >= layoutManager!!.childCount && !isNoMore && status < BaseRefreshHeader.STATE_REFRESHING) {
                isLoadingData = true
                if (mFootView is LoadingMoreFooter) {
                    (mFootView as LoadingMoreFooter).setState(LoadingMoreFooter.STATE_LOADING)
                } else {
                    footerViewCallBack?.onLoadingMore(mFootView)
                }
                mLoadingListener?.onLoadMore()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if (mLastY == -1f) mLastY = e.rawY
        when (e.action) {
            MotionEvent.ACTION_DOWN -> mLastY = e.rawY
            MotionEvent.ACTION_MOVE -> {
                val deltaY = e.rawY - mLastY
                mLastY = e.rawY
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED && mRefreshHeader != null) {
                    mRefreshHeader!!.onMove(deltaY / dragRate)
                    if (mRefreshHeader!!.getVisibleHeight() > 0 && mRefreshHeader!!.state < BaseRefreshHeader.STATE_REFRESHING) return false
                }
            }
            else -> {
                mLastY = -1f
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (mRefreshHeader != null && mRefreshHeader!!.releaseAction()) mLoadingListener?.onRefresh()
                }
            }
        }
        return super.onTouchEvent(e)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        var appBarLayout: AppBarLayout? = null
        var p = parent
        while (p != null) {
            if (p is CoordinatorLayout) break
            p = p.parent
        }
        if (p is CoordinatorLayout) {
            val childCount = p.childCount
            for (index in (0 until childCount).reversed()) {
                val child = p.getChildAt(index)
                if (child is AppBarLayout) {
                    appBarLayout = child
                    break
                }
            }
            appBarLayout?.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
                override fun onStateChanged(appBarLayout: AppBarLayout?, state: State?) {
                    appbarState = state!!
                }
            })
        }
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)

        //if we scroll to position 0, the scrollDyCounter should be reset
        if (position == 0) scrollDyCounter = 0
    }

    fun setScrollAlphaChangeListener(listener: ScrollAlphaChangeListener) {
        scrollAlphaChangeListener = listener
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        if (scrollAlphaChangeListener == null) return
        val height = scrollAlphaChangeListener!!.setLimitHeight()
        scrollDyCounter += dy
        if (scrollDyCounter <= 0) {
            scrollAlphaChangeListener!!.onAlphaChange(0)
        } else if (scrollDyCounter <= height && scrollDyCounter > 0) {
            val scale = scrollDyCounter * 1f / height //255/height = x/255
            val alpha = 255 * scale
            scrollAlphaChangeListener!!.onAlphaChange(alpha.toInt())
        } else {
            scrollAlphaChangeListener!!.onAlphaChange(255)
        }
    }

    private fun findMax(lastPositions: IntArray): Int {
        var max = lastPositions[0]
        for (value in lastPositions) {
            if (value > max) max = value
        }
        return max
    }

    private fun isOnTop(): Boolean = mRefreshHeader != null && mRefreshHeader?.parent != null

    /**判断一个type是否为HeaderType*/
    private fun isHeaderType(itemViewType: Int): Boolean =
        mHeaderViews.size > 0 && sHeaderTypes.contains(
            itemViewType
        )

    /**根据header的ViewType判断是哪个header*/
    private fun getHeaderViewByType(itemType: Int): View? =
        if (!isHeaderType(itemType)) null else mHeaderViews.get(itemType - HEADER_INIT_INDEX)

    /**判断是否是XRecyclerView保留的itemViewType*/
    private fun isReservedItemViewType(itemViewType: Int) =
        itemViewType == TYPE_REFRESH_HEADER || itemViewType == TYPE_FOOTER || sHeaderTypes.contains(
            itemViewType
        )

    private inner class WrapAdapter(var adapter: Adapter<ViewHolder>) : Adapter<ViewHolder>() {

        fun getOriginalAdapter(): Adapter<ViewHolder> = adapter

        fun isHeader(position: Int): Boolean =
            if (mHeaderViews.size == 0) false else position >= 1 && position < mHeaderViews.size + 1

        fun isFooter(position: Int): Boolean =
            if (loadingMoreEnabled) position == getItemCount() - 1 else false

        fun isRefreshHeader(position: Int): Boolean = position == 0

        fun getHeadersCount(): Int = mHeaderViews.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == TYPE_REFRESH_HEADER) {
                return SimpleViewHolder(mRefreshHeader!!)
            } else if (isHeaderType(viewType)) {
                return SimpleViewHolder(mHeaderViews.get(viewType - HEADER_INIT_INDEX))
            } else if (viewType == TYPE_FOOTER) {
                return SimpleViewHolder(mFootView)
            }
            return adapter.onCreateViewHolder(parent, viewType)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (isHeader(position) || isRefreshHeader(position)) return
            val adjPosition = position - (getHeadersCount() + 1)
            if (adjPosition < adapter.itemCount) {
                adapter.onBindViewHolder(holder, adjPosition)
            }
        }

        override fun onBindViewHolder(
            holder: ViewHolder, position: Int, payloads: MutableList<Any>
        ) {
            if (isHeader(position) || isRefreshHeader(position)) return
            val adjPosition = position - (getHeadersCount() + 1)
            if (adjPosition < adapter.itemCount) {
                if (payloads.isEmpty()) {
                    adapter.onBindViewHolder(holder, adjPosition)
                } else {
                    adapter.onBindViewHolder(holder, adjPosition, payloads)
                }
            }
        }

        override fun getItemCount(): Int {
            val adjLen = if (loadingMoreEnabled) 2 else 1
            return getHeadersCount() + adapter.itemCount + adjLen
        }

        override fun getItemViewType(position: Int): Int {
            val adjPosition = position - (getHeadersCount() + 1)
            if (isRefreshHeader(position)) return TYPE_REFRESH_HEADER
            if (isHeader(position)) return sHeaderTypes.get(position - 1)
            if (isFooter(position)) return TYPE_FOOTER
            if (adjPosition < adapter.itemCount) {
                val type = adapter.getItemViewType(adjPosition)
                if (isReservedItemViewType(type)) {
                    throw IllegalStateException(
                        "XRecyclerView require itemViewType in adapter should be less than 10000 "
                    )
                }
                return type
            }
            return super.getItemViewType(position)
        }

        override fun getItemId(position: Int): Long {
            if (position >= getHeadersCount() + 1) {
                val adjPosition = position - getHeadersCount() - 1
                if (adjPosition < adapter.itemCount) return adapter.getItemId(adjPosition)
            }
            return -1
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            val manager = recyclerView.layoutManager
            if (manager is GridLayoutManager) {
                manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                        if ((isHeader(position) || isFooter(position) || isRefreshHeader(
                                position
                            ))
                        ) manager.spanCount else 1
                }
            }
        }

        override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) =
            adapter.onDetachedFromRecyclerView(recyclerView)

        override fun onViewAttachedToWindow(holder: ViewHolder) {
            super.onViewAttachedToWindow(holder)
            val lp = holder.itemView.layoutParams
            if (lp is StaggeredGridLayoutManager.LayoutParams && (isHeader(
                    holder.layoutPosition
                ) || isRefreshHeader(holder.layoutPosition) || isFooter(holder.layoutPosition))
            ) {
                lp.isFullSpan = true
            }
            adapter.onViewAttachedToWindow(holder)
        }

        override fun onViewDetachedFromWindow(holder: ViewHolder) =
            adapter.onViewDetachedFromWindow(holder)

        override fun onViewRecycled(holder: ViewHolder) = adapter.onViewRecycled(holder)

        override fun onFailedToRecycleView(holder: ViewHolder): Boolean =
            adapter.onFailedToRecycleView(
                holder
            )

        override fun unregisterAdapterDataObserver(observer: AdapterDataObserver) =
            adapter.unregisterAdapterDataObserver(
                observer
            )

        override fun registerAdapterDataObserver(observer: AdapterDataObserver) =
            adapter.registerAdapterDataObserver(
                observer
            )
    }

    private inner class SimpleViewHolder(itemView: View) : ViewHolder(itemView)

    private inner class DataObserver : AdapterDataObserver() {
        override fun onChanged() {
            mWrapAdapter?.notifyDataSetChanged()
            if (mWrapAdapter != null && emptyView != null) {
                var emptyCount = 1 + mWrapAdapter!!.getHeadersCount()
                if (loadingMoreEnabled) emptyCount++
                if (mWrapAdapter!!.itemCount == emptyCount) {
                    emptyView!!.visibility = VISIBLE
                    visibility = GONE
                } else {
                    emptyView!!.visibility = GONE
                    visibility = VISIBLE
                }
            }
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            mWrapAdapter?.notifyItemRangeChanged(positionStart, itemCount, payload)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            mWrapAdapter?.notifyItemMoved(fromPosition, toPosition)
        }
    }

    inner class DividerItemDecoration(val mDivider: Drawable) : ItemDecoration() {
        private var mOrientation = 0

        /**
         * Draws horizontal or vertical dividers onto the parent RecyclerView.
         *
         * @param canvas The {@link Canvas} onto which dividers will be drawn
         * @param parent The RecyclerView onto which dividers are being added
         * @param state The current RecyclerView.State of the RecyclerView
         */
        override fun onDraw(c: Canvas, parent: RecyclerView, state: State) {
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                drawHorizontalDividers(c, parent)
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                drawVerticalDividers(c, parent)
            }
        }

        /**
         * Determines the size and location of offsets between items in the parent
         * RecyclerView.
         *
         * @param outRect The {@link Rect} of offsets to be added around the child
         *                view
         * @param view The child view to be decorated with an offset
         * @param parent The RecyclerView onto which dividers are being added
         * @param state The current RecyclerView.State of the RecyclerView
         */
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
            super.getItemOffsets(outRect, view, parent, state)
            if (parent.getChildAdapterPosition(view) <= mWrapAdapter!!.getHeadersCount() + 1) return
            mOrientation = (parent.layoutManager as LinearLayoutManager).orientation
            if (mOrientation == LinearLayoutManager.HORIZONTAL) {
                outRect.left = mDivider.intrinsicWidth
            } else if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.top = mDivider.intrinsicHeight
            }
        }

        /**
         * Adds dividers to a RecyclerView with a LinearLayoutManager or its
         * subclass oriented horizontally.
         *
         * @param canvas The {@link Canvas} onto which horizontal dividers will be
         *               drawn
         * @param parent The RecyclerView onto which horizontal dividers are being
         *               added
         */
        private fun drawHorizontalDividers(canvas: Canvas, parent: RecyclerView) {
            val parentTop = parent.paddingTop
            val parentBottom = parent.height - parent.paddingBottom
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as LayoutParams
                val parentLeft = child.right + params.rightMargin
                val parentRight = parentLeft + mDivider.intrinsicWidth
                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom)
                mDivider.draw(canvas)
            }
        }

        /**
         * Adds dividers to a RecyclerView with a LinearLayoutManager or its
         * subclass oriented vertically.
         *
         * @param canvas The {@link Canvas} onto which vertical dividers will be
         *               drawn
         * @param parent The RecyclerView onto which vertical dividers are being
         *               added
         */
        private fun drawVerticalDividers(canvas: Canvas, parent: RecyclerView) {
            val parentLeft = parent.paddingLeft
            val parentRight = parent.width - parent.paddingRight
            val childCount = parent.childCount
            for (i in 0 until childCount) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as LayoutParams
                val parentTop = child.bottom + params.bottomMargin
                val parentBottom = parentTop + mDivider.intrinsicHeight
                mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom)
                mDivider.draw(canvas)
            }
        }
    }

    interface LoadingListener {
        fun onRefresh()
        fun onLoadMore()
    }

    interface ScrollAlphaChangeListener {
        fun onAlphaChange(alpha: Int) //you can handle the alpha insert it
        fun setLimitHeight(): Int //set a height for the begging of the alpha start to change
    }
}