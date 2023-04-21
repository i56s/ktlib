package com.i56s.ktlib.views

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RadialGradient
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
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
        private const val STATE_SWITCH_OFF = 1
        private const val STATE_SWITCH_OFF2 = 2
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

    /**是否显示按钮阴影*/
    private var mShadow = false

    /**是否选中*/
    private var mChecked = false

    /**开启状态背景色*/
    private var mAccentColor = Color.parseColor("#32D8D2")

    /**开启状态按钮描边色*/
    private var mPrimaryDarkColor = Color.parseColor("#2CBCB7")

    /**关闭状态描边色*/
    private var mOffColor = Color.parseColor("#E3E3E3")

    /**关闭状态按钮描边色*/
    private var mOffDarkColor = Color.parseColor("#BFBFBF")

    /**按钮正常的背景色（关闭状态下）*/
    private var mBgColor = Color.parseColor("#DFDFDF")

    /**按钮禁用的背景色*/
    private var mBgEnabledColor = Color.parseColor("#BFBFBF")

    /**按钮阴影色*/
    private var mShadowColor = Color.parseColor("#333333")

    /**监听器*/
    var mListener: ((button: SwitchButton, checked: Boolean) -> Unit)? = null

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
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        val array = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton)
        mChecked = array.getBoolean(R.styleable.SwitchButton_android_checked, mChecked)
        setEnabled(array.getBoolean(R.styleable.SwitchButton_android_enabled, isEnabled()))
        mCheckedState = if (mChecked) STATE_SWITCH_ON else STATE_SWITCH_OFF
        mLastCheckedState = mCheckedState
        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED ->
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    56 + paddingLeft + paddingRight,
                    MeasureSpec.EXACTLY
                )
        }
        when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED ->
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    (MeasureSpec.getSize(widthMeasureSpec) * mAspectRatio).toInt()
                            + paddingTop + paddingBottom, MeasureSpec.EXACTLY
                )
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }
}