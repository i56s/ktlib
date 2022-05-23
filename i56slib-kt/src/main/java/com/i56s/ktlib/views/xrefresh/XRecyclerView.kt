package com.i56s.ktlib.views.xrefresh

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
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

    val recyclerView = RecyclerView(context).apply {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    init {
        LogUtils.d("测试","嘿嘿嘿")
        addView(recyclerView)
    }
}