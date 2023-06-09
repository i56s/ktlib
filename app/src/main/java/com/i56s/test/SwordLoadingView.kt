package com.i56s.test

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.RotateAnimation
import kotlin.math.min

/**
 * 创建者：wxr
 * 创建时间：2021-09-28 08:48
 * 描述：剑气加载
 */
class SwordLoadingView constructor(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int
) : View(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }

    val anim = ValueAnimator.ofFloat(0f, -360f).apply {
        // Z 轴是逆时针，取负数，得到顺时针的旋转
        interpolator = null
        repeatCount = RotateAnimation.INFINITE
        duration = 1000

        addUpdateListener {
            invalidate()
        }
    }

    init {
        anim.start()
    }

    var radius = 0f
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = min(w, h) / 3f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSword(canvas, 35f, -45f, 0f)
        drawSword(canvas, 50f, 10f, 120f)
        drawSword(canvas, 35f, 55f, 240f)
    }

    private val camera = Camera()
    private val rotateMatrix = Matrix()

    val xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

    private fun drawSword(canvas: Canvas, rotateX: Float, rotateY: Float, startValue: Float) {
        val layerId =
            canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        rotateMatrix.reset()
        camera.save()
        camera.rotateX(rotateX)
        camera.rotateY(rotateY)
        camera.rotateZ(anim.animatedValue as Float + startValue)
        camera.getMatrix(rotateMatrix)
        camera.restore()

        val halfW = width / 2f
        val halfH = height / 2f

        rotateMatrix.preTranslate(-halfW, -halfH)
        rotateMatrix.postTranslate(halfW, halfH)
        canvas.concat(rotateMatrix)
        canvas.drawCircle(halfW, halfH, radius, paint)
        paint.xfermode = xfermode
        canvas.drawCircle(halfW, halfH - 0.05f * radius, radius * 1.01f, paint)
        canvas.restoreToCount(layerId)
        paint.xfermode = null
    }
}