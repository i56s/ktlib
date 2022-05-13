package com.i56s.ktlib.views.xrefresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import com.i56s.ktlib.R
import com.i56s.ktlib.utils.LogUtils
import com.i56s.ktlib.utils.SizeUtils
import java.lang.RuntimeException
import kotlin.math.abs

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-07 15:04
 * ### 描述：下拉刷新，上拉加载 嵌套布局
 */
class MaterialRefreshLayout constructor(context: Context, attrs: AttributeSet?, defstyleAttr: Int) :
    FrameLayout(context, attrs, defstyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null)

    /**下拉控件*/
    var headerView: BaseMaterialView? = null

    /**上拉控件*/
    var footerView: BaseMaterialView? = null
    private var mChildView: View? = null
    private var mListener: MaterialRefreshListener? = null

    private val mInterpolator = DecelerateInterpolator(10f)//减速插值器

    /**是否覆盖子控件(浮在子控件上层)*/
    var isOverlay = false

    /**是否启用下拉刷新 默认启用*/
    var isRefreshEnable = false

    /**是否启用上拉加载 默认禁用*/
    var isLoadMoreEnable = false
    private var isMore = false
    private var isRefreshing = false
    private var isLoadMoreing = false

    /**拖动的最大高度*/
    private var mWaveHeight = SizeUtils.dp2px(140f)
        set(value) {
            if (value > 0) field = value
        }

    /**触发刷新的高度*/
    private var mHeadHeight = SizeUtils.dp2px(70f)
        set(value) {
            if (value > 0) field = value
        }
    private var mTouchY = 0f

    init {
        //只允许有一个子控件
        if (childCount > 1) throw RuntimeException("只允许有一个子控件")

        val t = context.obtainStyledAttributes(
            attrs,
            R.styleable.MaterialRefreshLayout,
            defstyleAttr,
            0
        )
        isOverlay = t.getBoolean(R.styleable.MaterialRefreshLayout_isOverlay, true)
        isRefreshEnable = t.getBoolean(R.styleable.MaterialRefreshLayout_refreshEnable, true)
        isLoadMoreEnable = t.getBoolean(R.styleable.MaterialRefreshLayout_loadMoreEnable, false)
        t.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        mChildView = getChildAt(0)

        //初始化刷新布局
        if (isRefreshEnable) {
            headerView = MaterialLoaderView(context).apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { gravity = Gravity.TOP }
                visibility = View.GONE
                LogUtils.d("测试", "对象1打印：${this.parent?.hashCode()}")
                this@MaterialRefreshLayout.addView(this)
            }
        }

        //初始化上拉加载
        if (isLoadMoreEnable) {
            footerView = MaterialLoaderView(context).apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { gravity = Gravity.BOTTOM }
                visibility = View.GONE
                this@MaterialRefreshLayout.addView(this)
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //刷新中-拦截事件
        if (isRefreshing || isLoadMoreing) return true
        ev?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    mTouchY = it.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val currentY = it.y
                    val dy = currentY - mTouchY
                    mTouchY = it.y
                    //如果滑动距离大于0 并且非向上滚动操作（也就是下拉）
                    if (dy > 0 && !canChildScrollUp() && isRefreshEnable) {
                        isMore = false
                        footerView?.view?.visibility = View.GONE
                        headerView?.let { base ->
                            mWaveHeight = SizeUtils.dp2px(base.slideMaxHeight())
                            mHeadHeight = SizeUtils.dp2px(base.triggerHeight())
                            base.onBegin()
                        }
                        return true
                        //上拉操作
                    } else if (dy < 0 && !canChildScrollDown() && isLoadMoreEnable) {
                        isMore = true
                        headerView?.view?.visibility = View.GONE
                        footerView?.let { base ->
                            mWaveHeight = SizeUtils.dp2px(base.slideMaxHeight())
                            mHeadHeight = SizeUtils.dp2px(base.triggerHeight())
                            base.onBegin()
                        }
                        return true
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    /** 滑动事件处理 */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isRefreshing || isLoadMoreing) return true

        event?.let {
            when (it.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (isMore) {
                        if (it.y - mTouchY > 0f) return true
                    } else {
                        if (it.y - mTouchY < 0f) return true
                    }
                    val dy = abs(it.y - mTouchY).let { y ->
                        //最大值和最小值范围控制
                        when {
                            y >= mWaveHeight * 2f -> mWaveHeight * 2f
                            y <= 0 -> 0f
                            else -> y
                        }
                    }
                    //计算滑动的高度
                    val offsetY = mInterpolator.getInterpolation(dy / mWaveHeight / 2) * dy / 2
                    //0f-1f
                    val fraction = offsetY / mHeadHeight
                    //LogUtils.d("测试", "计算后滑动的值：$fraction")

                    onMove(
                        if (isMore) footerView else if (isRefreshEnable) headerView else null,
                        it.x,
                        offsetY,
                        fraction
                    )
                    mChildView?.let { child ->
                        if (!isOverlay) child.translationY =
                            if (isMore) -offsetY else if (isRefreshEnable) offsetY else 0f
                    }
                    return@onTouchEvent true
                }
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> { //松开手指
                    onUp(if (isMore) footerView else if (isRefreshEnable) headerView else null)
                    return@onTouchEvent true
                }
                else -> return@onTouchEvent super.onTouchEvent(event)
            }
        }
        return super.onTouchEvent(event)
    }

    /** 自动刷新 */
    fun autoRefresh() {
        if (!isRefreshEnable) {
            Log.d("MaterialRefreshLayout", "下拉刷新没有开启")
            return
        }
        headerView?.let { base ->
            mWaveHeight = SizeUtils.dp2px(base.slideMaxHeight())
            mHeadHeight = SizeUtils.dp2px(base.triggerHeight())

            this@MaterialRefreshLayout.postDelayed({
                if (!isRefreshing) {
                    headerView?.onBegin()
                    headerView?.onSlide(0f, 1f)

                    if (isOverlay) {
                        base.view.layoutParams.height = mHeadHeight.toInt()
                        base.view.requestLayout()
                    } else {
                        mChildView?.let { child ->
                            createTranslationY(child, mHeadHeight, base.view)
                        }
                    }
                    refreshListener()
                }
            }, 50)
        }
    }

    /** 自动加载更多 */
    fun autoLoadMore() {
        if (!isLoadMoreEnable) {
            Log.d("MaterialRefreshLayout", "上拉加载没有开启")
            return
        }
        footerView?.let { base ->
            mWaveHeight = SizeUtils.dp2px(base.slideMaxHeight())
            mHeadHeight = SizeUtils.dp2px(base.triggerHeight())

            this@MaterialRefreshLayout.postDelayed({
                if (!isLoadMoreing) {
                    footerView?.onBegin()
                    footerView?.onSlide(0f, 1f)

                    if (isOverlay) {
                        base.view.layoutParams.height = mHeadHeight.toInt()
                        base.view.requestLayout()
                    } else {
                        mChildView?.let { child ->
                            createTranslationY(child, -mHeadHeight, base.view)
                        }
                    }
                    loadmoreListener()
                }
            }, 50)
        }
    }

    /**刷新完成*/
    fun finishRefresh() {
        post {
            resetChildView()
            headerView?.onComlete()
            isRefreshing = false
        }
    }

    /**上拉加载完成*/
    fun finishLoadMore() {
        post {
            resetChildView()
            footerView?.onComlete()
            isLoadMoreing = false
        }
    }

    /**设置刷新监听事件*/
    fun setMaterialRefreshListener(listener: MaterialRefreshListener.() -> Unit) {
        mListener = MaterialRefreshListener().apply { listener() }
    }

    /**触发下拉刷新*/
    private fun refreshListener() {
        isRefreshing = true
        headerView?.onRefreshing()
        mListener?.onRefresh?.invoke(this)
    }

    /**触发上拉加载*/
    private fun loadmoreListener() {
        isLoadMoreing = true
        footerView?.onRefreshing()
        mListener?.onLoadMore?.invoke(this)
    }

    /**创建动画*/
    private fun createTranslationY(v: View, h: Float, fl: View) {
        val compatAnimte = ViewCompat.animate(v)
        compatAnimte.duration = 250
        compatAnimte.interpolator = DecelerateInterpolator()
        compatAnimte.translationY(h)
        compatAnimte.start()
        compatAnimte.setUpdateListener {
            fl.layoutParams.height = abs(it.translationY.toInt())
            fl.requestLayout()
        }
    }

    /** 此布局的子视图是否可以向上滚动。如果子视图是自定义视图，请覆盖此选项。 */
    private fun canChildScrollUp(): Boolean {
        mChildView?.let {
            return@canChildScrollUp it.canScrollVertically(-1)
        }
        return false
    }

    /** 此布局的子视图是否可以向下滚动。如果子视图是自定义视图，请覆盖此选项。 */
    private fun canChildScrollDown(): Boolean {
        mChildView?.let {
            return@canChildScrollDown it.canScrollVertically(1)
        }
        return false
    }

    /**滑动状态*/
    private fun onMove(
        baaseMaterial: BaseMaterialView?,
        offsetX: Float,
        offsetY: Float,
        fraction: Float
    ) {
        baaseMaterial?.let {
            //修改刷新控件的高度
            it.view.layoutParams.height = offsetY.toInt()
            //更新控件
            it.view.requestLayout()
            //触发控件的滑动事件
            it.onSlide(offsetX, fraction)
        }
    }

    /**手指抬起状态*/
    private fun onUp(baaseMaterial: BaseMaterialView?) {
        //当滑动高度大于触发高度
        baaseMaterial?.view?.let {
            if (isOverlay) {
                if (it.layoutParams.height >= mHeadHeight) {
                    //触发加载事件
                    if (isMore) loadmoreListener() else refreshListener()
                    //修改刷新控件高度为触发刷新的高度
                    it.layoutParams.height = mHeadHeight.toInt()
                    it.requestLayout()
                } else {
                    //重置刷新控件的高度
                    it.layoutParams.height = 0
                    it.requestLayout()
                }
            } else {
                mChildView?.let { child ->
                    if (abs(child.translationY) >= mHeadHeight) {
                        createTranslationY(
                            child,
                            if (isMore) -mHeadHeight else mHeadHeight,
                            it
                        )
                        //触发加载事件
                        if (isMore) loadmoreListener() else refreshListener()
                    } else {
                        createTranslationY(child, 0f, it)
                    }
                }
            }
        }
    }

    /**重置子控件*/
    private fun resetChildView() {
        mChildView?.let {
            val compatAnimte = ViewCompat.animate(it)
            compatAnimte.duration = 200
            compatAnimte.y(it.translationY)
            compatAnimte.translationY(0f)
            compatAnimte.interpolator = DecelerateInterpolator()
            compatAnimte.start()
        }
    }
}