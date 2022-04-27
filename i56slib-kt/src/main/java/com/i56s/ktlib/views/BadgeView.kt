package com.i56s.ktlib.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.i56s.ktlib.R

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-03-24 17:18
 * ### 描述：红点数字
 */
class BadgeView constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private val mCirclePaint = Paint()

    private val mTextPaint = Paint()
    private var mText = "99+"
    private val mBounds = Rect()

    /**最大数量*/
    var maxBadge = 100

    /**文字颜色，默认白色*/
    var textColor = Color.WHITE

    /**背景颜色，默认红色*/
    var circleColor = Color.parseColor("#D3321B")

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.BadgeView)
        circleColor = typedArray.getColor(R.styleable.BadgeView_badgeBackgroundColor, circleColor)
        textColor = typedArray.getColor(R.styleable.BadgeView_badgeTextColor, Color.WHITE)
        setBadge(typedArray.getInt(R.styleable.BadgeView_badge, 100))
        typedArray.recycle()

        mCirclePaint.isAntiAlias = true
        mCirclePaint.style = Paint.Style.FILL

        mTextPaint.isAntiAlias = true
        mTextPaint.typeface = Typeface.DEFAULT_BOLD
    }

    override fun onDraw(canvas: Canvas?) {
        mCirclePaint.color = circleColor
        mTextPaint.color = textColor
        val vw = width
        val vh = height

        //画圆
        canvas?.drawCircle(vw / 2f, vh / 2f, Math.min(vw, vh) / 2f - 5f, mCirclePaint)

        mTextPaint.textSize =
            if (mText.length > 1) Math.min(vw, vh) / mText.length * 1f else Math.min(vw, vh) * 0.7f
        mTextPaint.getTextBounds(mText, 0, mText.length, mBounds)
        val textWith = mBounds.width()
        val textHeight = mBounds.height()
        canvas?.drawText(
            mText, vw / 2f - textWith / 2f - (if (mText.equals("1")) 5 else 0),
            vh / 2f + textHeight / 2f, mTextPaint
        )
    }

    /** 设置显示的数量 */
    fun setBadge(badge: Int) {
        this.mText = if (badge >= this.maxBadge) "${
            this.maxBadge - 1
        }+" else badge.toString()
        visibility = if (badge <= 0) INVISIBLE else VISIBLE
        invalidate()
    }
}