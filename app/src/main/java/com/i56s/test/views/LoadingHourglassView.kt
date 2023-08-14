package com.i56s.test.views

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.annotation.Keep
import com.i56s.ktlib.utils.SizeUtils
import com.i56s.test.R

/**
 * ### 创建者： wxr
 * ### 创建时间： 2023-05-30 14:43
 * ### 描述： 沙漏加载
 */
class LoadingHourglassView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val DURATION_DEFAULT = 5000
    private val FALT_DEFAULT = 7.5f

    /**上下盖子颜色*/
    private var mTopAndBottomColor = Color.parseColor("#EC6941")

    /**左右线条颜色*/
    private var mLeftAndRightColor = Color.parseColor("#844F01")

    /**沙子颜色*/
    //private var mSandColor = Color.parseColor("#00B7EE")
    private var mSandColor = Color.parseColor("#F3A7A5")

    /**沙子下落到底部的时间*/
    private var mDropDuration = 1000

    /**沙子下落完成的时间包括mDropDuration*/
    private var mDuration = 5200

    /**扁度*/
    private var mFalt = 0f

    /**是否自动开启*/
    private var mAuto = false

    /**顶部和底部的画笔*/
    private val mTopAndBottomPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**边框的画笔*/
    private val mFillPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**框内填充的画笔*/
    private val mFramePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**沙子的画笔*/
    private val mSandPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**沙子下落的画笔*/
    private val mDropPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**中心点X*/
    private var centerX = 0f

    /**中心点Y*/
    private var centerY = 0f

    /**漏斗高度(默认100dp)宽度自动计算*/
    private var height = 0f

    /**漏斗半个高度*/
    private var heightHalf = 0f

    /**顶部底部宽度*/
    private var width = 0f

    /**左边框*/
    private val mLeftPath = Path()

    /**右边框*/
    private val mRightPath = Path()

    /**顶部填充边框*/
    private val mFillTop = Path()

    /**底部填充边框*/
    private val mFillBottom = Path()

    /**底部上升*/
    private val mMoveBottom = Path()

    /**顶部下降*/
    private val mMoveTop = Path()

    /**默认底部的沙子高度*/
    private var defaultBottomHeight = 0f

    /**结束沙子的高度*/
    private var maxBottomHeight = 0f

    /**沙子下降*/
    private var progressTop = 0f

    /**沙子上升*/
    private var progressBottom = 0f

    /**沙子下落（直线）*/
    private var progressDrop = 0f

    /**沙子上升波动*/
    private var bottomWave = 0f

    /**沙子下降波动*/
    private var topWave = 0f

    /**判断方向0-1.1 1.1-0.9*/
    private var temp = 0f

    private val mDropAnimator = ObjectAnimator.ofFloat(this, "progressDrop", 0f, 1f)
    private val mTopAnimator = ObjectAnimator.ofFloat(this, "progressTop", 0f, 1f)
    private var mBottomAnimator: ObjectAnimator? = null
    private var mWaveBottomAnimator: ObjectAnimator? = null
    private var mWaveTopAnimator: ObjectAnimator? = null
    private val mAnimatorSet: AnimatorSet = AnimatorSet()

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingHourglassView)
        mTopAndBottomColor =
            typedArray.getColor(R.styleable.LoadingHourglassView_hv_topAndBottom_color, mTopAndBottomColor)
        mLeftAndRightColor =
            typedArray.getColor(R.styleable.LoadingHourglassView_hv_leftAndRight_color, mLeftAndRightColor)
        mSandColor = typedArray.getColor(R.styleable.LoadingHourglassView_hv_sand_color, mSandColor)
        mDuration = typedArray.getInt(R.styleable.LoadingHourglassView_hv_duration, DURATION_DEFAULT)
        mFalt = typedArray.getFloat(R.styleable.LoadingHourglassView_hv_flat, FALT_DEFAULT)
        mAuto = typedArray.getBoolean(R.styleable.LoadingHourglassView_hv_auto, false)

        if (mFalt <= 0) {
            mFalt = FALT_DEFAULT
        }
        if (mDuration <= 0) {
            mDuration = DURATION_DEFAULT
        }
        typedArray.recycle()

        mTopAndBottomPaint.strokeCap = Paint.Cap.ROUND
        mTopAndBottomPaint.style = Paint.Style.STROKE

        mFillPaint.color = Color.parseColor("#ffffff")
        mFillPaint.style = Paint.Style.FILL

        mFramePaint.style = Paint.Style.STROKE
        mFramePaint.color = mLeftAndRightColor

        mSandPaint.color = mSandColor
        mSandPaint.style = Paint.Style.FILL

        mDropPaint.color = mSandColor
        mDropPaint.alpha = (255 * 0.7f).toInt()
        mDropPaint.style = Paint.Style.STROKE
    }

    private fun initAnimator() {
        mWaveBottomAnimator = ObjectAnimator.ofFloat(
            this, "bottomWave", 0f, -height / 7.5f, height / 10f, -height / 20f
        )
        mWaveTopAnimator = ObjectAnimator.ofFloat(this, "topWave", 0f, height / 20, 0f)

        val keyframe = Keyframe.ofFloat(0f, 0f)
        val keyframe01 = Keyframe.ofFloat(0.85f, 1.1f)
        val keyframe02 = Keyframe.ofFloat(1f, 0.9f)
        val valuesHolder =
            PropertyValuesHolder.ofKeyframe("progressBottom", keyframe, keyframe01, keyframe02)
        mBottomAnimator = ObjectAnimator.ofPropertyValuesHolder(this, valuesHolder)

        val duration = (mDuration - mDropDuration).toLong()
        mBottomAnimator?.duration = duration
        mTopAnimator.duration = duration
        mWaveBottomAnimator?.duration = duration
        mWaveTopAnimator?.duration = duration
        mDropAnimator.duration = mDropDuration.toLong()

        mDropAnimator.interpolator = AccelerateInterpolator()

        mAnimatorSet.play(mTopAnimator).with(mWaveBottomAnimator).with(mBottomAnimator)
            .with(mWaveTopAnimator).after(mDropAnimator)

        mAnimatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                mDropPaint.color = mSandColor
                /*if (mStateListener != null) {
                    mStateListener.onStart()
                }*/
            }

            override fun onAnimationEnd(animation: Animator?) {
                //防止重绘显示出来
                mDropPaint.color = Color.TRANSPARENT
                /*if (mStateListener != null) {
                    mStateListener.onEnd()
                }*/
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }

        })

        if (mAuto) start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specMode = MeasureSpec.getMode(heightMeasureSpec)
        val specSize = MeasureSpec.getSize(heightMeasureSpec)
        when (specMode) {
            MeasureSpec.UNSPECIFIED, MeasureSpec.AT_MOST -> {
                //减去上下20盖子
                height = SizeUtils.dp2px(100f) - SizeUtils.dp2px(100f) / 7.5f
                width = height / 2 + height / mFalt
                setMeasuredDimension(
                    (width + height / 15).toInt() + paddingLeft + paddingRight,
                    (height + SizeUtils.dp2px(100f) / 7.5f).toInt() + paddingTop + paddingBottom
                )
            }

            MeasureSpec.EXACTLY -> {
                height = specSize - specSize / 7.5f
                width = height / 2 + height / mFalt
                setMeasuredDimension(
                    (width + specSize / 15).toInt() + paddingLeft + paddingRight,
                    specSize + paddingTop + paddingBottom
                )
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimatorSet.end()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initSize()

        centerX = w / 2f
        centerY = h / 2f
        maxBottomHeight = heightHalf - height / 7.5f

        //左边贝瑟尔曲线
        mLeftPath.moveTo(centerX - width / 2 /*+ height / 30*/, centerY - heightHalf)
        mLeftPath.quadTo(
            centerX - width / 2,
            centerY - heightHalf / 3,
            centerX - height / 20,
            centerY - height / 20
        )
        mLeftPath.quadTo(centerX, centerY, centerX - height / 20, centerY + height / 20)
        mLeftPath.quadTo(
            centerX - width / 2,
            centerY + heightHalf / 3,
            centerX - width / 2 /*+ height / 30*/,
            centerY + heightHalf
        )

        //右边贝瑟尔曲线
        mRightPath.moveTo(centerX + width / 2 /*- height / 30*/, centerY - heightHalf)
        mRightPath.quadTo(
            centerX + width / 2,
            centerY - heightHalf / 3,
            centerX + height / 20,
            centerY - height / 20
        )
        mRightPath.quadTo(centerX, centerY, centerX + height / 20, centerY + height / 20)
        mRightPath.quadTo(
            centerX + width / 2,
            centerY + heightHalf / 3,
            centerX + width / 2 /*- height / 30*/,
            centerY + heightHalf
        )

        //上半内边框填充
        mFillTop.moveTo(centerX - width / 2 + height / 15, centerY - heightHalf - height / 20)
        mFillTop.quadTo(
            centerX - width / 2 + height / 30,
            centerY - heightHalf / 3 - height / 30,
            centerX - height / 20,
            centerY - height / 12
        )
        mFillTop.lineTo(centerX, centerY - height / 30)
        mFillTop.lineTo(centerX + height / 20, centerY - height / 12)
        mFillTop.quadTo(
            centerX + width / 2 - height / 30,
            centerY - heightHalf / 3 - height / 30,
            centerX + width / 2 - height / 15,
            centerY - heightHalf - height / 20
        )

        //下半内边框填充
        mFillBottom.moveTo(centerX - height / 20, centerY + height / 12)
        mFillBottom.quadTo(
            centerX - width / 2 + height / 30,
            centerY + heightHalf / 3 + height / 30,
            centerX - width / 2 + height / 15,
            centerY + heightHalf + height / 30
        )
        mFillBottom.lineTo(centerX + width / 2 - height / 15, centerY + heightHalf + height / 30)
        mFillBottom.quadTo(
            centerX + width / 2 - height / 30,
            centerY + heightHalf / 3 + height / 30,
            centerX + height / 20,
            centerY + height / 12
        )

        //底部path
        mMoveBottom.moveTo(centerX - width / 2 + height / 15, centerY + heightHalf)
        mMoveBottom.lineTo(
            centerX - width / 2 + height / 15,
            centerY + heightHalf - defaultBottomHeight
        )
        mMoveBottom.lineTo(
            centerX + width / 2 - height / 15,
            centerY + heightHalf - defaultBottomHeight
        )
        mMoveBottom.lineTo(centerX + width / 2 - height / 15, centerY + heightHalf)
    }

    private fun initSize() {
        heightHalf = height / 2
        defaultBottomHeight = 0f
        mTopAndBottomPaint.strokeWidth = height / 15
        mFramePaint.strokeWidth = height / 60
        mDropPaint.strokeWidth = height / 75

        mDropDuration = (800 * height / SizeUtils.dp2px(100f)).toInt()
        initAnimator()
    }

    override fun onDraw(canvas: Canvas?) {
        //画边框
        drawFrame(canvas)
        //画上边沙子
        drawTopSand(canvas)
        //画下边沙子
        drawDownSand(canvas)
        //画下落的沙子
        drawDropLine(canvas)
        //画顶部和底部盖子
        drwaTopAndBottom(canvas)
    }

    /**画顶部和底部盖子*/
    private fun drwaTopAndBottom(canvas: Canvas?) {
        mTopAndBottomPaint.color = mTopAndBottomColor
        canvas?.drawLine(
            centerX - width / 2,
            centerY - heightHalf - height / 30,
            centerX + width / 2,
            centerY - heightHalf - height / 30,
            mTopAndBottomPaint
        )
        canvas?.drawLine(
            centerX - width / 2,
            centerY + heightHalf + height / 30,
            centerX + width / 2,
            centerY + heightHalf + height / 30,
            mTopAndBottomPaint
        )
    }

    /**画下边沙子*/
    private fun drawDownSand(canvas: Canvas?) {
        val size = canvas?.saveLayer(null, null, Canvas.ALL_SAVE_FLAG) ?: 0
        canvas?.drawPath(mFillBottom, mFillPaint)
        mSandPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        calculationBottomMovePath()
        canvas?.drawPath(mMoveBottom, mSandPaint)
        mSandPaint.xfermode = null
        canvas?.restoreToCount(size)
    }

    /**画上边沙子*/
    private fun drawTopSand(canvas: Canvas?) {
        val count = canvas?.saveLayer(null, null, Canvas.ALL_SAVE_FLAG) ?: 0
        canvas?.drawPath(mFillTop, mFillPaint)
        mSandPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        calculationTopMovePath()
        canvas?.drawPath(mMoveTop, mSandPaint)
        mSandPaint.xfermode = null
        canvas?.restoreToCount(count)
    }

    private fun drawFrame(canvas: Canvas?) {
        canvas?.drawPath(mLeftPath, mFramePaint)
        canvas?.drawPath(mRightPath, mFramePaint)
    }

    /**画下落的沙子*/
    private fun drawDropLine(canvas: Canvas?) {
        //正向下落
        if (temp - progressBottom <= 0) {
            val endY =
                centerY - height / 25 + (heightHalf - defaultBottomHeight + height / 25) * progressDrop
            canvas?.drawLine(centerX, centerY - height / 25, centerX, endY, mDropPaint)
            temp = progressBottom
        } else {
            var v = 0f

            //progressTop 0-1 加速 如果设置的完成时间越长这里速度就得越大，避免最后落下来很慢
            var offset = 1f
            if (mDuration > DURATION_DEFAULT) {
                offset = (mDuration / DURATION_DEFAULT).toFloat()
            }
            v =
                if (((1.1f - progressBottom) / 0.2f) * offset >= 1) 1f else ((1.1f - progressBottom) / 0.2f) * offset

            val endY = centerY + heightHalf - maxBottomHeight + height / 5
            //变化速率1.1-0.9  v- 0-1

            val startY =
                centerY - height / 25 + (heightHalf - maxBottomHeight + height / 25f + height / 5) * v
            canvas?.drawLine(centerX, startY, centerX, endY, mDropPaint)
            temp = progressBottom
        }
    }

    /**计算向下移动的轨迹*/
    private fun calculationTopMovePath() {
        mMoveTop.reset()
        var offset = 1
        if (temp - progressBottom > 0 && mDuration > DURATION_DEFAULT) {
            offset = mDuration / DURATION_DEFAULT
        }
        val v = if (progressTop * offset > 1) 1f else progressTop * offset
        val top = centerY - heightHalf + height / 20 + (heightHalf - height / 20) * v
        mMoveTop.moveTo(centerX - width / 2 + height / 30, top)
        mMoveTop.lineTo(centerX - width / 2 + height / 30, centerY)
        mMoveTop.lineTo(centerX + width / 2 - height / 30, centerY)
        mMoveTop.lineTo(centerX + width / 2 - height / 30, top)
        mMoveTop.quadTo(centerX, top + topWave, centerX - width / 2 + height / 30, top)
    }

    /**计算向上移动的轨迹*/
    private fun calculationBottomMovePath() {
        mMoveBottom.reset()
        val top =
            centerY + heightHalf - defaultBottomHeight - (maxBottomHeight - defaultBottomHeight) * progressBottom
        mMoveBottom.moveTo(centerX - width / 2 + height / 15, centerY + heightHalf)
        mMoveBottom.lineTo(centerX - width / 2 + height / 15, top)
        mMoveBottom.quadTo(centerX, top + bottomWave, centerX + width / 2 - height / 15, top)
        mMoveBottom.lineTo(centerX + width / 2 - height / 15, centerY + heightHalf)
    }

    /**重置*/
    fun reset() {
        if (mAnimatorSet.isStarted) {
            mAnimatorSet.cancel()
        }
        progressTop = 0f
        progressBottom = 0f
        progressDrop = 0f
        bottomWave = 0f
        topWave = 0f
        temp = 0f
        invalidate()
    }

    @Keep
    private fun setProgressDrop(progressDrop: Float) {
        this.progressDrop = progressDrop
        invalidate()
    }

    @Keep
    private fun setProgressBottom(progressBottom: Float) {
        this.progressBottom = progressBottom
    }

    @Keep
    private fun setBottomWave(wave: Float) {
        this.bottomWave = wave
    }

    @Keep
    private fun setTopWave(wave: Float) {
        this.topWave = wave
    }

    @Keep
    private fun setProgressTop(progress: Float) {
        this.progressTop = progress
        invalidate()
    }

    /**上下盖子颜色*/
    fun setTopAndBottomColor(topAndBottomColor: Int) {
        mTopAndBottomColor = topAndBottomColor
        invalidate()
    }

    /**左右边框颜色*/
    fun setLeftAndRightColor(leftAndRightColor: Int) {
        mLeftAndRightColor = leftAndRightColor
        mFramePaint.setColor(mLeftAndRightColor)
        invalidate()
    }

    /**沙子颜色*/
    fun setSandColor(sandColor: Int) {
        mSandColor = sandColor
        mSandPaint.setColor(mSandColor)
        mDropPaint.setColor(mSandColor)
        invalidate()
    }

    /**设置动画时间
     * @param duration 完成动画时间（包括起始沙子下落时间）*/
    fun setDuration(duration: Int) {
        mDuration = duration
        if (mDuration <= 0) {
            mDuration = DURATION_DEFAULT
        }
    }

    /**设置扁度默认7.5f*/
    fun setFalt(falt: Float) {
        mFalt = falt
        if (mFalt <= 0) {
            mFalt = FALT_DEFAULT
        }

        invalidate()
    }

    /**开启*/
    fun start() {
        post {
            if (!mAnimatorSet.isStarted) {
                if (0 < progressBottom) {
                    reset()
                }
                mAnimatorSet.start()
            }
        }
    }

    /**结束*/
    fun end() {
        mAnimatorSet.end()
    }

    /**状态*/
    fun isStart() = mAnimatorSet.isStarted

    fun resume() {
        mAnimatorSet.resume()
    }

    fun pause() {
        mAnimatorSet.pause()
    }
}