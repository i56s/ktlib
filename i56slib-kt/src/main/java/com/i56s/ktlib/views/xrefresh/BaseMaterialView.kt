package com.i56s.ktlib.views.xrefresh

import android.view.View

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-07 17:34
 * ### 描述：
 */
interface BaseMaterialView {

    fun getView(): View

    /** 开始触发 */
    fun onBegin()

    /** 滑动 */
    fun onSlide(fraction: Float)

    /** 刷新中 */
    fun onRefreshing()

    /** 完成 */
    fun onComlete()
}