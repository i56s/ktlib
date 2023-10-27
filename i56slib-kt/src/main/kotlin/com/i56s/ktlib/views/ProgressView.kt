package com.i56s.ktlib.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import com.i56s.ktlib.R
import com.i56s.ktlib.utils.LogUtils

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
    private var mWidth = 0f

    /**控件高度*/
    private var mHeight = 0f

    /**进度条未完成画笔*/
    private val mBgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeCap = Paint.Cap.ROUND
    }

    /**进度变化监听器*/
    private var mListener: ((progress: Int, view: View) -> Unit)? = null
    private var mSlideStartListener: ((view: View) -> Unit)? = null
    private var mSlideStopListener: ((view: View) -> Unit)? = null

    /**进度条宽度*/
    var progressWidth = 0f

    /**当前进度*/
    var progress = 20
        set(value) {
            field = if (value > maxProgress) maxProgress else 0.coerceAtLeast(value)
            invalidate()
            mListener?.invoke(field, this@ProgressView)
        }

    /**最大进度*/
    var maxProgress = 100
        set(value) {
            field = 0.coerceAtLeast(value)
            invalidate()
        }

    /**进度颜色*/
    var progressColor = 0

    /**进度背景颜色*/
    var progressBackgroundColor = 0

    /**滑块*/
    var thumbDrawable: Drawable? = null

    /**进度开始位置*/
    var gravity: Int = 0
        set(value) {
            field = 0.coerceAtLeast(value)
        }

    /**是否可拖动*/
    var isDraggable: Boolean = false

    /**滑块宽度*/
    private var thumbW = 0f

    /**滑块高度*/
    private var thumbH = 0f

    /**背景框*/
    private val bgRect = RectF()

    /**进度框*/
    private val proRect = RectF()

    /**方向*/
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
        gravity = array.getInt(R.styleable.ProgressView_android_gravity, -1)
        progressWidth = array.getDimension(R.styleable.ProgressView_progressWidth, 0f)
        progressColor = array.getColor(R.styleable.ProgressView_progressColor, 0)
        isDraggable = array.getBoolean(R.styleable.ProgressView_isDraggable, false)
        progressBackgroundColor =
            array.getColor(R.styleable.ProgressView_progressBackgroundColor, 0)
        thumbDrawable = array.getDrawable(R.styleable.ProgressView_android_thumb)
        thumbDrawable?.let {
            thumbW = it.intrinsicWidth.toFloat()
            thumbH = it.intrinsicHeight.toFloat()

            thumbW = if (thumbW == 0f) 20f else thumbW
            thumbH = if (thumbH == 0f) 20f else thumbH
        }
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wm = MeasureSpec.getMode(widthMeasureSpec)
        val hm = MeasureSpec.getMode(heightMeasureSpec)

        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        val w = thumbW / 2f
        val h = thumbH / 2f

        if (orientation == VERTICAL) {
            //竖向
            if (wm == MeasureSpec.AT_MOST || wm == MeasureSpec.UNSPECIFIED) {
                mWidth = thumbW.coerceAtLeast(progressWidth) + paddingStart + paddingEnd
            }
            bgRect.set(
                mWidth / 2f - progressWidth / 2f,//
                0f + paddingTop + h,//
                mWidth / 2f + progressWidth / 2f,//
                mHeight - paddingBottom - h//
            )
        } else {
            //横向
            if (hm == MeasureSpec.AT_MOST || wm == MeasureSpec.UNSPECIFIED) {
                mHeight = thumbH.coerceAtLeast(progressWidth) + paddingTop + paddingBottom
            }
            bgRect.set(
                0f + paddingStart + w,//
                mHeight / 2f - progressWidth / 2f,//
                mWidth - paddingEnd - w,//
                mHeight / 2f + progressWidth / 2f
            )
        }
        proRect.set(bgRect)

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(mWidth.toInt(), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mHeight.toInt(), MeasureSpec.EXACTLY)
        )

        //有效的控件宽高
        mWidth = bgRect.right - bgRect.left
        mHeight = bgRect.bottom - bgRect.top
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isDraggable) return super.onTouchEvent(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                progress = if (orientation == HORIZONTAL) {
                    if (gravity == Gravity.RIGHT || gravity == Gravity.END) {
                        ((mWidth - event.x + bgRect.left) * maxProgress / mWidth + 0.5f).toInt()
                    } else ((event.x - bgRect.left) * maxProgress / mWidth + 0.5f).toInt()
                } else {
                    if (gravity == Gravity.BOTTOM) {
                        ((mHeight - event.y + bgRect.top) * maxProgress / mHeight + 0.5f).toInt()
                    } else ((event.y - bgRect.top) * maxProgress / mHeight + 0.5f).toInt()
                }
                if (event.action == MotionEvent.ACTION_DOWN) {
                    mSlideStartListener?.invoke(this)
                }
                return true
            }

            //手指抬起
            MotionEvent.ACTION_UP -> mSlideStopListener?.invoke(this)
        }
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        //进度条高度的一半
        val center = progressWidth / 2f
        //滑块的一半
        val w = thumbW / 2
        val h = thumbH / 2

        if (orientation == VERTICAL) {
            //竖向
            when (gravity) {

                //由下到上
                Gravity.BOTTOM -> proRect.top =
                    mHeight - progress * mHeight / maxProgress.toFloat() + h + paddingTop

                //由上到下
                else -> proRect.bottom = progress / maxProgress.toFloat() * mHeight + h + paddingTop
            }
        } else {
            //横向
            when (gravity) {
                //由右到左
                Gravity.RIGHT, Gravity.END -> proRect.left =
                    mWidth - progress / maxProgress.toFloat() * mWidth + w + paddingStart

                //由左到右
                else -> proRect.right = progress / maxProgress.toFloat() * mWidth + w + paddingStart
            }
        }
        mBgPaint.color = progressBackgroundColor
        //画进度条背景
        canvas?.drawRoundRect(bgRect, center, center, mBgPaint)
        mBgPaint.color = progressColor
        //画当前进度
        canvas?.drawRoundRect(proRect, center, center, mBgPaint)
        //画滑块
        thumbDrawable?.let {
            if (orientation == HORIZONTAL) {
                //横向
                if (gravity == Gravity.RIGHT || gravity == Gravity.END) {
                    it.setBounds(
                        (proRect.left - w).toInt(), (proRect.bottom - center - h).toInt(),
                        (proRect.left + w).toInt(), (proRect.bottom - center + h).toInt()
                    )
                } else {
                    it.setBounds(
                        (proRect.right - w).toInt(), (proRect.bottom - center - h).toInt(),
                        (proRect.right + w).toInt(), (proRect.bottom - center + h).toInt()
                    )
                }
            } else {
                //竖向
                if (gravity == Gravity.BOTTOM) {
                    it.setBounds(
                        (proRect.right - center - w).toInt(), (proRect.top - h).toInt(),
                        (proRect.right - center + w).toInt(), (proRect.top + h).toInt()
                    )
                } else {
                    it.setBounds(
                        (proRect.right - center - w).toInt(), (proRect.bottom - h).toInt(),
                        (proRect.right - center + w).toInt(), (proRect.bottom + h).toInt()
                    )
                }
            }
            canvas?.let(it::draw)
        }
    }

    /**进度变化监听*/
    fun setOnProgressChangeListener(listener: ((progress: Int, view: View) -> Unit)?) {
        mListener = listener
    }

    /**
     * 进度滑动监听
     * @param slideStart 滑动开始
     * @param slideStop 滑动结束
     */
    fun setOnProgressSlideListener(
            slideStart: ((view: View) -> Unit)? = null,
            slideStop: ((view: View) -> Unit)?
    ) {
        mSlideStartListener = slideStart
        mSlideStopListener = slideStop
    }
}