package com.i56s.ktlib.views.xrefresh

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
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

    companion object {
        /**拖动的最大高度*/
        private const val DEFAULT_WAVE_HEIGHT = 140

        /**触发刷新的高度*/
        private const val DEFAULT_HEAD_HEIGHT = 70

        /**刷新控件的高度*/
        private const val HIGHER_HEAD_HEIGHT = 100
    }

    private lateinit var mMaterialHeaderView: BaseMaterialView
    private lateinit var mMaterialFooterView: BaseMaterialView
    private lateinit var mInterpolator: DecelerateInterpolator//减速插值器

    private var mChildView: View? = null
    private var mListener: MaterialRefreshListener? = null

    private var isOverlay = false
    private var isMore = false
    private var isRefreshing = false
    private var isLoadMoreing = false
    private var isLoadMore = false
    private var isSunStyle = false //是否是太阳刷新

    /**拖动的最大高度*/
    private var mWaveHeight = DEFAULT_WAVE_HEIGHT.toFloat()

    /**触发刷新的高度*/
    private var mHeadHeight = DEFAULT_HEAD_HEIGHT.toFloat()
    private var mTouchY = 0f

    init {
        //是否是xml编辑状态
        if (!isInEditMode) {
            //只允许有一个子控件
            if (childCount > 1) throw RuntimeException("只允许有一个子控件")

            mInterpolator = DecelerateInterpolator(10f)

            val t = context.obtainStyledAttributes(
                attrs,
                R.styleable.MaterialRefreshLayout,
                defstyleAttr,
                0
            )
            isOverlay = t.getBoolean(R.styleable.MaterialRefreshLayout_overlay, false)
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
            mMaterialHeaderView.view.layoutParams = layoutParams
            mMaterialHeaderView.view.visibility = View.GONE
            addView(mMaterialHeaderView.view)
            //添加底部加载
            //mMaterialFooterView = MaterialFooterView(context)
            mMaterialFooterView = MaterialHeaderView(context)
            val layoutParams2 = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                SizeUtils.dp2px(HIGHER_HEAD_HEIGHT.toFloat()).toInt()
            )
            layoutParams2.gravity = Gravity.BOTTOM
            mMaterialFooterView.view.layoutParams = layoutParams2
            mMaterialFooterView.view.visibility = View.GONE
            addView(mMaterialFooterView.view)
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
                    //如果滑动距离大于0 并且非向上滚动操作（也就是下拉）
                    if (dy > 0 && !canChildScrollUp()) {
                        isMore = false
                        mMaterialHeaderView.view.visibility = View.VISIBLE
                        mMaterialHeaderView.onBegin()
                        return true
                        //上拉操作
                    } else if (dy < 0 && !canChildScrollDown() && isLoadMore) {
                        isMore = true
                        mMaterialFooterView.view.visibility = View.VISIBLE
                        mMaterialFooterView.onBegin()
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
                    LogUtils.d("测试", "计算后滑动的值：$fraction")

                    onMove(
                        if (isMore) mMaterialFooterView else mMaterialHeaderView,
                        it.x,
                        offsetY,
                        fraction
                    )
                    mChildView?.let { child ->
                        if (!isOverlay) child.translationY = if (isMore) -offsetY else offsetY
                    }
                    return@onTouchEvent true
                }
                MotionEvent.ACTION_CANCEL,
                MotionEvent.ACTION_UP -> { //松开手指
                    onUp(if (isMore) mMaterialFooterView else mMaterialHeaderView)
                    return@onTouchEvent true
                }
                else -> return@onTouchEvent super.onTouchEvent(event)
            }
        }
        return super.onTouchEvent(event)
    }

    /**设置是否是刷新太阳*/
    fun setSunStyle(isSunStyle: Boolean) {
        this.isSunStyle = isSunStyle
    }

    /** 自动刷新 */
    fun autoRefresh() {
        postDelayed({
            if (!isRefreshing) {
                mMaterialHeaderView.view.visibility = View.VISIBLE
                mMaterialHeaderView.onSlide(0f, 1f)

                if (isOverlay) {
                    mMaterialHeaderView.view.layoutParams.height = mHeadHeight.toInt()
                    mMaterialHeaderView.view.requestLayout()
                } else {
                    mChildView?.let {
                        createTranslationY(it, mHeadHeight, mMaterialHeaderView.view)
                    }
                }
                refreshListener()
            }
        }, 50)
    }

    /** 自动加载更多 */
    fun autoLoadMore() {
        if (!isLoadMore) throw  RuntimeException("加载更多没有开启")
        postDelayed({
            if (!isLoadMoreing) {
                mMaterialFooterView.view.visibility = View.VISIBLE
                mMaterialFooterView.onSlide(0f, 1f)

                if (isOverlay) {
                    mMaterialFooterView.view.layoutParams.height = mHeadHeight.toInt()
                    mMaterialFooterView.view.requestLayout()
                } else {
                    mChildView?.let {
                        createTranslationY(it, -mHeadHeight, mMaterialFooterView.view)
                    }
                }
                loadmoreListener()
            }
        }, 50)


    }

    /**触发下拉刷新*/
    private fun refreshListener() {
        isRefreshing = true
        mMaterialHeaderView.onRefreshing()
        mListener?.onRefresh(this)
    }

    /**触发上拉加载*/
    private fun loadmoreListener() {
        isLoadMoreing = true
        mMaterialFooterView.onRefreshing()
        mListener?.onLoadMore(this)
    }

    /** 是否允许加载更多 */
    fun setLoadMore(isLoadMore: Boolean) {
        this.isLoadMore = isLoadMore
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

    /**刷新完成*/
    fun finishRefresh() {
        post {
            resetChildView()
            mMaterialHeaderView.onComlete()
            isRefreshing = false
        }
    }

    /**上拉加载完成*/
    fun finishLoadMore() {
        post {
            resetChildView()
            mMaterialFooterView.onComlete()
            isLoadMoreing = false
        }
    }

    /**设置刷新监听事件*/
    fun setMaterialRefreshListener(listener: MaterialRefreshListener) {
        this.mListener = listener
    }

    /**滑动状态*/
    private fun onMove(
        baaseMaterial: BaseMaterialView,
        offsetX: Float,
        offsetY: Float,
        fraction: Float
    ) {
        //修改刷新控件的高度
        baaseMaterial.view.layoutParams?.height = offsetY.toInt()
        //更新控件
        baaseMaterial.view.requestLayout()
        //触发控件的滑动事件
        baaseMaterial.onSlide(offsetX, fraction)
    }

    /**手指抬起状态*/
    private fun onUp(baaseMaterial: BaseMaterialView) {
        //当滑动高度大于触发高度
        if (isOverlay) {
            if (baaseMaterial.view.layoutParams.height >= mHeadHeight) {
                //触发加载事件
                if (isMore) loadmoreListener() else refreshListener()
                //修改刷新控件高度为触发刷新的高度
                baaseMaterial.view.layoutParams.height = mHeadHeight.toInt()
                baaseMaterial.view.requestLayout()
            } else {
                //重置刷新控件的高度
                baaseMaterial.view.layoutParams.height = 0
                baaseMaterial.view.requestLayout()
            }
        } else {
            mChildView?.let { child ->
                if (abs(child.translationY) >= mHeadHeight) {
                    createTranslationY(
                        child,
                        if (isMore) -mHeadHeight else mHeadHeight,
                        baaseMaterial.view
                    )
                    //触发加载事件
                    if (isMore) loadmoreListener() else refreshListener()
                } else {
                    createTranslationY(child, 0f, baaseMaterial.view)
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