package com.i56s.ktlib.utils

import com.i56s.ktlib.I56sLib

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:54
 * ### 描述：尺寸转换px dp sp互转
 */
object SizeUtils {

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * @param pxValue 需要转换的px值
     * @return 转换后的值
     */
    @JvmStatic
    fun px2dp(pxValue: Float): Float = pxValue / density()

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * @param dipValue 需要转换的dip值
     * @return 转换后的值
     */
    @JvmStatic
    fun dp2px(dipValue: Float): Float = dipValue * density()

    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param pxValue 需要转换的px值
     * @return 转换后的值
     */
    @JvmStatic
    fun px2sp(pxValue: Float): Float = pxValue / scaledDensity()

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue 需要转换的sp值
     * @return 转换后的值
     */
    @JvmStatic
    fun sp2px(spValue: Float): Float = spValue * scaledDensity()

    /**获取density*/
    private fun density(): Float = I56sLib.context.resources.displayMetrics.density

    /**获取scaledDensity*/
    private fun scaledDensity(): Float = I56sLib.context.resources.displayMetrics.scaledDensity
}