package com.i56s.ktlib.views.xrefresh

import android.view.View
import com.i56s.ktlib.utils.SizeUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-07 17:34
 * ### 描述：
 */
interface BaseMaterialView {

    /**当前控件*/
    val view: View

    /**滑动的最大高度(dp)
     * @return 必需>0*/
    fun slideMaxHeight(): Float = 140f

    /**触发事件的高度(dp)
     * @return 必需>0*/
    fun triggerHeight(): Float = 70f

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