package com.i56s.ktlib.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.i56s.ktlib.R
import kotlin.math.min

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 17:00
 * ### 描述：加载中的圆圈控件
 */
class LoadingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    /**默认颜色*/
    private val COLORS =
        arrayOf(0xFFdddddd, 0xFFcccccc, 0xFFbbbbbb, 0xFFaaaaaa, 0xFF999999, 0xFF888888)

    /**view宽度*/
    private var vWidth = 0

    /**view高度*/
    private var vHeight = 0

    /**菊花矩形的宽*/
    private var widthRect = 0

    /**菊花矩形的高*/
    private var heightRect = 0

    /**菊花绘制画笔*/
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**循环绘制位置*/
    private var pos = 0

    /**菊花矩形*/
    private var rect: RectF? = null

    /**圆角*/
    private var round = 0f

    /**循环颜色*/
    private val color = mutableListOf<Int>()

    init {
        val colors = resources.obtainTypedArray(R.array.loading_view)
        repeat(colors.length()) {
            color.add(colors.getColor(it, COLORS[it % COLORS.size].toInt()))
        }
        colors.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            vWidth = 130
        } else {
            vWidth = MeasureSpec.getSize(widthMeasureSpec)
            vHeight = MeasureSpec.getSize(heightMeasureSpec)
            vWidth = min(vWidth, vHeight)
        }
        widthRect = vWidth / 12 //菊花矩形的宽
        heightRect = 4 * widthRect //菊花矩形的高
        setMeasuredDimension(vWidth, vWidth)
    }

    override fun onDraw(canvas: Canvas?) {
        if (rect == null) {
            rect = RectF((vWidth - widthRect) / 2f, 0f, (vWidth + widthRect) / 2f, heightRect - 10f)
            round = rect?.right!! - rect?.left!!
        }

        for (i in 0..11) {
            if (i - pos >= 5) {
                rectPaint.setColor(color.get(5))
            } else if (i - pos >= 0 && i - pos < 5) {
                rectPaint.setColor(color.get(i - pos))
            } else if (i - pos >= -7 && i - pos < 0) {
                rectPaint.setColor(color.get(5))
            } else if (i - pos >= -11 && i - pos < -7) {
                rectPaint.setColor(color.get(12 + i - pos))
            }
            canvas?.drawRoundRect(rect!!, round, round, rectPaint) //绘制
            canvas?.rotate(30f, vWidth / 2f, vWidth / 2f) //旋转
        }
        pos++
        if (pos > 11) pos = 0

        postInvalidateDelayed(100)//一个周期用时100毫秒
    }
}