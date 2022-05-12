package com.i56s.ktlib.views.xrefresh

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-11 15:02
 * ### 描述：刷新回调接口
 */
class MaterialRefreshListener {

    /**触发刷新回调*/
    var onRefresh: ((MaterialRefreshLayout) -> Unit)? = null

    /**触发加载更多*/
    var onLoadMore: ((MaterialRefreshLayout) -> Unit)? = null
}