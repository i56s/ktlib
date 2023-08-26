package com.i56s.ktlib.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.graphics.Shader
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import com.i56s.ktlib.R

/**
 * ### 创建者： wxr
 * ### 创建时间： 2023-04-21 19:48
 * ### 描述：高仿ios开关按钮
 */
class SwitchButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        /**状态-关*/
        private const val STATE_SWITCH_OFF = 1
        private const val STATE_SWITCH_OFF2 = 2

        /**状态-开*/
        private const val STATE_SWITCH_ON = 3
        private const val STATE_SWITCH_ON2 = 4
    }

    private val mInterpolator = AccelerateInterpolator(2f)
    private val mPaint = Paint()
    private val mBackgroundPath = Path()
    private val mBarPath = Path()
    private val mBound = RectF()
    private var mAnim1 = 0f
    private var mAnim2 = 0f
    private var mShadowGradient: RadialGradient? = null

    /**按钮宽高形状比率(0,1] 不推荐大幅度调整*/
    private val mAspectRatio = 0.5f
    private val mAnimationSpeed = 0.1f

    /**上一个选中状态*/
    private var mLastCheckedState = 0

    /**当前的选中状态*/
    private var mCheckedState = 0
    private var mCanVisibleDrawing = false

    /**是否显示圆圈阴影*/
    var isOpenShadow = false
        set(value) {
            field = value
            invalidate()
        }

    /**是否选中*/
    var isChecked = false
        set(value) {
            val newState = if (value) STATE_SWITCH_ON else STATE_SWITCH_OFF
            if (newState == mCheckedState) {
                return
            }
            if ((newState == STATE_SWITCH_ON && (mCheckedState == STATE_SWITCH_OFF || mCheckedState == STATE_SWITCH_OFF2)) || (newState == STATE_SWITCH_OFF && (mCheckedState == STATE_SWITCH_ON || mCheckedState == STATE_SWITCH_ON2))) {
                mAnim1 = 1f
            }
            mAnim2 = 1f

            if (!field && newState == STATE_SWITCH_ON) {
                field = true
            } else if (field && newState == STATE_SWITCH_OFF) {
                field = false
            }
            mLastCheckedState = mCheckedState
            mCheckedState = newState
            postInvalidate()
        }

    /**开启状态背景色*/
    var openColor: Int = 0

    /**开启状态圆圈颜色*/
    var openCircleColor: Int = 0

    /**开启状态圆圈描边色*/
    var openCircleStrokeColor: Int = 0

    /**关闭状态背景色*/
    var closeColor: Int = 0

    /**关闭状态圆圈颜色*/
    var closeCircleColor: Int = 0

    /**关闭状态描边色*/
    var closeStrokeColor: Int = 0

    /**关闭状态圆圈描边色*/
    var closeCircleStrokeColor: Int = 0

    /**禁用状态背景色*/
    var disableColor: Int = 0

    /**禁用状态圆圈颜色*/
    var disableCircleColor: Int = 0

    /**圆圈阴影色*/
    var shadowCircleColor: Int = 0

    /**监听器*/
    private var mListener: ((button: SwitchButton, checked: Boolean) -> Unit)? = null

    private var mRight = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mScale = 0f
    private var mOffset = 0f
    private var mRadius = 0f
    private var mStrokeWidth = 0f
    private var mWidth = 0f
    private var mLeft = 0f
    private var bRight = 0f
    private var mOnLeftX = 0f
    private var mOn2LeftX = 0f
    private var mOff2LeftX = 0f
    private var mOffLeftX = 0f
    private var mShadowReservedHeight = 0f

    init {
        if (!isInEditMode) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }

        val array =
            context.obtainStyledAttributes(attrs, R.styleable.SwitchButton, 0, R.style.SwitchButton)
        isChecked = array.getBoolean(R.styleable.SwitchButton_android_checked, false)
        isEnabled = array.getBoolean(R.styleable.SwitchButton_android_enabled, true)
        isOpenShadow = array.getBoolean(R.styleable.SwitchButton_shadowOpen, false)
        openColor = array.getColor(R.styleable.SwitchButton_openColor, 0)
        openCircleColor = array.getColor(R.styleable.SwitchButton_openCircleColor, 0)
        openCircleStrokeColor = array.getColor(R.styleable.SwitchButton_openCircleStrokeColor, 0)
        closeStrokeColor = array.getColor(R.styleable.SwitchButton_closeStrokeColor, 0)
        closeCircleStrokeColor = array.getColor(R.styleable.SwitchButton_closeCircleStrokeColor, 0)
        closeColor = array.getColor(R.styleable.SwitchButton_closeColor, 0)
        closeCircleColor = array.getColor(R.styleable.SwitchButton_closeCircleColor, 0)
        disableColor = array.getColor(R.styleable.SwitchButton_disableColor, 0)
        disableCircleColor = array.getColor(R.styleable.SwitchButton_disableCircleColor, 0)
        shadowCircleColor = array.getColor(R.styleable.SwitchButton_shadowCircleColor, 0)
        array.recycle()
    }

    /**设置选中监听器*/
    fun setOnCheckedListener(listener: ((button: SwitchButton, checked: Boolean) -> Unit)?) {
        mListener = listener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var w = widthMeasureSpec
        var h = heightMeasureSpec
        when (MeasureSpec.getMode(w)) {
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> w = MeasureSpec.makeMeasureSpec(
                156 + paddingLeft + paddingRight, MeasureSpec.EXACTLY
            )

            MeasureSpec.EXACTLY -> {}
        }
        when (MeasureSpec.getMode(h)) {
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> h = MeasureSpec.makeMeasureSpec(
                (MeasureSpec.getSize(w) * mAspectRatio).toInt() + paddingTop + paddingBottom,
                MeasureSpec.EXACTLY
            )

            MeasureSpec.EXACTLY -> {}
        }
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        mCanVisibleDrawing =
            width > paddingLeft + paddingRight && height > paddingTop + paddingBottom
        if (mCanVisibleDrawing) {
            val actuallyDrawingAreaWidth = paddingLeft - paddingRight
            val actuallyDrawingAreaHeight = paddingTop - paddingBottom
            var actuallyDrawingAreaLeft = 0
            var actuallyDrawingAreaRight = 0
            var actuallyDrawingAreaTop = 0
            var actuallyDrawingAreaBottom = 0
            if (actuallyDrawingAreaWidth * mAspectRatio < actuallyDrawingAreaHeight) {
                actuallyDrawingAreaLeft = paddingLeft
                actuallyDrawingAreaRight = width - paddingRight
                val heightExtraSize =
                    (actuallyDrawingAreaHeight - actuallyDrawingAreaWidth * mAspectRatio).toInt()
                actuallyDrawingAreaTop = paddingTop + heightExtraSize / 2
                actuallyDrawingAreaBottom = getHeight() - paddingBottom - heightExtraSize / 2
            } else {
                val widthExtraSize =
                    (actuallyDrawingAreaWidth - actuallyDrawingAreaHeight / mAspectRatio).toInt()
                actuallyDrawingAreaLeft = paddingLeft + widthExtraSize / 2
                actuallyDrawingAreaRight = getWidth() - paddingRight - widthExtraSize / 2
                actuallyDrawingAreaTop = paddingTop
                actuallyDrawingAreaBottom = getHeight() - paddingBottom
            }
            mShadowReservedHeight = (actuallyDrawingAreaBottom - actuallyDrawingAreaTop) * 0.07f
            val left = actuallyDrawingAreaLeft
            val top = actuallyDrawingAreaTop + mShadowReservedHeight
            mRight = actuallyDrawingAreaRight.toFloat()
            val bottom = actuallyDrawingAreaBottom - mShadowReservedHeight

            val sHeight = bottom - top
            mCenterX = (mRight + left) / 2
            mCenterY = (bottom + top) / 2

            mLeft = left.toFloat()
            mWidth = bottom - top
            bRight = left + mWidth
            // OfB
            val halfHeightOfS = mWidth / 2
            mRadius = halfHeightOfS * 0.95f
            // offset of switching
            mOffset = mRadius * 0.2f
            mStrokeWidth = (halfHeightOfS - mRadius) * 2
            mOnLeftX = mRight - mWidth
            mOn2LeftX = mOnLeftX - mOffset
            mOffLeftX = left.toFloat()
            mOff2LeftX = mOffLeftX + mOffset
            mScale = 1 - mStrokeWidth / sHeight

            mBackgroundPath.reset()
            val bound = RectF()
            bound.top = top
            bound.bottom = bottom
            bound.left = left.toFloat()
            bound.right = left + sHeight
            mBackgroundPath.arcTo(bound, 90f, 180f)
            bound.left = mRight - sHeight
            bound.right = mRight
            mBackgroundPath.arcTo(bound, 270f, 180f)
            mBackgroundPath.close()

            mBound.left = mLeft
            mBound.right = bRight
            // bTop = sTop
            mBound.top = top + mStrokeWidth / 2
            // bBottom = sBottom
            mBound.bottom = bottom - mStrokeWidth / 2
            val bCenterX = (bRight + mLeft) / 2
            val bCenterY = (bottom + top) / 2

            val red = shadowCircleColor shr 16 and 0xFF
            val green = shadowCircleColor shr 8 and 0xFF
            val blue = shadowCircleColor and 0xFF
            mShadowGradient = RadialGradient(
                bCenterX,
                bCenterY,
                mRadius,
                Color.argb(200, red, green, blue),
                Color.argb(25, red, green, blue),
                Shader.TileMode.CLAMP
            )
        }
    }

    private fun calcBPath(percent: Float) {
        mBarPath.reset()
        mBound.left = mLeft + mStrokeWidth / 2
        mBound.right = bRight - mStrokeWidth / 2
        mBarPath.arcTo(mBound, 90f, 180f)
        mBound.left = mLeft + percent * mOffset + mStrokeWidth / 2
        mBound.right = bRight + percent * mOffset - mStrokeWidth / 2
        mBarPath.arcTo(mBound, 270f, 180f)
        mBarPath.close()
    }

    private fun calcBTranslate(percent: Float): Float {
        var result = 0f
        when (mCheckedState - mLastCheckedState) {
            1 -> if (mCheckedState == STATE_SWITCH_OFF2) {
                // off -> off2
                result = mOffLeftX
            } else if (mCheckedState == STATE_SWITCH_ON) {
                // on2 -> on
                result = mOnLeftX - (mOnLeftX - mOn2LeftX) * percent
            }

            2 -> if (mCheckedState == STATE_SWITCH_ON) {
                // off2 -> on
                result = mOnLeftX - (mOnLeftX - mOffLeftX) * percent
            } else if (mCheckedState == STATE_SWITCH_ON2) {
                // off -> on2
                result = mOn2LeftX - (mOn2LeftX - mOffLeftX) * percent
            }
            // off -> on
            3 -> result = mOnLeftX - (mOnLeftX - mOffLeftX) * percent
            -1 -> if (mCheckedState == STATE_SWITCH_ON2) {
                // on -> on2
                result = mOn2LeftX + (mOnLeftX - mOn2LeftX) * percent
            } else if (mCheckedState == STATE_SWITCH_OFF) {
                // off2 -> off
                result = mOffLeftX
            }

            -2 -> if (mCheckedState == STATE_SWITCH_OFF) {
                // on2 -> off
                result = mOffLeftX + (mOn2LeftX - mOffLeftX) * percent
            } else if (mCheckedState == STATE_SWITCH_OFF2) {
                // on -> off2
                result = mOff2LeftX + (mOnLeftX - mOff2LeftX) * percent
            }
            // on -> off
            -3 -> result = mOffLeftX + (mOnLeftX - mOffLeftX) * percent
            else -> if (mCheckedState == STATE_SWITCH_OFF) {
                //  off -> off
                result = mOffLeftX
            } else if (mCheckedState == STATE_SWITCH_ON) {
                // on -> on
                result = mOnLeftX
            }
        }
        return result - mOffLeftX
    }

    override fun onDraw(canvas: Canvas?) {
        if (!mCanVisibleDrawing) {
            return
        }

        //设置抗锯齿
        mPaint.isAntiAlias = true

        val isOn = (mCheckedState == STATE_SWITCH_ON || mCheckedState == STATE_SWITCH_ON2)
        // Draw background
        mPaint.style = Paint.Style.FILL
        mPaint.color = if (isOn) openColor else closeStrokeColor
        canvas?.drawPath(mBackgroundPath, mPaint)

        mAnim1 = if (mAnim1 - mAnimationSpeed > 0) mAnim1 - mAnimationSpeed else 0f
        mAnim2 = if (mAnim2 - mAnimationSpeed > 0) mAnim2 - mAnimationSpeed else 0f

        val dsAnim = mInterpolator.getInterpolation(mAnim1)
        val dbAnim = mInterpolator.getInterpolation(mAnim2)
        // Draw background animation
        val scale = mScale * (if (isOn) dsAnim else 1 - dsAnim)
        val scaleOffset = (mRight - mCenterX - mRadius) * (if (isOn) 1 - dsAnim else dsAnim)
        canvas?.save()
        canvas?.scale(scale, scale, mCenterX + scaleOffset, mCenterY)
        if (isEnabled) {
            mPaint.color = closeColor
        } else {
            mPaint.color = disableColor
        }
        canvas?.drawPath(mBackgroundPath, mPaint)
        canvas?.restore()
        // To prepare center bar path
        canvas?.save()
        canvas?.translate(calcBTranslate(dbAnim), mShadowReservedHeight)
        val isState2 = (mCheckedState == STATE_SWITCH_ON2 || mCheckedState == STATE_SWITCH_OFF2)
        calcBPath(if (isState2) 1 - dbAnim else dbAnim)
        // 绘制阴影
        if (isOpenShadow) {
            mPaint.style = Paint.Style.FILL
            mPaint.shader = mShadowGradient
            canvas?.drawPath(mBarPath, mPaint)
            mPaint.shader = null
        }
        canvas?.translate(0f, -mShadowReservedHeight)
        // 画圆
        canvas?.scale(0.98f, 0.98f, mWidth / 2, mWidth / 2)
        mPaint.style = Paint.Style.FILL
        mPaint.color =
            if (!isEnabled) disableCircleColor else if (isOn) openCircleColor else closeCircleColor
        canvas?.drawPath(mBarPath, mPaint)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = mStrokeWidth * 0.5f
        mPaint.color = if (isOn) openCircleStrokeColor else closeCircleStrokeColor
        canvas?.drawPath(mBarPath, mPaint)
        canvas?.restore()

        mPaint.reset()
        if (mAnim1 > 0 || mAnim2 > 0) {
            invalidate()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)
        if (isEnabled && (mCheckedState == STATE_SWITCH_ON || mCheckedState == STATE_SWITCH_OFF) && (mAnim1 * mAnim2 == 0f)) {
            when (event?.action) {
                MotionEvent.ACTION_UP -> {
                    mLastCheckedState = mCheckedState
                    mAnim2 = 1f
                    when (mCheckedState) {
                        STATE_SWITCH_OFF -> {
                            isChecked = true
                            mListener?.invoke(this, true)
                        }

                        STATE_SWITCH_ON -> {
                            isChecked = false
                            mListener?.invoke(this, false)
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val state = SavedState(superState)
        state.checked = isChecked
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state as SavedState
        super.onRestoreInstanceState(state.superState)
        isChecked = state.checked
        mCheckedState = if (isChecked) STATE_SWITCH_ON else STATE_SWITCH_OFF
        invalidate()
    }

    private class SavedState : BaseSavedState {
        var checked = false

        constructor(superState: Parcelable?) : super(superState)

        constructor(inP: Parcel) : super(inP) {
            checked = 1 == inP.readInt()
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(if (checked) 1 else 0)
        }

        override fun describeContents(): Int = 0

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState = SavedState(parcel)

            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}