package com.i56s.ktlib.views.xrefresh

import android.view.View
import com.i56s.ktlib.utils.SizeUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-07 17:34
 * ### 描述：
 */
interface BaseMaterialView {

    val view: View

    /**加载中的高度(px)*/
    //fun loadingHeight():Float= SizeUtils.dp2px(70f)

    /** 开始触发 */
    fun onBegin()

    /** 滑动
     * @param moveX 滑动的x坐标
     * @param fractionY 滑动的y坐标比例*/
    fun onSlide(moveX: Float, fractionY: Float)

    /** 刷新中 */
    fun onRefreshing()

    /** 完成 */
    fun onComlete()
}