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

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

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