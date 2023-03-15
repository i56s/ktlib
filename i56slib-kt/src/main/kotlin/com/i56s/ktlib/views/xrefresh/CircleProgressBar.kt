package com.i56s.ktlib.views.xrefresh

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.i56s.ktlib.R
import com.i56s.ktlib.utils.SizeUtils
import kotlin.math.min

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-09 14:29
 * ### 描述：进度圆圈
 */
class CircleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defstyleAttr: Int = 0
) :
    AppCompatImageView(context, attrs, defstyleAttr), BaseMaterialView {

    private val mProgressDrawable = MaterialProgressDrawable(context, this).apply {
        setStartEndTrim(0f, 0.75f)
    }
    private val mShadowRadius = SizeUtils.dp2px(3.5f).toInt()

    /**进度框背景颜色*/
    private val progressBackGroundColor = 0xFFFAFAFA.toInt()

    /**画笔大小(dp)*/
    private val progressStokeWidth = SizeUtils.dp2px(3f)

    private var mDiameter = 0

    /**是否显示箭头*/
    var isShowArrow = true
        set(value) {
            field = value
            invalidate()
        }

    /**变换的颜色集*/
    var colors =
        intArrayOf(0xffF44336.toInt(), 0xff4CAF50.toInt(), 0xff03A9F4.toInt(), 0xffFFEB3B.toInt())

    init {
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defstyleAttr, 0)

        isShowArrow = a.getBoolean(R.styleable.CircleProgressBar_isShowArrow, true)
        a.recycle()
        super.setImageDrawable(mProgressDrawable)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mDiameter = min(measuredWidth, measuredHeight).let {
            if (it <= 0) SizeUtils.dp2px(40f).toInt()
            else it
        }

        if (background == null) {
            val mBgCircle = ShapeDrawable(OvalShadow(mShadowRadius, mDiameter - mShadowRadius * 2))
            setLayerType(LAYER_TYPE_SOFTWARE, mBgCircle.paint)
            //设置阴影，模糊半径（越大越模糊），阴影离开文字的x横向距离，阴影离开文字的Y横向距离，阴影颜色
            mBgCircle.paint?.setShadowLayer(
                mShadowRadius.toFloat(), 0f, SizeUtils.dp2px(1.75f),
                0x1E000000
            )
            val padding = mShadowRadius
            // set padding so the inner image sits correctly within the shadow.
            setPadding(padding, padding, padding, padding)
            mBgCircle.paint.color = progressBackGroundColor
            setBackgroundDrawable(mBgCircle)
        }

        mProgressDrawable.setBackgroundColor(progressBackGroundColor)
        mProgressDrawable.setColorSchemeColors(colors)
        mProgressDrawable.setSizeParameters(
            mDiameter.toDouble(), mDiameter.toDouble(),
            (mDiameter - progressStokeWidth * 2) / 4.0,
            progressStokeWidth.toDouble(),
            progressStokeWidth * 4f,
            progressStokeWidth * 2f
        )
        if (isShowArrow) {
            mProgressDrawable.isShowArrowOnFirstStart = true
            mProgressDrawable.setArrowScale(1f)
            mProgressDrawable.showArrow(true)
        }
        super.setImageDrawable(null)
        super.setImageDrawable(mProgressDrawable)
        mProgressDrawable.alpha = 255
        if (visibility == VISIBLE) mProgressDrawable.setStartEndTrim(0f, 0.8f)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

    }

    override fun setImageResource(resId: Int) = Unit

    override fun setImageURI(uri: Uri?) = Unit

    override fun setImageDrawable(drawable: Drawable?) = Unit

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mProgressDrawable.stop()
        mProgressDrawable.setVisible(visibility == VISIBLE, false)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mProgressDrawable.stop()
        mProgressDrawable.setVisible(false, false)
    }

    override val view: View = this

    override fun onBegin() {
        visibility = View.VISIBLE
    }

    override fun onSlide(moveX: Float, fractionY: Float) {
        mProgressDrawable.setProgressRotation(fractionY)
    }

    override fun onRefreshing() {
        mProgressDrawable.start()
    }

    override fun onComlete() {
        mProgressDrawable.stop()
        visibility = View.INVISIBLE
    }

    private inner class OvalShadow(shadowRadius: Int, circleDiameter: Int) : OvalShape() {
        private var mShadowRadius = shadowRadius
        private val mShadowPaint = Paint().apply {
            shader = RadialGradient(
                circleDiameter / 2f,
                circleDiameter / 2f,
                shadowRadius.toFloat(),
                intArrayOf(0x3Dff0000, Color.TRANSPARENT),
                null,
                Shader.TileMode.CLAMP
            )
        }
        private var mCircleDiameter = circleDiameter

        override fun draw(canvas: Canvas?, paint: Paint?) {
            val viewWidth = this@CircleProgressBar.width
            val viewHeight = this@CircleProgressBar.height
            canvas?.drawCircle(
                viewWidth / 2f, viewHeight / 2f, (mCircleDiameter / 2 + mShadowRadius).toFloat(),
                mShadowPaint
            )
            paint?.let {
                canvas?.drawCircle(
                    viewWidth / 2f,
                    viewHeight / 2f,
                    mCircleDiameter / 2f,
                    it
                )
            }
        }
    }
}