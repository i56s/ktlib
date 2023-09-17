package com.i56s.ktlib.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.i56s.ktlib.R

/**
 * ### 创建者： wxr
 * ### 创建时间： 2023-09-15 20:11
 * ### 描述：进度条
 */
class ProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    /**控件宽度*/
    private var mWidth = 0

    /**控件高度*/
    private var mHeight = 0

    /**进度条未完成画笔*/
    private val mBgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
    }

    /**进度条宽度*/
    var progressWidth = 0f

    /**当前进度*/
    var progress = 20
        set(value) {
            field = value
            invalidate()
        }

    /**最大进度*/
    var maxProgress = 100

    /**进度颜色*/
    var progressColor = 0

    /**进度背景颜色*/
    var progressBackgroundColor = 0

    /**背景框*/
    private val bgRect = RectF()

    /**进度框*/
    private val proRect = RectF()

    var orientation: Int = 0
        set(value) {
            if (value < 0) return
            field = value
            requestLayout()
        }

    init {
        val array =
            context.obtainStyledAttributes(attrs, R.styleable.ProgressView, 0, R.style.ProgressView)
        orientation = array.getInt(R.styleable.ProgressView_android_orientation, -1)
        progress = array.getInt(R.styleable.ProgressView_android_progress, 0)
        maxProgress = array.getInt(R.styleable.ProgressView_android_max, 0)
        progressWidth = array.getDimension(R.styleable.ProgressView_progressWidth, 0f)
        progressColor = array.getColor(R.styleable.ProgressView_progressColor, 0)
        progressBackgroundColor =
            array.getColor(R.styleable.ProgressView_progressBackgroundColor, 0)
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        if (orientation == VERTICAL) {
            //竖向
            bgRect.set(
                mWidth / 2f - progressWidth / 2f + paddingStart,//
                0f + paddingTop,//
                mWidth / 2f + progressWidth / 2f - paddingEnd,//
                mHeight.toFloat() - paddingBottom //
            )
        } else {
            //横向
            bgRect.set(
                0f + paddingStart,//
                mHeight / 2f - progressWidth / 2f + paddingTop,//
                mWidth.toFloat() - paddingEnd,//
                mHeight / 2f + progressWidth / 2f - paddingBottom
            )
        }
        proRect.set(bgRect)
    }

    override fun onDraw(canvas: Canvas?) {
        //进度条高度的一半
        val center = progressWidth / 2f
        canvas?.save()
        if (orientation == VERTICAL) {
            //竖向
            proRect.bottom =
                progress / maxProgress.toFloat() * (bgRect.bottom - bgRect.top) + paddingBottom
            canvas?.rotate(180f, bgRect.centerX(), bgRect.centerY())
        } else {
            //横向
            proRect.right =
                progress / maxProgress.toFloat() * (bgRect.right - bgRect.left) + paddingEnd
        }
        mBgPaint.color = progressBackgroundColor
        //画进度条背景
        canvas?.drawRoundRect(bgRect, center, center, mBgPaint)
        mBgPaint.color = progressColor
        //画当前进度
        canvas?.drawRoundRect(proRect, center, center, mBgPaint)
        canvas?.restore()
    }
}