package com.i56s.ktlib.orders

import android.view.View
import com.i56s.ktlib.I56sLib

/**设置单一点击事件*/
fun View.setOnSingleClickListener(
    intervalTime: Long = I56sLib.singleClickDelayMillis,
    l: View.OnClickListener
) {
    setOnClickListener(OnSingleClickListener(intervalTime, l))
}

/**设置单一点击事件*/
fun View.setOnSingleClickListener(
    intervalTime: Long = I56sLib.singleClickDelayMillis,
    l: (View) -> Unit
) {
    setOnClickListener(OnSingleClickListener(intervalTime, l))
}

/**控件显示*/
fun View.visible() {
    this.visibility = View.VISIBLE
}

/**控件消失*/
fun View.gone(isGone: Boolean = true) {
    this.visibility = if (isGone) View.GONE else View.VISIBLE
}

/**控件隐藏*/
fun View.invisible(isInvisible: Boolean = true) {
    this.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

class OnSingleClickListener : View.OnClickListener {
    private var previousClickTimeMillis = 0L
    private var intervalTime: Long = 0

    private val onClickListener: View.OnClickListener

    constructor(intervalTime: Long, listener: View.OnClickListener) {
        this.intervalTime = intervalTime
        onClickListener = listener
    }

    constructor(intervalTime: Long, listener: (View) -> Unit) {
        this.intervalTime = intervalTime
        onClickListener = View.OnClickListener {
            listener.invoke(it)
        }
    }

    override fun onClick(v: View) {
        val currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis >= previousClickTimeMillis + intervalTime) {
            previousClickTimeMillis = currentTimeMillis
            onClickListener.onClick(v)
        }
    }
}