package com.jcodecraeer.xrecyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-11-26 14:25
 * ### 描述：
 */
class JellyView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    View(context, attrs, defStyleAttr, defStyleRes), BaseRefreshHeader {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var path = Path()
    private var paint = Paint()
    var jellyHeight = 0f

    init {
        if (!isInEditMode) {
            paint.color = ContextCompat.getColor(context, android.R.color.holo_blue_bright)
            paint.isAntiAlias = true
        }
    }

    fun setJellyColor(jellyColor: Int) {
        paint.color = jellyColor
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()
        path.lineTo(0f, minimumHeight.toFloat())
        path.quadTo(
            measuredWidth / 2f,
            minimumHeight + jellyHeight,
            measuredWidth.toFloat(),
            minimumHeight.toFloat()
        )
        path.lineTo(measuredWidth.toFloat(), 0f)
        canvas?.drawPath(path, paint)
    }

    override fun refreshComplete() {}

    override fun onMove(delta: Float) {
        jellyHeight += delta
    }

    override fun releaseAction(): Boolean = false
}