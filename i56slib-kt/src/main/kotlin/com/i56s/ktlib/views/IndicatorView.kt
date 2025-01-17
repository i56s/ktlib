package com.i56s.ktlib.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.i56s.ktlib.R

/**
 * ### 创建者： wxr
 * ### 创建时间： 2023-08-27 15:40
 * ### 描述：指示器控件
 */
class IndicatorView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr), ViewPager.OnPageChangeListener {

    private val LETTER = arrayOf(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "G", "K", "L", "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X", "Y", "Z"
    )

    private val mCirclePaint: Paint = Paint().also {
        it.isDither = true
        it.isAntiAlias = true
        it.style = Paint.Style.FILL_AND_STROKE
    }
    private val mTextPaint: Paint = Paint().also {
        it.isDither = true
        it.isAntiAlias = true
    }
    private val textBound = Rect()

    /**指示器选中的颜色*/
    var selectColor = 0

    /**指示器数量*/
    var count = 0
        set(value) {
            field = value
            invalidate()
        }

    /**指示器圆半径*/
    var radius = 0f
        set(value) {
            mTextPaint.textSize = value
            field = value
        }

    /**画笔宽度*/
    var strokeWidth = 0f
        set(value) {
            mCirclePaint.strokeWidth = value
            field = value
        }

    /**小圆点中文字的颜色*/
    var textColor = 0
        set(value) {
            mTextPaint.color = value
            field = value
        }

    /**指示器默认颜色*/
    var normalColor = 0
        set(value) {
            mCirclePaint.color = value
            field = value
        }

    /**指示器选中的位置*/
    var selectPosition = 0

    /**圆点之间的间距*/
    var space = 0f
    private val mIndicators = mutableListOf<Point>()

    /**指示器样式*/
    var fillMode = FillMode.NONE

    /**关联的ViewPager*/
    var viewPager: ViewPager? = null
        set(value) {
            field?.removeOnPageChangeListener(this)
            value?.addOnPageChangeListener(this)
            count = value?.adapter?.count ?: count
            field = value
        }
    private var mOnIndicatorClickListener: OnIndicatorClickListener? = null

    /**是否允许点击指示器切换ViewPager*/
    var isEnableClickSwitch = false

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.IndicatorView, 0, R.style.IndicatorView
        )
        count =
            typedArray.getInt(R.styleable.IndicatorView_indicatorCount, if (isInEditMode) 5 else 0)
        selectPosition = typedArray.getInt(R.styleable.IndicatorView_indicatorSelectPosition, 0)

        radius = typedArray.getDimension(R.styleable.IndicatorView_indicatorRadius, 0f)
        strokeWidth = typedArray.getDimension(R.styleable.IndicatorView_indicatorBorderWidth, 0f)
        space = typedArray.getDimension(R.styleable.IndicatorView_indicatorSpace, 0f)

        textColor = typedArray.getColor(R.styleable.IndicatorView_android_textColor, 0)
        selectColor = typedArray.getColor(R.styleable.IndicatorView_indicatorSelectColor, 0)
        normalColor = typedArray.getColor(R.styleable.IndicatorView_indicatorColor, 0)

        isEnableClickSwitch =
            typedArray.getBoolean(R.styleable.IndicatorView_indicatorEnableSwitch, false)

        fillMode = when (typedArray.getInt(R.styleable.IndicatorView_indicatorFillMode, 0)) {
            FillMode.LETTER.type -> FillMode.LETTER
            FillMode.NUMBER.type -> FillMode.NUMBER
            else -> FillMode.NONE
        }
        typedArray.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (count > 0) {
            val width = (radius + strokeWidth) * 2 * count + space * (count - 1)
            val height = radius * 2 + space * 2
            setMeasuredDimension(width.toInt(), height.toInt())

            mIndicators.clear()
            var cx = 0f
            for (i in 0 until count) {
                if (i == 0) {
                    cx = radius + strokeWidth
                } else {
                    cx += (radius + strokeWidth) * 2 + space
                }
                mIndicators.add(Point((cx + 0.5f).toInt(), measuredHeight / 2))
            }
        } else setMeasuredDimension(0, 0)
    }

    override fun onDraw(canvas: Canvas) {
        mIndicators.forEachIndexed { index, point ->
            if (selectPosition == index) {
                mCirclePaint.style = Paint.Style.FILL
                mCirclePaint.color = selectColor
            } else {
                mCirclePaint.color = normalColor
                mCirclePaint.style =
                    if (fillMode != FillMode.NONE) Paint.Style.STROKE else Paint.Style.FILL
            }
            canvas?.drawCircle(point.x.toFloat(), point.y.toFloat(), radius, mCirclePaint)

            // 绘制小圆点中的内容
            if (fillMode != FillMode.NONE) {
                val text = if (fillMode == FillMode.LETTER && index >= 0 && index < LETTER.size) {
                    LETTER[index]
                } else (index + 1).toString()
                mTextPaint.getTextBounds(text, 0, text.length, textBound)
                val textWidth = textBound.width()
                val textHeight = textBound.height()

                canvas?.drawText(text, x - textWidth / 2, y + textHeight / 2, mTextPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        var xPoint = 0f
        var yPoint = 0f
        if (event?.action == MotionEvent.ACTION_DOWN) {
            xPoint = event.x
            yPoint = event.y
            mIndicators.forEachIndexed { index, point ->
                if (xPoint < (point.x + radius + strokeWidth) && xPoint >= (point.x - (radius + strokeWidth)) && yPoint >= (yPoint - (point.y + strokeWidth)) && yPoint < (point.y + radius + strokeWidth)) {
                    // 找到了点击的point
                    // 是否允许切换ViewPager
                    if (isEnableClickSwitch) {
                        viewPager?.setCurrentItem(index, false)
                    }

                    //回调
                    mOnIndicatorClickListener?.onSelected(index)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    /**设置指示器点击监听器*/
    fun setOnIndicatorClickListener(listener: OnIndicatorClickListener) {
        mOnIndicatorClickListener = listener
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) =
        Unit

    override fun onPageSelected(position: Int) {
        selectPosition = position
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) = Unit

    enum class FillMode(val type: Int) {
        /**空心圆*/
        LETTER(0),//

        /**数字*/
        NUMBER(1),//

        /**实心圆*/
        NONE(-1)
    }

    interface OnIndicatorClickListener {
        fun onSelected(position: Int)
    }
}