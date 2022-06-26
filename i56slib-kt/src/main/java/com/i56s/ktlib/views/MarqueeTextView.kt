package com.i56s.ktlib.views

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-26 14:19
 * ### 描述：文本跑马灯
 */
class MarqueeTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    init { //设置单行
        setSingleLine() //设置Ellipsize
        ellipsize = TextUtils.TruncateAt.MARQUEE //获取焦点
        isFocusable = true //走马灯的重复次数，-1表示无限重复
        marqueeRepeatLimit = -1 //强制获得焦点
        isFocusableInTouchMode = true
    }

    override fun isFocused(): Boolean = true
}