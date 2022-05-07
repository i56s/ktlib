package com.i56s.test

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.i56s.ktlib.utils.LogUtils

/**
 * 创建者：wxr
 * 创建时间：2022-05-07 10:09
 * 描述：
 */
class FragmentLayoutTestView constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var mHeaderView: TextView
    private var mTouchY = 0f

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mHeaderView = TextView(context)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180)
        layoutParams.topMargin = -180
        layoutParams.gravity = Gravity.TOP
        mHeaderView.layoutParams = layoutParams
        mHeaderView.setBackgroundColor(Color.RED)
        addView(mHeaderView)

        LogUtils.d(
            "测试",
            "宽度:${mHeaderView.width} 高度：${mHeaderView.height} lh=${layoutParams.height}"
        )
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return super.onInterceptTouchEvent(ev)
        //return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> mTouchY = it.y
                MotionEvent.ACTION_MOVE -> {
                    val touchY = it.y
                    val params = mHeaderView.layoutParams as LayoutParams
                    if (touchY > mTouchY) {
                        //向下拉
                        val y = touchY - mTouchY //下拉的距离
                        if (y + params.topMargin >= 0) {
                            LogUtils.d("测试", "拉到头了")
                            return super.onTouchEvent(event)
                        }
                        params.topMargin += y.toInt()
                    } else {
                        //向上滑
                        val y = mTouchY - touchY  //上滑的距离
                        if (params.topMargin - y <= -180) {
                            LogUtils.d("测试", "滑到头了")
                            return super.onTouchEvent(event)
                        }
                        params.topMargin -= y.toInt()
                    }
                    mTouchY = touchY//重新赋值
                    mHeaderView.requestLayout()
                    LogUtils.d("测试", "滑动：${it.y}")
                }
                MotionEvent.ACTION_UP -> {
                    mTouchY = it.y
                }
            }
        }
        return true
    }
}