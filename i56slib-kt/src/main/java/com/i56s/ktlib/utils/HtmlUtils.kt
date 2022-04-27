package com.i56s.ktlib.utils

import android.text.Html
import android.text.Spanned

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-03-24 16:50
 * ### 描述：html转换工具类
 */
object HtmlUtils {

    /** Html转换 */
    @JvmStatic
    fun fromHtml(source: String?): Spanned =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_COMPACT);
        } else {
            Html.fromHtml(source);
        }
}