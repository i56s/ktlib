package com.jcodecraeer.xrecyclerview

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.i56s.ktlib.R
import com.i56s.ktlib.databinding.ListviewHeaderBinding
import com.i56s.ktlib.utils.LogUtils
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView
import kotlin.math.max

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-11-26 09:14
 * ### 描述：头部刷新布局
 */
class ArrowRefreshHeader(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs),
    BaseRefreshHeader {

    constructor(context: Context) : this(context, null)

    private val cXrRefreshKey = "XR_REFRESH_KEY"
    private val cXrRefreshTimeKey = "XR_REFRESH_TIME_KEY"
    private val cRotateAnimDuration = 180L

     var state: Int = BaseRefreshHeader.STATE_NORMAL
        set(value) {
            if (value == field) return
            when (value) {
                BaseRefreshHeader.STATE_NORMAL -> { //正常状态
                    if (field == BaseRefreshHeader.STATE_RELEASE_TO_REFRESH) {
                        mBinding.listviewHeaderArrow.startAnimation(mRotateDownAnim)
                    }
                    if (field == BaseRefreshHeader.STATE_REFRESHING) {
                        mBinding.listviewHeaderArrow.clearAnimation()
                    }
                    mBinding.refreshStatusTextview.setText(R.string.listview_header_hint_normal)
                }
                BaseRefreshHeader.STATE_RELEASE_TO_REFRESH -> { // 准备刷新
                    if (field != BaseRefreshHeader.STATE_RELEASE_TO_REFRESH) {
                        mBinding.listviewHeaderArrow.clearAnimation()
                        mBinding.listviewHeaderArrow.startAnimation(mRotateUpAnim)
                        mBinding.refreshStatusTextview.setText(R.string.listview_header_hint_release)
                    }
                }
                BaseRefreshHeader.STATE_REFRESHING -> { // 刷新中
                    mBinding.listviewHeaderArrow.clearAnimation()
                    mBinding.listviewHeaderArrow.visibility = View.INVISIBLE
                    mBinding.listviewHeaderProgressbar.visibility = View.VISIBLE
                    mBinding.refreshStatusTextview.setText(R.string.refreshing)
                    smoothScrollTo(mMeasuredHeight)
                }
                BaseRefreshHeader.STATE_DONE -> { // 刷新完成
                    mBinding.lastRefreshTime.text = friendlyTime(getLastRefreshTime())
                    mBinding.refreshStatusTextview.setText(R.string.refresh_done)
                    mBinding.listviewHeaderArrow.visibility = View.INVISIBLE
                    mBinding.listviewHeaderProgressbar.visibility = View.INVISIBLE
                }
                else -> { // 显示箭头图片
                    mBinding.listviewHeaderArrow.visibility = View.VISIBLE
                    mBinding.listviewHeaderProgressbar.visibility = View.INVISIBLE
                }
            }
            field = value
        }

    /** 展示控件的高度 */
    var visibleHeight: Int = 0
        set(value) {
            val lp = mBinding.root.layoutParams
            lp.height = if (value < 0) 0 else value
            mBinding.root.layoutParams = lp
            field = value
        }

    private var progressView: AVLoadingIndicatorView
    private var mRotateUpAnim: Animation
    private var mRotateDownAnim: Animation
    private var mMeasuredHeight = 0
    private var customRefreshPsKey: String = cXrRefreshKey

    private val mBinding = ListviewHeaderBinding.inflate(LayoutInflater.from(context))

    init {
        // 初始情况，设置下拉刷新view高度为0
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 0)
        layoutParams = lp
        setPadding(0, 0, 0, 0)

        //添加刷新控件并设置高度为 0
        addView(mBinding.root, LayoutParams(LayoutParams.MATCH_PARENT, 0))
        gravity = Gravity.BOTTOM

        //init the progress view
        progressView = AVLoadingIndicatorView(context)
        progressView.setIndicatorColor(0xFFB5B5B5.toInt())
        progressView.setIndicatorId(ProgressStyle.BallSpinFadeLoader)
        mBinding.listviewHeaderProgressbar.setView(progressView)

        mRotateUpAnim = RotateAnimation(
            0f,
            -180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        mRotateUpAnim.duration = cRotateAnimDuration
        mRotateUpAnim.fillAfter = true
        mRotateDownAnim = RotateAnimation(
            -180.0f, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        mRotateDownAnim.duration = cRotateAnimDuration
        mRotateDownAnim.fillAfter = true

        //测量实际高度
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mMeasuredHeight = measuredHeight
        //LogUtils.d("测试", "测量结果$mMeasuredHeight")
    }

    fun destroy() {
        progressView.destroy()
        mRotateUpAnim.cancel()
        mRotateDownAnim.cancel()
    }

    /**设置是否显示刷新时间*/
    fun setRefreshTimeVisible(show: Boolean) {
        mBinding.headerRefreshTimeContainer.visibility = if (show) VISIBLE else GONE
    }

    /**设置刷新时间的 KEY（避免所有显示相同时间）*/
    fun setXrRefreshTimeKey(keyName: String) {
        customRefreshPsKey = keyName
    }

    fun setProgressStyle(style: Int) {
        if (style == ProgressStyle.SysProgress) {
            mBinding.listviewHeaderProgressbar.setView(
                ProgressBar(
                    context,
                    null,
                    android.R.attr.progressBarStyle
                )
            )
        } else {
            progressView = AVLoadingIndicatorView(context)
            progressView.setIndicatorColor(0xFFB5B5B5.toInt())
            progressView.setIndicatorId(style)
            mBinding.listviewHeaderProgressbar.setView(progressView)
        }
    }

    fun setArrowImageView(resId: Int) = mBinding.listviewHeaderArrow.setImageResource(resId)

    fun reset() {
        smoothScrollTo(0)
        Handler(Looper.getMainLooper()).postDelayed(
            { state = BaseRefreshHeader.STATE_NORMAL },
            500
        )
    }

    /** 计算刷新时间 */
    private fun friendlyTime(time: Long): String {
        //获取time距离当前的秒数
        val ct = ((System.currentTimeMillis() - time) / 1000).toInt()
        if (ct == 0) return "刚刚"
        if (ct in 1..59) return "${ct}秒前"
        if (ct in 60..3599) return "${max(ct / 60, 1)}分钟前"
        if (ct in 3600..86399) return "${ct / 3600}小时前"
        if (ct in 86400..2591999) return "${ct / 86400}天前"
        if (ct in 2592000..31103999) return "${ct / 2592000}月前"
        return "${ct / 31104000}年前"
    }

    /** 刷新完成主动调用 */
    override fun refreshComplete() {
        mBinding.lastRefreshTime.text = friendlyTime(getLastRefreshTime())
        saveLastRefreshTime(System.currentTimeMillis())
        state = BaseRefreshHeader.STATE_DONE
        Handler(Looper.getMainLooper()).postDelayed({ reset() }, 200L)
    }

    override fun onMove(delta: Float) {
        if (visibleHeight > 0 || delta > 0) {
            //val height = (delta + visibleHeight).toInt()
            visibleHeight += delta.toInt()
            if (state <= BaseRefreshHeader.STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                state = if (visibleHeight > mMeasuredHeight)
                    BaseRefreshHeader.STATE_RELEASE_TO_REFRESH
                else
                    BaseRefreshHeader.STATE_NORMAL
            }
        }
    }

    /** 释放刷新(动作) */
    override fun releaseAction(): Boolean {
        var isOnRefresh = false
        //滑动的高度>实际高度 并且 当前状态为 正常 或 待刷新 时变更状态为刷新中
        if (visibleHeight > mMeasuredHeight && state < BaseRefreshHeader.STATE_REFRESHING) {
            state = BaseRefreshHeader.STATE_REFRESHING
            //拉出刷新控件，完全显示控件
            smoothScrollTo(mMeasuredHeight)
            isOnRefresh = true
        } else {
            //并非刷新状态-复位
            smoothScrollTo(0)
        }
        return isOnRefresh
    }

    /**获取最后的刷新时间*/
    private fun getLastRefreshTime(): Long {
        val s = context.getSharedPreferences(customRefreshPsKey, Context.MODE_PRIVATE)
        return s.getLong(cXrRefreshTimeKey, System.currentTimeMillis())
    }

    /**保存最后的刷新时间*/
    private fun saveLastRefreshTime(refreshTime: Long) {
        val s = context.getSharedPreferences(customRefreshPsKey, Context.MODE_PRIVATE)
        s.edit().putLong(cXrRefreshTimeKey, refreshTime).apply()
    }

    private fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(visibleHeight, destHeight)
        animator.duration = 300
        //animator.start()
        animator.addUpdateListener {
            visibleHeight = it.animatedValue as Int
        }
        animator.start()
    }
}