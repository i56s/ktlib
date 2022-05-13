package com.i56s.ktlib.views.xrefresh

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import com.i56s.ktlib.utils.SizeUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-09 11:00
 * ### 描述：上拉或下拉的刷新控件
 */
class MaterialLoaderView constructor(context: Context, attrs: AttributeSet?, defstyleAttr: Int) :
    FrameLayout(context, attrs, defstyleAttr), BaseMaterialView {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private lateinit var materialWaveView: MaterialWaveView
    private lateinit var circleProgressBar: CircleProgressBar

    /**进度框大小(dp)*/
    private val progressSize = SizeUtils.dp2px(50f).toInt()

    init {
        if (!isInEditMode) {
            clipToPadding = false
            setWillNotDraw(false)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        materialWaveView = MaterialWaveView(context)
        addView(materialWaveView)
        circleProgressBar = CircleProgressBar(context)
        circleProgressBar.layoutParams = LayoutParams(progressSize, progressSize).apply {
            gravity = Gravity.CENTER
        }
        addView(circleProgressBar)
    }

    override val view: View = this

    override fun onBegin() {
        visibility = View.VISIBLE
        materialWaveView.onBegin()
        circleProgressBar.apply {
            scaleX = 0.001f
            scaleY = 0.001f
            onBegin()
        }
    }

    override fun onSlide(moveX: Float, fractionY: Float) {
        materialWaveView.onSlide(moveX, fractionY)
        circleProgressBar.apply {
            onSlide(moveX, fractionY)
            val a = SizeUtils.limitValue(1f, fractionY)
            scaleX = a
            scaleY = a
            alpha = a
        }
    }

    override fun onRefreshing() {
        materialWaveView.onRefreshing()
        circleProgressBar.onRefreshing()
    }

    override fun onComlete() {
        materialWaveView.onComlete()
        circleProgressBar.onComlete()
        circleProgressBar.translationY = 0f
        circleProgressBar.scaleX = 0f
        circleProgressBar.scaleY = 0f
        visibility = View.GONE
    }
}