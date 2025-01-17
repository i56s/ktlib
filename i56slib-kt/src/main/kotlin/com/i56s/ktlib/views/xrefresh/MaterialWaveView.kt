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
import com.i56s.ktlib.utils.LogUtils
import com.i56s.ktlib.utils.SizeUtils
import kotlin.math.max

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-09 11:45
 * ### 描述：下拉刷新滑动背景
 */
class MaterialWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defstyleAttr: Int = 0
) :
    View(context, attrs, defstyleAttr), BaseMaterialView {

    private var waveHeight = 0f
    private var headHeight = 0f

    private var path = Path()
    private var paint = Paint().apply {
        isAntiAlias = true
    }

    /**拖动的背景颜色*/
    var color = 0x90FFFFFF.toInt()

    /**是否是底部*/
    var isFooter = false

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.reset()
        paint.color = color

        if (isFooter) {
            path.moveTo(0f, measuredHeight.toFloat())
            path.lineTo(0f, measuredHeight.toFloat() - headHeight)
            //贝塞尔曲线
            path.quadTo(
                measuredWidth / 2f,  //起点x坐标
                measuredHeight.toFloat() - headHeight - waveHeight, //起点y坐标
                measuredWidth.toFloat(), //终点x坐标
                measuredHeight.toFloat() - headHeight //终点y坐标
            )
            path.lineTo(measuredWidth.toFloat(), measuredHeight.toFloat())
            canvas?.drawPath(path, paint)
        } else {
            path.lineTo(0f, headHeight)
            //贝塞尔曲线
            path.quadTo(
                measuredWidth / 2f,  //起点x坐标
                headHeight + waveHeight, //起点y坐标
                measuredWidth.toFloat(), //终点x坐标
                headHeight //终点y坐标
            )
            path.lineTo(measuredWidth.toFloat(), 0f)
            canvas?.drawPath(path, paint)
        }
    }

    override val view: View = this

    override fun onBegin() = Unit

    override fun onSlide(moveX: Float, fractionY: Float) {
        headHeight = SizeUtils.dp2px(triggerHeight()) * SizeUtils.limitValue(1f, fractionY)
        waveHeight = SizeUtils.dp2px(slideMaxHeight()) * max(0f, fractionY - 1)
        invalidate()
    }

    override fun onRefreshing() {
        headHeight = SizeUtils.dp2px(triggerHeight())
        val animator = ValueAnimator.ofFloat(waveHeight, 0f)
        animator.addUpdateListener {
            waveHeight = it.animatedValue as Float
            invalidate()
        }
        animator.interpolator = BounceInterpolator()
        animator.duration = 200
        animator.start()
    }

    override fun onComlete() {
        waveHeight = 0f
        val animator = ValueAnimator.ofFloat(headHeight, 0f)
        animator.duration = 200
        animator.interpolator = DecelerateInterpolator()
        animator.start()
        animator.addUpdateListener {
            headHeight = it.animatedValue as Float
            invalidate()
        }
    }
}