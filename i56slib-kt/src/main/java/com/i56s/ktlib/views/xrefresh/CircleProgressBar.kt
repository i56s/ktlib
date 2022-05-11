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
class CircleProgressBar constructor(context: Context, attrs: AttributeSet?, defstyleAttr: Int) :
    AppCompatImageView(context, attrs, defstyleAttr), BaseMaterialView {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private var mProgressDrawable = MaterialProgressDrawable(context, this).apply {
        setStartEndTrim(0f, 0.75f)
    }
    private var mBgCircle: ShapeDrawable? = null
    private var mShadowRadius = SizeUtils.dp2px(3.5f).toInt()
    var progressBackGroundColor = 0
        set(value) {
            invalidate()
            field = value
        }

    /**dp*/
    var progressStokeWidth = 0f
        set(value) {
            field = SizeUtils.dp2px(value)
            invalidate()
        }
    private var mArrowWidth = 0
    private var mArrowHeight = 0
    private var mDiameter = 0
    private var mInnerRadius = 0
    var isShowArrow = false
        set(value) {
            field = value
            invalidate()
        }
    var circleBackgroundEnabled = false
        set(value) {
            field = value
            invalidate()
        }
    var colors =
        intArrayOf(0xffF44336.toInt(), 0xff4CAF50.toInt(), 0xff03A9F4.toInt(), 0xffFFEB3B.toInt())
        set(value) {
            mProgressDrawable.setColorSchemeColors(value)
            field = value
        }

    init {
        val a =
            context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defstyleAttr, 0)

        progressBackGroundColor = a.getColor(
            R.styleable.CircleProgressBar_mlpb_background_color, 0xFFFAFAFA.toInt()
        )

        mInnerRadius =
            a.getDimensionPixelOffset(R.styleable.CircleProgressBar_mlpb_inner_radius, -1)

        progressStokeWidth = a.getDimensionPixelOffset(
            R.styleable.CircleProgressBar_mlpb_progress_stoke_width,
            3
        ).toFloat()

        mArrowWidth = a.getDimensionPixelOffset(
            R.styleable.CircleProgressBar_mlpb_arrow_width, -1
        )
        mArrowHeight = a.getDimensionPixelOffset(
            R.styleable.CircleProgressBar_mlpb_arrow_height, -1
        )

        isShowArrow = a.getBoolean(R.styleable.CircleProgressBar_mlpb_show_arrow, false)
        circleBackgroundEnabled =
            a.getBoolean(R.styleable.CircleProgressBar_mlpb_enable_circle_background, true)
        a.recycle()
        super.setImageDrawable(mProgressDrawable)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mDiameter = min(measuredWidth, measuredHeight).let {
            if (it <= 0) SizeUtils.dp2px(40f).toInt()
            else it
        }

        if (background == null && circleBackgroundEnabled) {
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
            if (mInnerRadius <= 0) (mDiameter - progressStokeWidth * 2) / 4.0 else mInnerRadius.toDouble(),
            progressStokeWidth.toDouble(),
            if (mArrowWidth < 0) progressStokeWidth * 4f else mArrowWidth.toFloat(),
            if (mArrowHeight < 0) progressStokeWidth * 2f else mArrowHeight.toFloat()
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

    override fun onSlide(moveX: Float,fraction: Float) {
        mProgressDrawable.setProgressRotation(fraction)
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