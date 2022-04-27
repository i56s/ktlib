package com.jcodecraeer.xrecyclerview

import android.view.View

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-11-26 14:58
 * ### 描述：
 */
interface CustomFooterViewCallBack {

    fun onLoadingMore(yourFooterView: View)
    fun onLoadMoreComplete(yourFooterView: View)
    fun onSetNoMore(yourFooterView: View, noMore: Boolean)
}