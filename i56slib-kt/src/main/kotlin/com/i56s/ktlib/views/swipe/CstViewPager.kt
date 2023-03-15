package com.i56s.ktlib.views.swipe

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * @author wxr
 * @createtime 2023-01-24 09:45
 * @desc 解决 滑动冲突的 ViewPager
 */
class CstViewPager(context: Context, attrs: AttributeSet? = null) : ViewPager(context, attrs) {
    private var mLastX = 0
    private var mLastY = 0

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var intercept = false
        ev?.let {
            val x = it.x.toInt()
            val y = it.y.toInt()
            when (it.action) {
                MotionEvent.ACTION_MOVE -> if (isHorizontalScroll(x, y)) {
                    //除了在 第一页的手指向右滑 ， 最后一页的左滑，其他时刻都是父控件需要拦截事件
                    if (isReactFirstPage() && isScrollRight(x)) {
                        intercept = false
                    } else intercept = !(isReachLastPage() && isScrollLeft(x))
                }
            }
            mLastX = x
            mLastY = y
        }
        return intercept || super.onInterceptTouchEvent(ev)
    }


    /**是否在水平滑动*/
    private fun isHorizontalScroll(x: Int, y: Int): Boolean =
        Math.abs(y - mLastY) < Math.abs(x - mLastX)

    /**是否未到达最后一页*/
    private fun isReachLastPage(): Boolean = adapter != null && adapter!!.count - 1 == currentItem

    /**是否在第一页*/
    private fun isReactFirstPage(): Boolean = currentItem == 0

    /**是否左滑*/
    private fun isScrollLeft(x: Int) = x - mLastX < 0

    /**是否右滑*/
    private fun isScrollRight(x: Int) = x - mLastX > 0
}