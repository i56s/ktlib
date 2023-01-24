package com.i56s.ktlib.views.swipe

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import com.i56s.ktlib.R

/**
 * @author wxr
 * @createtime 2023-01-24 10:01
 * @desc Item侧滑删除菜单控件
 */
class SwipeMenuLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        var viewCache: SwipeMenuLayout? = null
        private var isTouching = false
    }

    /**为了处理单击事件的冲突*/
    private var mScaleTouchSlop = 0

    /**计算滑动速度用*/
    private var mMaxVelocity = 0

    /**多点触摸只算第一根手指的速度*/
    private var mPointerId = 0

    /**自己的高度*/
    private var mHeight = 0

    /**右侧菜单宽度总和(最大滑动距离)*/
    private var mRightMenuWidths = 0

    /**滑动判定临界值（右侧菜单宽度的40%） 手指抬起时，超过了展开，没超过收起menu*/
    private var mLimit = 0

    /**存储contentView(第一个View)*/
    private var mContentView: View? = null

    /**上一次的xy*/
    private val mLastP = PointF()

    /**仿QQ，侧滑菜单展开时，点击除侧滑菜单之外的区域，关闭侧滑菜单*/
    private var isUnMoved = true
    private val mFirstP = PointF()
    private var isUserSwiped = false
    private var mVelocityTracker: VelocityTracker? = null

    /**平滑展开*/
    private var mExpandAnim: ValueAnimator? = null
    private var mCloseAnim: ValueAnimator? = null

    /**代表当前是否是展开状态*/
    private var isExpand = false

    /**右滑删除功能的开关,默认开*/
    var isSwipeEnable = false

    /**IOS、QQ式交互，默认开*/
    var isIos = false
    /**IOS类型下，是否拦截事件的flag*/
    private var iosInterceptFlag = false

    /**左滑右滑的开关,默认左滑打开菜单*/
    var isLeftSwipe = false

    init {
        mScaleTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMaxVelocity = ViewConfiguration.get(context).scaledMaximumFlingVelocity

        //右滑删除功能的开关,默认开
        isSwipeEnable = true
        //IOS、QQ式交互，默认开
        isIos = true
        //左滑右滑的开关,默认左滑打开菜单
        isLeftSwipe = true
        val ta = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout)
        isSwipeEnable = ta.getBoolean(R.styleable.SwipeMenuLayout_swipeEnable, true)
        isIos = ta.getBoolean(R.styleable.SwipeMenuLayout_ios, true)
        isLeftSwipe = ta.getBoolean(R.styleable.SwipeMenuLayout_leftSwipe, true)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        isClickable = true
        mRightMenuWidths = 0
        mHeight = 0
        var contentWidth = 0
        val childCount = childCount
        val measureMatchParentChildren =
            MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY
        var isNeedMeasureChildHeight = false
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            childView.isClickable = true
            if (childView.visibility != GONE) {
                //后续计划加入上滑、下滑，则将不再支持Item的margin
                measureChild(childView, widthMeasureSpec, heightMeasureSpec)
                val lp = childView.layoutParams as MarginLayoutParams
                mHeight = Math.max(mHeight, childView.measuredHeight)
                if (measureMatchParentChildren && lp.height == LayoutParams.MATCH_PARENT) {
                    isNeedMeasureChildHeight = true
                }
                if (i > 0) {//第一个布局是Left item，从第二个开始才是RightMenu
                    mRightMenuWidths += childView.measuredWidth
                } else {
                    mContentView = childView
                    contentWidth = childView.measuredWidth
                }
            }
        }
        setMeasuredDimension(
            paddingLeft + paddingRight + contentWidth, mHeight + paddingTop + paddingBottom
        )
        mLimit = mRightMenuWidths * 4 / 10//滑动判断的临界值
        if (isNeedMeasureChildHeight) {
            forceUniformHeight(childCount, widthMeasureSpec)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams =
        MarginLayoutParams(context, attrs)

    /**给MatchParent的子View设置高度*/
    private fun forceUniformHeight(count: Int, widthMeasureSpec: Int) {
        //以父布局高度构建一个Exactly的测量参数
        val uniformMeasureSpec = MeasureSpec.makeMeasureSpec(
            measuredHeight, MeasureSpec.EXACTLY
        )
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                val lp = child.layoutParams as MarginLayoutParams
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    val oldWidth = lp.width
                    lp.width = child.measuredWidth
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0)
                    lp.width = oldWidth
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = paddingLeft
        var right = paddingLeft
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView.visibility != GONE) {
                if (i == 0) {
                    //第一个子View是内容 宽度设置为全屏
                    childView.layout(
                        left,
                        paddingTop,
                        left + childView.measuredWidth,
                        paddingTop + childView.measuredHeight
                    )
                    left = left + childView.measuredWidth
                } else {
                    if (isLeftSwipe) {
                        childView.layout(
                            left,
                            paddingTop,
                            left + childView.measuredWidth,
                            paddingTop + childView.measuredHeight
                        )
                        left = left + childView.measuredWidth
                    } else {
                        childView.layout(
                            right - childView.measuredWidth,
                            paddingTop,
                            right,
                            paddingTop + childView.measuredHeight
                        )
                        right -= childView.measuredWidth
                    }
                }
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipeEnable) {
            acquireVelocityTracker(ev)
            val verTracker = mVelocityTracker
            when (ev?.action) {
                MotionEvent.ACTION_DOWN -> {
                    isUserSwiped = false
                    isUnMoved = true
                    iosInterceptFlag = false
                    if (isTouching) {
                        return false
                    } else {
                        //第一个摸的指头，赶紧改变标志，宣誓主权。
                        isTouching = true
                    }
                    mLastP.set(ev.rawX, ev.rawY)
                    mFirstP.set(ev.rawX, ev.rawY)

                    //如果down，view和cacheview不一样，则立马让它还原。且把它置为null
                    if (viewCache != null) {
                        if (viewCache != this) {
                            viewCache?.smoothClose()
                            iosInterceptFlag = isIos
                        }
                        parent.requestDisallowInterceptTouchEvent(true)
                    }
                    mPointerId = ev.getPointerId(0)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (!iosInterceptFlag) {
                        val gap = mLastP.x - ev.rawX
                        //为了在水平滑动中禁止父类ListView等再竖直滑动
                        if (Math.abs(gap) > 10 || Math.abs(scrollX) > 10) {
                            parent.requestDisallowInterceptTouchEvent(true)
                        }
                        if (Math.abs(gap) > mScaleTouchSlop) {
                            isUnMoved = false
                        }
                        scrollBy(gap.toInt(), 0)
                        if (isLeftSwipe) {//左滑
                            if (scrollX < 0) {
                                scrollTo(0, 0)
                            }
                            if (scrollX > mRightMenuWidths) {
                                scrollTo(mRightMenuWidths, 0)
                            }
                        } else {//右滑
                            if (scrollX < -mRightMenuWidths) {
                                scrollTo(-mRightMenuWidths, 0)
                            }
                            if (scrollX > 0) {
                                scrollTo(0, 0)
                            }
                        }
                        mLastP.set(ev.rawX, ev.rawY)
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (Math.abs(ev.rawX - mFirstP.x) > mScaleTouchSlop) {
                        isUserSwiped = true
                    }
                    if (!iosInterceptFlag) {
                        //且滑动了 才判断是否要收起、展开menu
                        //求伪瞬时速度
                        verTracker?.computeCurrentVelocity(1000, mMaxVelocity.toFloat())
                        val velocityX = verTracker?.getXVelocity(mPointerId) ?: 0f
                        if (Math.abs(velocityX) > 1000) {//滑动速度超过阈值
                            if (velocityX < -1000) {
                                if (isLeftSwipe) smoothExpand() else smoothClose()
                            } else {
                                if (isLeftSwipe) smoothClose() else smoothExpand()
                            }
                        } else {
                            if (Math.abs(scrollX) > mLimit) smoothExpand() else smoothClose()
                        }
                    }
                    //释放
                    releaseVelocityTracker()
                    isTouching = false //没有手指在摸我了
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (isSwipeEnable) {
            when (ev?.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (Math.abs(ev.rawX - mFirstP.x) > mScaleTouchSlop) {
                        return true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (isLeftSwipe) {
                        if (scrollX > mScaleTouchSlop) {
                            if (ev.x < width - scrollX) {
                                if (isUnMoved) smoothClose()
                                return true
                            }
                        }
                    } else {
                        if (-scrollX > mScaleTouchSlop) {
                            if (ev.x > -scrollX) {
                                if (isUnMoved) smoothClose()
                                return true
                            }
                        }
                    }
                    if (isUserSwiped) return true
                }
            }
            if (iosInterceptFlag) return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    fun smoothExpand() {
        viewCache = this
        mContentView?.isLongClickable = false
        cancelAnim()
        mExpandAnim =
            ValueAnimator.ofInt(scrollX, if (isLeftSwipe) mRightMenuWidths else -mRightMenuWidths)
        mExpandAnim?.addUpdateListener {
            scrollTo(it.animatedValue as Int, 0)
        }
        mExpandAnim?.setInterpolator(OvershootInterpolator())
        mExpandAnim?.addListener { isExpand = true }
        mExpandAnim?.setDuration(300)?.start()
    }

    /**每次执行动画之前都应该先取消之前的动画*/
    private fun cancelAnim() {
        mCloseAnim?.let {
            if (it.isRunning) it.cancel()
        }
        mExpandAnim?.let {
            if (it.isRunning) it.cancel()
        }
    }

    /**平滑关闭*/
    fun smoothClose() {
        viewCache = null
        mContentView?.isLongClickable = true
        cancelAnim()
        mCloseAnim = ValueAnimator.ofInt(scrollX, 0)
        mCloseAnim?.addUpdateListener {
            scrollTo(it.animatedValue as Int, 0)
        }
        mCloseAnim?.setInterpolator(AccelerateInterpolator())
        mCloseAnim?.addListener { isExpand = false }
        mCloseAnim?.setDuration(300)?.start()
    }

    /**向VelocityTracker添加MotionEvent*/
    private fun acquireVelocityTracker(event: MotionEvent?) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(event)
    }

    /**释放VelocityTracker*/
    private fun releaseVelocityTracker() {
        mVelocityTracker?.clear()
        mVelocityTracker?.recycle()
        mVelocityTracker = null
    }

    override fun onDetachedFromWindow() {
        if (viewCache == this) {
            viewCache?.smoothClose()
            viewCache = null
        }
        super.onDetachedFromWindow()
    }

    override fun performLongClick(): Boolean {
        if (Math.abs(scrollX) > mScaleTouchSlop) return false
        return super.performLongClick()
    }

    /**快速关闭。 用于 点击侧滑菜单上的选项,同时想让它快速关闭(删除 置顶)。 这个方法在
     * ListView里是必须调用的， 在RecyclerView里，视情况而定，如果是mAdapter.
     * notifyItemRemoved(pos)方法不用调用。*/
    fun quickClose() {
        if (viewCache == this) {
            cancelAnim()
            viewCache?.scrollTo(0, 0)
            viewCache = null
        }
    }
}