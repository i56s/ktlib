package com.i56s.ktlib.views.xrefresh

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-11 15:02
 * ### 描述：刷新回调接口
 */
interface MaterialRefreshListener {

    /**触发刷新回调*/
    fun onRefresh(layout: MaterialRefreshLayout)

    /**触发加载更多*/
    fun onLoadMore(layout: MaterialRefreshLayout)
}