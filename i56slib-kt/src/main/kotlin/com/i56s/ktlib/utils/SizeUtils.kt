package com.i56s.ktlib.utils

import android.content.Context
import com.i56s.ktlib.I56sLib
import kotlin.math.max
import kotlin.math.min

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
    fun px2dp(pxValue: Float): Float = px2dp(I56sLib.context, pxValue)

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * @param context 上下文
     * @param pxValue 需要转换的px值
     * @return 转换后的值
     */
    @JvmStatic
    fun px2dp(context: Context, pxValue: Float): Float = pxValue / density(context)

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * @param dipValue 需要转换的dip值
     * @return 转换后的值
     */
    @JvmStatic
    fun dp2px(dipValue: Float): Float = dp2px(I56sLib.context, dipValue)

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     * @param context 上下文
     * @param dipValue 需要转换的dip值
     * @return 转换后的值
     */
    @JvmStatic
    fun dp2px(context: Context, dipValue: Float): Float = dipValue * density(context)

    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param pxValue 需要转换的px值
     * @return 转换后的值
     */
    @JvmStatic
    fun px2sp(pxValue: Float): Float = px2sp(I56sLib.context, pxValue)

    /**
     * 将px值转换为sp值，保证文字大小不变
     * @param context 上下文
     * @param pxValue 需要转换的px值
     * @return 转换后的值
     */
    @JvmStatic
    fun px2sp(context: Context, pxValue: Float): Float = pxValue / scaledDensity(context)

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param spValue 需要转换的sp值
     * @return 转换后的值
     */
    @JvmStatic
    fun sp2px(spValue: Float): Float = sp2px(I56sLib.context, spValue)

    /**
     * 将sp值转换为px值，保证文字大小不变
     * @param context 上下文
     * @param spValue 需要转换的sp值
     * @return 转换后的值
     */
    @JvmStatic
    fun sp2px(context: Context, spValue: Float): Float = spValue * scaledDensity(context)

    /***/
    @JvmStatic
    fun limitValue(a: Float, b: Float): Float = when {
        b >= a -> a
        b <= 0f -> 0f
        else -> b
    }

    /**获取density*/
    private fun density(context: Context): Float = context.resources.displayMetrics.density

    /**获取scaledDensity*/
    private fun scaledDensity(context: Context): Float =
        context.resources.displayMetrics.scaledDensity
}