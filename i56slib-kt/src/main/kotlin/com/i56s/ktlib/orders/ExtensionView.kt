package com.i56s.ktlib.orders

import android.view.View
import com.i56s.ktlib.I56sLib

/**设置单一点击事件*/
fun View.setOnSingleClickListener(l: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(l))
}

/**设置单一点击事件*/
fun View.setOnSingleClickListener(l: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener(l))
}

/**控件显示*/
fun View.visible() {
    this.visibility = View.VISIBLE
}

/**控件是否显示*/
fun View.isVisible(): Boolean = this.visibility == View.VISIBLE

/**控件消失*/
fun View.gone(isGone: Boolean = true) {
    this.visibility = if (isGone) View.GONE else View.VISIBLE
}

/**控件是否消失*/
fun View.isGone(): Boolean = this.visibility == View.GONE

/**控件隐藏*/
fun View.invisible(isInvisible: Boolean = true) {
    this.visibility = if (isInvisible) View.INVISIBLE else View.VISIBLE
}

/**控件是否隐藏*/
fun View.isInvisible(): Boolean = this.visibility == View.INVISIBLE

class OnSingleClickListener : View.OnClickListener {
    private var previousClickTimeMillis = 0L

    private val onClickListener: View.OnClickListener

    constructor(listener: View.OnClickListener) {
        onClickListener = listener
    }

    constructor(listener: (View) -> Unit) {
        onClickListener = View.OnClickListener {
            listener.invoke(it)
        }
    }

    override fun onClick(v: View) {
        val currentTimeMillis = System.currentTimeMillis()

        if (currentTimeMillis >= previousClickTimeMillis + I56sLib.singleClickDelayMillis) {
            previousClickTimeMillis = currentTimeMillis
            onClickListener.onClick(v)
        }
    }
}