package com.i56s.ktlib.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:43
 * ### 描述：列表适配器，封装添加头部/尾部控件
 * ### 更新：2023-09-01 ViewBinding增加LayoutParams初始化
 */
abstract class BaseRecyclerAdapter<T> constructor(context: Context, datas: MutableList<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cTypeNormal = 3000
    private val cBaseItemTypeHeader = 1000
    private val cBaseItemTypeFooter = 2000

    private val mHeaderViews: SparseArrayCompat<ViewBinding> = SparseArrayCompat()
    private val mFootViews: SparseArrayCompat<ViewBinding> = SparseArrayCompat()

    var datas: MutableList<T> = mutableListOf()

    protected var mContext: Context
        private set
    private var mListener: ((position: Int, data: T) -> Unit)? = null

    init {
        this.mContext = context
        this.datas = datas
    }

    /** 设置点击事件监听器 */
    open fun setOnItemClickListener(li: ((position: Int, data: T) -> Unit)?) {
        mListener = li
    }

    override fun getItemViewType(position: Int): Int =
        when {
            isHeaderViewPos(position) -> mHeaderViews.keyAt(position)
            isFooterViewPos(position) -> mFootViews.keyAt(
                position - getHeadersCount() - getRealItemCount()
            )

            else -> cTypeNormal
        }

    fun getRealItemCount(): Int = datas.size

    /** 判断是否是头部控件 */
    fun isHeaderViewPos(position: Int): Boolean = position < getHeadersCount()

    /** 判断是否是尾部控件 */
    fun isFooterViewPos(position: Int): Boolean = position >= getHeadersCount() + getRealItemCount()

    /** 获取头部所有控件 */
    fun getHeaderViews(): SparseArrayCompat<ViewBinding> = mHeaderViews

    /** 获取尾部所有控件 */
    fun getFootViews(): SparseArrayCompat<ViewBinding> = mFootViews

    /** 添加头部控件 */
    fun addHeaderView(view: ViewBinding) =
        mHeaderViews.put(mHeaderViews.size() + cBaseItemTypeHeader, view)

    /** 添加尾部控件 */
    fun addFootView(view: ViewBinding) =
        mFootViews.put(mFootViews.size() + cBaseItemTypeFooter, view)

    /** 删除底部控件 */
    fun removeFootView(view: ViewBinding) = mFootViews.remove(mFootViews.indexOfValue(view))

    /** 获取头部控件数量 */
    fun getHeadersCount(): Int = mHeaderViews.size()

    /** 获取底部控件数量 */
    fun getFootersCount(): Int = mFootViews.size()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (mHeaderViews.get(viewType) != null) {
            return Holder(mHeaderViews.get(viewType)!!)
        } else if (mFootViews.get(viewType) != null) {
            return Holder(mFootViews.get(viewType)!!)
        }
        return Holder(onCreate(LayoutInflater.from(parent.context), parent, viewType))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeaderViewPos(position)) return
        if (isFooterViewPos(position)) return

        val pos = position - getHeadersCount()
        val data = datas[pos]
        if (holder is Holder) onBind(holder, holder.binding, pos, data)
        holder.itemView.setOnClickListener { mListener?.invoke(position, data) }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val manager = recyclerView.layoutManager
        if (manager is GridLayoutManager) {
            manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int =
                    when {
                        mHeaderViews.get(getItemViewType(position)) != null -> manager.spanCount
                        mFootViews.get(getItemViewType(position)) != null -> manager.spanCount
                        else -> 1
                    }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.layoutParams?.let {
            if (it is StaggeredGridLayoutManager.LayoutParams && holder.layoutPosition == 0) {
                it.isFullSpan = true
            }
        }
    }

    override fun getItemCount(): Int = getHeadersCount() + getFootersCount() + getRealItemCount()

    fun getString(@StringRes resId: Int): String = mContext.resources.getString(resId)

    fun getString(@StringRes resId: Int, formatArgs: Any?): String =
        mContext.resources.getString(resId, formatArgs)

    /** 创建view holder */
    abstract fun onCreate(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
    ): ViewBinding

    /** 绑定数据 */
    abstract fun onBind(
            holder: RecyclerView.ViewHolder,
            binding: ViewBinding,
            position: Int,
            data: T
    )

    class Holder constructor(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            if (binding.root.layoutParams == null) {
                binding.root.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }
    }
}