package com.i56s.ktlib.views.xrefresh

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.i56s.ktlib.R
import com.i56s.ktlib.utils.SizeUtils
import java.lang.RuntimeException
import kotlin.math.max
import kotlin.math.min

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-07 15:04
 * ### 描述：下拉刷新，上拉加载 嵌套布局
 */
class MaterialRefreshLayout constructor(context: Context, attrs: AttributeSet?, defstyleAttr: Int) :
    FrameLayout(context, attrs, defstyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    companion object {
        private const val DEFAULT_WAVE_HEIGHT = 140
        private const val HIGHER_WAVE_HEIGHT = 180
        private const val DEFAULT_HEAD_HEIGHT = 70
        private const val HIGHER_HEAD_HEIGHT = 100
    }

    private var mMaterialHeaderView: BaseMaterialView? = null
    private var mMaterialFooterView: BaseMaterialView? = null
    private var mChildView: View? = null
    private var decelerateInterpolator: DecelerateInterpolator? = null//动画减速器
    private var refreshListener: MaterialRefreshListener? = null

    private var isOverlay = false
    private var isRefreshing = false
    private var isLoadMoreing = false
    private var isLoadMore = false
    private var isSunStyle = false
    private var mWaveHeight = 0f
    private var mHeadHeight = 0f
    private var mTouchY = 0f
    private var mCurrentY = 0f

    init {
        //是否是xml编辑状态
        if (!isInEditMode) {
            //只允许有一个子控件
            if (childCount > 1) throw RuntimeException("只允许有一个子控件")

            decelerateInterpolator = DecelerateInterpolator(10f)

            val t = context.obtainStyledAttributes(
                attrs,
                R.styleable.MaterialRefreshLayout,
                defstyleAttr,
                0
            )
            isOverlay = t.getBoolean(R.styleable.MaterialRefreshLayout_overlay, false)
            mHeadHeight = DEFAULT_HEAD_HEIGHT.toFloat()
            mWaveHeight = DEFAULT_WAVE_HEIGHT.toFloat()
            MaterialWaveView.DefaulHeadHeight = DEFAULT_HEAD_HEIGHT
            MaterialWaveView.DefaulWaveHeight = DEFAULT_WAVE_HEIGHT
            isLoadMore = t.getBoolean(R.styleable.MaterialRefreshLayout_isLoadMore, false)
            t.recycle()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mChildView = getChildAt(0)
        mChildView?.let {
            mWaveHeight = SizeUtils.dp2px(mWaveHeight)
            mHeadHeight = SizeUtils.dp2px(mHeadHeight)

            if (isSunStyle) {
                //添加刷新太阳
                mMaterialHeaderView = SunLayout(context)
            } else {
                //添加刷新圆圈
                mMaterialHeaderView = MaterialHeaderView(context)
            }
            val layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                SizeUtils.dp2px(HIGHER_HEAD_HEIGHT.toFloat()).toInt()
            )
            layoutParams.gravity = Gravity.TOP
            mMaterialHeaderView?.getView()?.layoutParams = layoutParams
            mMaterialHeaderView?.getView()?.visibility = View.GONE
            setHeaderView(mMaterialHeaderView!!.getView())
            //添加底部加载
            mMaterialFooterView = MaterialFooterView(context)
            val layoutParams2 = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                SizeUtils.dp2px(HIGHER_HEAD_HEIGHT.toFloat()).toInt()
            )
            layoutParams2.gravity = Gravity.BOTTOM
            mMaterialFooterView?.getView()?.layoutParams = layoutParams2
            mMaterialFooterView?.getView()?.visibility = View.GONE
            setFooderView(mMaterialFooterView!!.getView())
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //刷新中-拦截事件
        if (isRefreshing) return true
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchY = it.y
                    mCurrentY = mTouchY
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentY = it.y
                    val dy = currentY - mTouchY
                    //如果滑动距离大于0 并且非向上滚动操作（也就是下拉）
                    if (dy > 0 && !canChildScrollUp()) {
                        if (mMaterialHeaderView != null) {
                            mMaterialHeaderView?.getView()?.visibility = View.VISIBLE
                            mMaterialHeaderView?.onBegin()
                        }
                        return true
                    } else if (dy < 0 && !canChildScrollDown() && isLoadMore) {
                        if (mMaterialFooterView != null && !isLoadMoreing) {
                            soveLoadMoreLogic()
                        }
                        return super.onInterceptTouchEvent(it)
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    /** 触发加载更多 */
    private fun soveLoadMoreLogic() {
        isLoadMoreing = true
        mMaterialFooterView?.getView()?.visibility = View.VISIBLE
        mMaterialFooterView?.onBegin()
        mMaterialFooterView?.onRefreshing()
        refreshListener?.onRefreshLoadMore(this)
    }

    /** 滑动事件处理 */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isRefreshing) return super.onTouchEvent(event)

        event?.let {
            when (it.action) {
                MotionEvent.ACTION_MOVE -> {
                    mCurrentY = it.y
                    var dy = mCurrentY - mTouchY
                    dy = min(mWaveHeight * 2, dy)
                    dy = max(0f, dy)
                    if (mChildView != null) {
                        val offsetY =
                            decelerateInterpolator!!.getInterpolation(dy / mWaveHeight / 2) * dy / 2
                        val fraction = offsetY / mHeadHeight
                        if (mMaterialHeaderView != null) {
                            mMaterialHeaderView?.getView()?.layoutParams?.height = offsetY.toInt()
                            //更新控件
                            mMaterialHeaderView?.getView()?.requestLayout()
                            mMaterialHeaderView?.onSlide(fraction)
                        }
                        if (!isOverlay) ViewCompat.setTranslationY(mChildView, offsetY)
                    }
                    return@onTouchEvent true
                }
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> {
                    if (mChildView != null) {
                        mMaterialHeaderView?.let { v ->
                            if (isOverlay) {
                                if (v.getView().layoutParams.height > mHeadHeight) {

                                    updateListener()

                                    v.getView().layoutParams.height =
                                        mHeadHeight.toInt()
                                    v.getView().requestLayout()

                                } else {
                                    v.getView().layoutParams.height = 0
                                    v.getView().requestLayout()
                                }

                            } else {
                                if (ViewCompat.getTranslationY(mChildView) >= mHeadHeight) {
                                    createAnimatorTranslationY(
                                        mChildView!!, mHeadHeight,
                                        v.getView()
                                    )
                                    updateListener()
                                } else {
                                    createAnimatorTranslationY(mChildView!!, 0f, v.getView())
                                }
                            }
                        }
                    }
                    return@onTouchEvent true
                }
                else -> return@onTouchEvent super.onTouchEvent(event)
            }
        }

        return super.onTouchEvent(event)
    }

    fun setSunStyle(isSunStyle: Boolean) {
        this.isSunStyle = isSunStyle
    }

    /** 自动刷新 */
    fun autoRefresh() {
        postDelayed({
            if (!isRefreshing) {
                mMaterialHeaderView?.let {
                    it.getView().visibility = View.VISIBLE

                    if (isOverlay) {
                        it.getView().layoutParams.height = mHeadHeight.toInt()
                        it.getView().requestLayout()
                    } else {
                        createAnimatorTranslationY(
                            mChildView!!,
                            mHeadHeight,
                            it.getView()
                        )
                    }
                }
                updateListener()
            }
        }, 50)
    }

    /** 自动加载更多 */
    fun autoRefreshLoadMore() {
        this.post {
            if (isLoadMore) {
                soveLoadMoreLogic()
            } else {
                throw  RuntimeException("加载更多没有开启")
            }
        }
    }

    fun updateListener() {
        isRefreshing = true
        mMaterialHeaderView?.onRefreshing()
        refreshListener?.onRefresh(this)
    }

    /** 是否允许加载更多 */
    fun setLoadMore(isLoadMore: Boolean) {
        this.isLoadMore = isLoadMore
    }

    fun createAnimatorTranslationY(v: View, h: Float, fl: View) {
        val viewPropertyAnimatorCompat = ViewCompat.animate(v)
        viewPropertyAnimatorCompat.duration = 250
        viewPropertyAnimatorCompat.interpolator = DecelerateInterpolator()
        viewPropertyAnimatorCompat.translationY(h)
        viewPropertyAnimatorCompat.start()
        viewPropertyAnimatorCompat.setUpdateListener {
            val height = ViewCompat.getTranslationY(v)
            fl.layoutParams.height = height.toInt()
            fl.requestLayout()
        }
    }

    /** 此布局的子视图是否可以向上滚动。如果子视图是自定义视图，请覆盖此选项。 */
    fun canChildScrollUp(): Boolean {
        mChildView?.let {
            return ViewCompat.canScrollVertically(it, -1)
        }
        return false
    }

    /** 此布局的子视图是否可以向下滚动。如果子视图是自定义视图，请覆盖此选项。 */
    fun canChildScrollDown(): Boolean {
        mChildView?.let {
            return ViewCompat.canScrollVertically(it, 1)
        }
        return false
    }

    fun setWaveHigher() {
        mHeadHeight = HIGHER_HEAD_HEIGHT.toFloat()
        mWaveHeight = HIGHER_WAVE_HEIGHT.toFloat()
        MaterialWaveView.DefaulHeadHeight = HIGHER_HEAD_HEIGHT
        MaterialWaveView.DefaulWaveHeight = HIGHER_WAVE_HEIGHT
    }

    fun finishRefreshing() {
        mChildView?.let {
            val viewPropertyAnimatorCompat = ViewCompat.animate(it)
            viewPropertyAnimatorCompat.duration = 200
            viewPropertyAnimatorCompat.y(ViewCompat.getTranslationY(mChildView))
            viewPropertyAnimatorCompat.translationY(0f)
            viewPropertyAnimatorCompat.interpolator = DecelerateInterpolator()
            viewPropertyAnimatorCompat.start()

            mMaterialHeaderView?.onComlete()
            refreshListener?.onfinish()
        }
        isRefreshing = false
    }

    fun finishRefresh() {
        post {
            finishRefreshing()
        }
    }

    fun finishRefreshLoadMore() {
        post {
            if (mMaterialFooterView != null && isLoadMoreing) {
                isLoadMoreing = false
                mMaterialFooterView?.onComlete()
            }
        }
    }

    private fun setHeaderView(headerView: View) = addView(headerView)

    private fun setFooderView(fooderView: View) = addView(fooderView)

    fun setMaterialRefreshListener(refreshListener: MaterialRefreshListener) {
        this.refreshListener = refreshListener
    }
}