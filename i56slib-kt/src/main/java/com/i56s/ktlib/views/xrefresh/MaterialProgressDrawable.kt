package com.i56s.ktlib.views.xrefresh

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.*
import android.view.animation.Interpolator
import kotlin.math.*

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-10 16:31
 * ### 描述：画箭头
 */
class MaterialProgressDrawable(context: Context, animExcutor: View) : Drawable(), Animatable {

    companion object {
        // Maps to ProgressBar.Large style
        const val LARGE = 0

        // Maps to ProgressBar default style
        const val DEFAULT = 1
        private val LINEAR_INTERPOLATOR: Interpolator = LinearInterpolator()
        private val END_CURVE_INTERPOLATOR: Interpolator = EndCurveInterpolator()
        private val START_CURVE_INTERPOLATOR: Interpolator = StartCurveInterpolator()
        private val EASE_INTERPOLATOR: Interpolator = AccelerateDecelerateInterpolator()

        // Maps to ProgressBar default style
        private const val CIRCLE_DIAMETER = 40
        private const val CENTER_RADIUS = 8.75f//should add up to 10 when + stroke_width
        private const val STROKE_WIDTH = 2.5f

        // Maps to ProgressBar.Large style
        private const val CIRCLE_DIAMETER_LARGE = 56
        private const val CENTER_RADIUS_LARGE = 12.5f
        private const val STROKE_WIDTH_LARGE = 3f

        /**The duration of a single progress spin in milliseconds.*/
        private const val ANIMATION_DURATION = 1000 * 80 / 60

        /**The number of points in the progress "star".*/
        private const val NUM_POINTS = 5f

        /**Layout info for the arrowhead in dp*/
        private const val ARROW_WIDTH = 10
        private const val ARROW_HEIGHT = 5
        private const val ARROW_OFFSET_ANGLE = 0

        /**Layout info for the arrowhead for the large spinner in dp*/
        private const val ARROW_WIDTH_LARGE = 12
        private const val ARROW_HEIGHT_LARGE = 6
        private const val MAX_PROGRESS_ARC = .8
        private val COLORS = intArrayOf(Color.BLACK)
    }

    /**The indicator ring, used to manage animation state.*/
    private val mCallback = object : Callback {
        override fun invalidateDrawable(p0: Drawable) = invalidateSelf()

        override fun scheduleDrawable(d: Drawable, what: Runnable, p2: Long) =
            scheduleSelf(what, p2)

        override fun unscheduleDrawable(p0: Drawable, p1: Runnable) = unscheduleSelf(p1)
    }
    private val mRing = Ring(this, mCallback).apply {
        colors = COLORS
    }

    /**Canvas rotation in degrees.*/
    private var mFinishing = false
    var rotation = 0f
        set(value) {
            field = value
            invalidateSelf()
        }
    private val mResources = context.resources
    private val mAnimExcutor = animExcutor
    private var mAnimation: Animation? = null
    private var mRotationCount = 0f
    private var mWidth = 0.0
    private var mHeight = 0.0
    var isShowArrowOnFirstStart = false

    init {
        updateSizes(DEFAULT)
        setupAnimators()
    }

    fun setSizeParameters(
        progressCircleWidth: Double, progressCircleHeight: Double,
        centerRadius: Double, strokeWidth: Double, arrowWidth: Float, arrowHeight: Float
    ) {
        mWidth = progressCircleWidth
        mHeight = progressCircleHeight
        mRing.strokeWidth = strokeWidth.toFloat()
        mRing.setCenterRadius(centerRadius)
        mRing.colorIndex = 0
        mRing.setArrowDimensions(arrowWidth.toInt(), arrowHeight.toInt())
        mRing.setInsets(mWidth.toInt(), mHeight.toInt())
    }

    /**
     * Set the overall size for the progress spinner. This updates the radius
     * and stroke width of the ring.
     */
    fun updateSizes(@ProgressDrawableSize size: Int) {
        val metrics = mResources.displayMetrics
        val screenDensity = metrics.density

        if (size == LARGE) {
            setSizeParameters(
                CIRCLE_DIAMETER_LARGE * screenDensity.toDouble(),
                CIRCLE_DIAMETER_LARGE * screenDensity.toDouble(),
                CENTER_RADIUS_LARGE * screenDensity.toDouble(),
                STROKE_WIDTH_LARGE * screenDensity.toDouble(),
                ARROW_WIDTH_LARGE * screenDensity,
                ARROW_HEIGHT_LARGE * screenDensity
            )
        } else {
            setSizeParameters(
                CIRCLE_DIAMETER * screenDensity.toDouble(),
                CIRCLE_DIAMETER * screenDensity.toDouble(),
                CENTER_RADIUS * screenDensity.toDouble(),
                STROKE_WIDTH * screenDensity.toDouble(),
                ARROW_WIDTH * screenDensity,
                ARROW_HEIGHT * screenDensity
            )
        }
    }

    /**
     * @param isShow Set to true to display the arrowhead on the progress spinner.
     */
    fun showArrow(isShow: Boolean) {
        mRing.isShowArrow = isShow
    }

    /**
     * @param scale Set the scale of the arrowhead for the spinner.
     */
    fun setArrowScale(scale: Float) {
        mRing.arrowScale = scale
    }

    /**
     * Set the start and end trim for the progress spinner arc.
     *
     * @param startAngle start angle
     * @param endAngle   end angle
     */
    fun setStartEndTrim(startAngle: Float, endAngle: Float) {
        mRing.startTrim = startAngle
        mRing.endTrim = endAngle
    }

    /**
     * Set the amount of rotation to apply to the progress spinner.
     *
     * @param rotation Rotation is from [0..1]
     */
    fun setProgressRotation(rotation: Float) {
        mRing.rotation = rotation
    }

    /**
     * Update the background color of the circle image view.
     */
    fun setBackgroundColor(color: Int) {
        mRing.backgroundColor = color
    }

    /**
     * Set the colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colors
     */
    fun setColorSchemeColors(colors: IntArray) {
        mRing.colors = colors
        mRing.colorIndex = 0
    }

    override fun getIntrinsicHeight(): Int = mHeight.toInt()

    override fun getIntrinsicWidth(): Int = mWidth.toInt()

    override fun draw(c: Canvas) {
        val bounds = getBounds()
        val saveCount = c.save()
        c.rotate(rotation, bounds.exactCenterX(), bounds.exactCenterY())
        mRing.draw(c, bounds)
        c.restoreToCount(saveCount)
    }

    override fun getAlpha(): Int = mRing.alpha

    override fun setAlpha(alpha: Int) {
        mRing.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        colorFilter?.let { mRing.setColorFilter(it) }
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    override fun isRunning(): Boolean = !(mAnimation?.hasEnded() ?: true)

    override fun start() {
        mAnimation?.reset()
        mRing.storeOriginals()
        mRing.isShowArrow = isShowArrowOnFirstStart

        // Already showing some part of the ring
        if (mRing.endTrim != mRing.startTrim) {
            mFinishing = true;
            mAnimation?.duration = ANIMATION_DURATION / 2L
            mAnimExcutor.startAnimation(mAnimation)
        } else {
            mRing.colorIndex = 0
            mRing.resetOriginals()
            mAnimation?.duration = ANIMATION_DURATION.toLong()
            mAnimExcutor.startAnimation(mAnimation)
        }
    }

    override fun stop() {
        mAnimExcutor?.clearAnimation()
        rotation = 0f
        mRing.isShowArrow = false
        mRing.colorIndex = 0
        mRing.resetOriginals()
    }

    private fun applyFinishTranslation(interpolatedTime: Float, ring: Ring) {
        // shrink back down and complete a full rotation before
        // starting other circles
        // Rotation goes between [0..1].
        val targetRotation = (Math.floor(ring.startingRotation / MAX_PROGRESS_ARC)
                + 1f).toFloat()
        val startTrim =
            ring.startingStartTrim + (ring.startingEndTrim - ring.startingStartTrim) * interpolatedTime
        ring.startTrim = startTrim
        val rotation =
            ring.startingRotation + (targetRotation - ring.startingRotation) * interpolatedTime
        ring.rotation = rotation
    }

    private fun setupAnimators() {
        val ring = mRing
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (mFinishing) {
                    applyFinishTranslation(interpolatedTime, mRing)
                } else {
                    // The minProgressArc is calculated from 0 to create an
                    // angle that
                    // matches the stroke width.
                    val minProgressArc = Math.toRadians(
                        mRing.strokeWidth / (2 * Math.PI * mRing.getCenterRadius())
                    ).toFloat()
                    val startingEndTrim = mRing.startingEndTrim
                    val startingTrim = mRing.startingStartTrim
                    val startingRotation = mRing.startingRotation

                    // Offset the minProgressArc to where the endTrim is
                    // located.
                    val minArc = MAX_PROGRESS_ARC - minProgressArc
                    var endTrim = startingEndTrim + (minArc
                            * START_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime)).toFloat()
                    val startTrim = startingTrim + (MAX_PROGRESS_ARC
                            * END_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime))

                    val sweepTrim = endTrim - startTrim
                    //Avoid the ring to be a full circle
                    if (Math.abs(sweepTrim) >= 1) {
                        endTrim = startTrim.toFloat() + 0.5f
                    }

                    mRing.endTrim = endTrim

                    mRing.startTrim = startTrim.toFloat()

                    val rotation = startingRotation + (0.25f * interpolatedTime)
                    mRing.rotation = rotation

                    val groupRotation =
                        (720.0f / NUM_POINTS) * interpolatedTime + 720.0f * (mRotationCount / NUM_POINTS)
                    this@MaterialProgressDrawable.rotation = groupRotation
                }
            }
        };
        animation.repeatCount = Animation.INFINITE
        animation.repeatMode = Animation.RESTART
        animation.interpolator = LINEAR_INTERPOLATOR
        animation.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation?) {
                mRotationCount = 0f
            }

            override fun onAnimationEnd(animation: Animation?) = Unit

            override fun onAnimationRepeat(animation: Animation) {
                mRing.storeOriginals()
                mRing.goToNextColor()
                mRing.startTrim = mRing.endTrim
                if (mFinishing) {
                    // finished closing the last ring from the swipe gesture; go
                    // into progress mode
                    mFinishing = false
                    animation.duration = ANIMATION_DURATION.toLong()
                    mRing.isShowArrow = false
                } else {
                    mRotationCount = (mRotationCount + 1) % (NUM_POINTS)
                }
            }
        })
        mAnimation = animation
    }

    annotation class ProgressDrawableSize

    private class Ring constructor(drawable: Drawable, callback: Callback) {
        private val mDrawable = drawable
        private val mTempBounds = RectF()
        private val mPaint = Paint().apply {
            strokeCap = Paint.Cap.SQUARE
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        private val mArrowPaint = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        private val mCirclePaint = Paint()
        private val mCallback = callback

        var startTrim = 0f
            set(value) {
                field = value
                invalidateSelf()
            }
        var endTrim = 0f
            set(value) {
                field = value
                invalidateSelf()
            }
        var rotation = 0f
            set(value) {
                field = value
                invalidateSelf()
            }
        var strokeWidth = 5f
            /**@param value Set the stroke width of the progress spinner in pixels.*/
            set(value) {
                field = value
                mPaint.strokeWidth = value
                invalidateSelf()
            }
        private var mStrokeInset = 2.5f
        var colors = intArrayOf()
            /**
             * Set the colors the progress spinner alternates between.
             *
             * @param value Array of integers describing the colors. Must be non-<code>null</code>.
             */
            set(value) {
                field = value
                // if colors are reset, make sure to reset the color index as well
                colorIndex = 0
            }

        // mColorIndex represents the offset into the available mColors that the
        // progress circle should currently display. As the progress circle is
        // animating, the mColorIndex moves by one to the next available color.
        var colorIndex = 0
        var startingStartTrim = 0f
        var startingEndTrim = 0f
        var startingRotation = 0f
        var isShowArrow = false
            set(value) {
                if (field != value) {
                    field = value
                    invalidateSelf()
                }
            }
        private var mArrow: Path? = null
        var arrowScale = 0f
            /** @param value Set the scale of the arrowhead for the spinner.*/
            set(value) {
                if (field != value) {
                    field = value
                    invalidateSelf()
                }
            }
        private var mRingCenterRadius = 0.0
        private var mArrowWidth = 0
        private var mArrowHeight = 0
        var alpha = 0
        var backgroundColor = 0

        /**
         * Set the dimensions of the arrowhead.
         *
         * @param width  Width of the hypotenuse of the arrow head
         * @param height Height of the arrow point
         */
        fun setArrowDimensions(width: Int, height: Int) {
            mArrowWidth = width
            mArrowHeight = height
        }

        /**Draw the progress spinner*/
        fun draw(c: Canvas, bounds: Rect) {
            mTempBounds.set(bounds)
            mTempBounds.inset(mStrokeInset, mStrokeInset)

            val startAngle = (startTrim + rotation) * 360
            val endAngle = (endTrim + rotation) * 360
            val sweepAngle = endAngle - startAngle
            mPaint.color = colors[colorIndex]
            c.drawArc(mTempBounds, startAngle, sweepAngle, false, mPaint)

            if (isShowArrow) drawTriangle(c, startAngle, sweepAngle, bounds)

            if (alpha < 255) {
                mCirclePaint.color = backgroundColor
                mCirclePaint.alpha = 255 - alpha
                c.drawCircle(
                    bounds.exactCenterX(), bounds.exactCenterY(), bounds.width() / 2f,
                    mCirclePaint
                )
            }
        }

        private fun drawTriangle(c: Canvas, startAngle: Float, sweepAngle: Float, bounds: Rect) {
            if (mArrow == null) {
                mArrow = Path()
                mArrow!!.fillType = Path.FillType.EVEN_ODD
            } else {
                mArrow!!.reset()
            }

            // Adjust the position of the triangle so that it is inset as
            // much as the arc, but also centered on the arc.
            val x = (mRingCenterRadius * cos(0.0) + bounds.exactCenterX()).toFloat()
            val y = (mRingCenterRadius * sin(0.0) + bounds.exactCenterY()).toFloat()

            // Update the path each time. This works around an issue in SKIA
            // where concatenating a rotation matrix to a scale matrix
            // ignored a starting negative rotation. This appears to have
            // been fixed as of API 21.
            mArrow?.apply {
                moveTo(0f, 0f)
                lineTo((mArrowWidth) * arrowScale, 0f)
                lineTo(((mArrowWidth) * arrowScale / 2), (mArrowHeight * arrowScale))
                offset(x - ((mArrowWidth) * arrowScale / 2), y)
                close()
                // draw a triangle
                mArrowPaint.color = colors[colorIndex]
                //when sweepAngle < 0 adjust the position of the arrow
                c.rotate(
                    startAngle + (if (sweepAngle < 0) 0f else sweepAngle) - ARROW_OFFSET_ANGLE,
                    bounds.exactCenterX(),
                    bounds.exactCenterY()
                )
                c.drawPath(this, mArrowPaint)
            }
        }

        /**
         * Proceed to the next available ring color. This will automatically
         * wrap back to the beginning of colors.
         */
        fun goToNextColor() {
            colorIndex = (colorIndex + 1) % (colors.size)
        }

        fun setColorFilter(filter: ColorFilter) {
            mPaint.colorFilter = filter
            invalidateSelf()
        }

        fun setInsets(width: Int, height: Int) {
            val minEdge = min(width, height).toFloat()
            val insets = if (mRingCenterRadius <= 0 || minEdge < 0) {
                ceil(strokeWidth / 2.0).toFloat()
            } else {
                (minEdge / 2.0f - mRingCenterRadius).toFloat()
            }
            mStrokeInset = insets
        }

        fun getInsets(): Float = mStrokeInset

        fun getCenterRadius(): Double = mRingCenterRadius

        fun setCenterRadius(centerRadius: Double) {
            mRingCenterRadius = centerRadius
        }

        /**
         * If the start / end trim are offset to begin with, store them so that
         * animation starts from that offset.
         */
        fun storeOriginals() {
            startingStartTrim = startTrim
            startingEndTrim = endTrim
            startingRotation = rotation
        }

        /**
         * Reset the progress spinner to default rotation, start and end angles.
         */
        fun resetOriginals() {
            startingStartTrim = 0f
            startingEndTrim = 0f
            startingRotation = 0f
            startTrim = 0f
            endTrim = 0f
            rotation = 0f
        }

        private fun invalidateSelf() = mCallback.invalidateDrawable(mDrawable)
    }

    /** Squishes the interpolation curve into the second half of the animation. */
    private class EndCurveInterpolator : AccelerateDecelerateInterpolator() {
        override fun getInterpolation(input: Float): Float =
            super.getInterpolation(max(0f, (input - 0.5f) * 2f))
    }

    /** Squishes the interpolation curve into the first half of the animation. */
    private class StartCurveInterpolator : AccelerateDecelerateInterpolator() {
        override fun getInterpolation(input: Float): Float =
            super.getInterpolation(min(1f, input * 2f))
    }
}