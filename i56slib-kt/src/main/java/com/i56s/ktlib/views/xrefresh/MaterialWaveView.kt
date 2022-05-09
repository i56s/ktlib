package com.i56s.ktlib.views.xrefresh

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.DecelerateInterpolator
import com.i56s.ktlib.utils.SizeUtils
import kotlin.math.max

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-09 11:45
 * ### 描述：
 */
class MaterialWaveView constructor(context: Context, attrs: AttributeSet?, defstyleAttr: Int) :
    View(context, attrs, defstyleAttr), BaseMaterialView {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    /**滑动的最大高度(dp)*/
    var defaulWaveHeight = SizeUtils.dp2px(140f)
        set(value) {
            field = SizeUtils.dp2px(value)
        }

    /**刷新中的高度(dp)*/
    var defaulHeadHeight = SizeUtils.dp2px(70f)
        set(value) {
            field = SizeUtils.dp2px(value)
        }

    var waveHeight = 0
    var headHeight = 0
    var color = 0
    private var path = Path()
    private var paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()
        paint.color = color
        path.lineTo(0f, headHeight.toFloat())
        path.quadTo(
            measuredWidth / 2f,
            (headHeight + waveHeight).toFloat(),
            measuredWidth.toFloat(),
            headHeight.toFloat()
        )
        path.lineTo(measuredWidth.toFloat(), 0f)
        canvas?.drawPath(path, paint)
    }

    override val view: View = this

    override fun onBegin() = Unit

    override fun onSlide(fraction: Float) {
        headHeight = (defaulHeadHeight * fraction).toInt()
        waveHeight = (defaulWaveHeight * max(0f, fraction - 1f)).toInt()
        invalidate()
    }

    override fun onRefreshing() {
        headHeight = defaulHeadHeight.toInt()
        val animator = ValueAnimator.ofInt(waveHeight, 0)
        animator.addUpdateListener {
            waveHeight = it.animatedValue as Int
            invalidate()
        }
        animator.interpolator = BounceInterpolator()
        animator.duration = 200
        animator.start()
    }

    override fun onComlete() {
        waveHeight = 0
        val animator = ValueAnimator.ofInt(headHeight, 0)
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.start()
        animator.addUpdateListener {
            headHeight = it.animatedValue as Int
            invalidate()
        }
    }
}