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
 * ### 描述：头部刷新控件
 */
class MaterialHeaderView constructor(context: Context, attrs: AttributeSet?, defstyleAttr: Int) :
    FrameLayout(context, attrs, defstyleAttr), BaseMaterialView {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    private var materialWaveView: MaterialWaveView? = null
    private var circleProgressBar: CircleProgressBar? = null

    /**拖动的背景颜色*/
    var waveColor: Int = 0x90000000.toInt()
        set(value) {
            materialWaveView?.color = value
            field = value
        }

    /**进度框的加载条颜色组*/
    var progressColors = intArrayOf(
        0xffF44336.toInt(), 0xff4CAF50.toInt(), 0xff03A9F4.toInt(), 0xffFFEB3B.toInt()
    )
        set(value) {
            circleProgressBar?.colors = value
            field = value
        }

    /**进度框的画笔大小*/
    var progressStokeWidth = 3f
        set(value) {
            circleProgressBar?.progressStokeWidth = value
            field = value
        }

    /**进度框背景颜色*/
    var progressBg = 0xFFFAFAFA.toInt()
        set(value) {
            circleProgressBar?.progressBackGroundColor = value
            field = value
        }

    /**进度框大小(dp)*/
    var progressSize = SizeUtils.dp2px(50f)
        set(value) {
            val xy = SizeUtils.dp2px(value)
            circleProgressBar?.layoutParams = LayoutParams(xy.toInt(), xy.toInt()).apply {
                gravity = Gravity.CENTER
            }
            field = xy
        }

    /**是否显示箭头*/
    var isShowArrow = true
        set(value) {
            circleProgressBar?.isShowArrow = isShowArrow
            field = value
        }

    /**是否显示进度框背景*/
    var isShowProgressBg = true
        set(value) {
            circleProgressBar?.circleBackgroundEnabled = value
            field = value
        }

    init {
        if (!isInEditMode) {
            clipToPadding = false
            setWillNotDraw(false)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val waveView = MaterialWaveView(context)
        waveView.color = waveColor
        addView(waveView)
        materialWaveView = waveView
        val circleBar = CircleProgressBar(context)
        val layoutParams = LayoutParams(progressSize.toInt(), progressSize.toInt())
        layoutParams.gravity = Gravity.CENTER
        circleBar.layoutParams = layoutParams
        circleBar.colors = progressColors
        circleBar.progressStokeWidth = progressStokeWidth
        circleBar.isShowArrow = isShowArrow
        circleBar.circleBackgroundEnabled = isShowProgressBg
        circleBar.progressBackGroundColor = progressBg
        addView(circleBar)
        circleProgressBar = circleBar
    }

    override val view: View = this

    override fun onBegin() {
        visibility = View.VISIBLE
        materialWaveView?.onBegin()
        circleProgressBar?.apply {
            scaleX = 0.001f
            scaleY = 0.001f
            onBegin()
        }

    }

    override fun onSlide(moveX: Float, fractionY: Float) {
        materialWaveView?.onSlide(moveX, fractionY)
        circleProgressBar?.apply {
            onSlide(moveX, fractionY)
            val a = SizeUtils.limitValue(1f, fractionY)
            scaleX = a
            scaleY = a
            alpha = a
        }
    }

    override fun onRefreshing() {
        materialWaveView?.onRefreshing()
        circleProgressBar?.onRefreshing()
    }

    override fun onComlete() {
        materialWaveView?.onComlete()
        circleProgressBar?.onComlete()
        circleProgressBar?.translationY = 0f
        circleProgressBar?.scaleX = 0f
        circleProgressBar?.scaleY = 0f
    }
}