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
import com.jcodecraeer.xrecyclerview.progressindicator.AVLoadingIndicatorView
import java.util.*
import kotlin.math.max

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-11-26 09:14
 * ### 描述：头部刷新布局
 */
class ArrowRefreshHeader(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs),
    BaseRefreshHeader {

    constructor(context: Context) : this(context, null)

    private val XR_REFRESH_KEY = "XR_REFRESH_KEY"
    private val XR_REFRESH_TIME_KEY = "XR_REFRESH_TIME_KEY"
    private val ROTATE_ANIM_DURATION = 180L

    var state: Int = BaseRefreshHeader.STATE_NORMAL
        set(value) {
            if (value == field) return
            when (value) { // 显示进度
                BaseRefreshHeader.STATE_REFRESHING -> {
                    mBinding.listviewHeaderArrow.clearAnimation()
                    mBinding.listviewHeaderArrow.visibility = View.INVISIBLE
                    mBinding.listviewHeaderProgressbar.visibility = View.VISIBLE
                    smoothScrollTo(mMeasuredHeight)
                }
                BaseRefreshHeader.STATE_DONE -> {
                    mBinding.listviewHeaderArrow.visibility = View.INVISIBLE
                    mBinding.listviewHeaderProgressbar.visibility = View.INVISIBLE
                }
                else -> { // 显示箭头图片
                    mBinding.listviewHeaderArrow.visibility = View.VISIBLE
                    mBinding.listviewHeaderProgressbar.visibility = View.INVISIBLE
                }
            }
            mBinding.lastRefreshTime.text = friendlyTime(getLastRefreshTime())
            when (value) {
                BaseRefreshHeader.STATE_NORMAL -> {
                    if (field == BaseRefreshHeader.STATE_RELEASE_TO_REFRESH) {
                        mBinding.listviewHeaderArrow.startAnimation(mRotateDownAnim)
                    }
                    if (field == BaseRefreshHeader.STATE_REFRESHING) {
                        mBinding.listviewHeaderArrow.clearAnimation()
                    }
                    mBinding.refreshStatusTextview.setText(R.string.listview_header_hint_normal)
                }
                BaseRefreshHeader.STATE_RELEASE_TO_REFRESH -> {
                    if (field != BaseRefreshHeader.STATE_RELEASE_TO_REFRESH) {
                        mBinding.listviewHeaderArrow.clearAnimation()
                        mBinding.listviewHeaderArrow.startAnimation(mRotateUpAnim)
                        mBinding.refreshStatusTextview.setText(R.string.listview_header_hint_release)
                    }
                }
                BaseRefreshHeader.STATE_REFRESHING -> mBinding.refreshStatusTextview.setText(R.string.refreshing)
                BaseRefreshHeader.STATE_DONE -> mBinding.refreshStatusTextview.setText(R.string.refresh_done)
            }
            field = value
        }


    private var progressView: AVLoadingIndicatorView
    private var mRotateUpAnim: Animation
    private var mRotateDownAnim: Animation
    private var mMeasuredHeight = 0
    private var customRefreshPsKey: String = XR_REFRESH_KEY

    private val mBinding = ListviewHeaderBinding.inflate(LayoutInflater.from(context))

    init {
        // 初始情况，设置下拉刷新view高度为0
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, 0, 0, 0)
        layoutParams = lp
        setPadding(0, 0, 0, 0)

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
        mRotateUpAnim.duration = ROTATE_ANIM_DURATION
        mRotateUpAnim.fillAfter = true
        mRotateDownAnim = RotateAnimation(
            -180.0f, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )
        mRotateDownAnim.duration = ROTATE_ANIM_DURATION
        mRotateDownAnim.fillAfter = true

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mMeasuredHeight = measuredHeight
    }

    fun destroy() {
        progressView.destroy()
        mRotateUpAnim.cancel()
        mRotateDownAnim.cancel()
    }

    fun setRefreshTimeVisible(show: Boolean) {
        mBinding.headerRefreshTimeContainer.visibility = if (show) VISIBLE else GONE
    }

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

    fun setVisibleHeight(height: Int) {
        val lp = mBinding.root.layoutParams
        lp.height = if (height < 0) 0 else height
        mBinding.root.layoutParams = lp
    }

    fun getVisibleHeight(): Int = mBinding.root.layoutParams.height

    fun reset() {
        smoothScrollTo(0)
        Handler(Looper.getMainLooper()).postDelayed(
            { state = BaseRefreshHeader.STATE_NORMAL },
            500
        )
    }

    fun friendlyTime(time: Date): String = friendlyTime(time.time)

    fun friendlyTime(time: Long): String {
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

    override fun refreshComplete() {
        mBinding.lastRefreshTime.text = friendlyTime(getLastRefreshTime())
        saveLastRefreshTime(System.currentTimeMillis())
        state = BaseRefreshHeader.STATE_DONE
        Handler(Looper.getMainLooper()).postDelayed({ reset() }, 200L)
    }

    override fun onMove(delta: Float) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((delta + getVisibleHeight()).toInt())
            if (state <= BaseRefreshHeader.STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    state = BaseRefreshHeader.STATE_RELEASE_TO_REFRESH
                } else {
                    state = BaseRefreshHeader.STATE_NORMAL
                }
            }
        }
    }

    override fun releaseAction(): Boolean {
        var isOnRefresh = false
        val height = getVisibleHeight()
        if (getVisibleHeight() > mMeasuredHeight && state < BaseRefreshHeader.STATE_REFRESHING) {
            state = BaseRefreshHeader.STATE_REFRESHING
            isOnRefresh = true
        }
        // refreshing and header isn't shown fully. do nothing.
        if (state == BaseRefreshHeader.STATE_REFRESHING && height <= mMeasuredHeight) {
            //return
        }
        if (state != BaseRefreshHeader.STATE_REFRESHING) smoothScrollTo(0)
        if (state == BaseRefreshHeader.STATE_REFRESHING) smoothScrollTo(mMeasuredHeight)
        return isOnRefresh
    }

    private fun getLastRefreshTime(): Long {
        val s = context.getSharedPreferences(customRefreshPsKey, Context.MODE_PRIVATE)
        return s.getLong(XR_REFRESH_TIME_KEY, System.currentTimeMillis())
    }

    private fun saveLastRefreshTime(refreshTime: Long) {
        val s = context.getSharedPreferences(customRefreshPsKey, Context.MODE_PRIVATE)
        s.edit().putLong(XR_REFRESH_TIME_KEY, refreshTime).apply()
    }

    private fun smoothScrollTo(destHeight: Int) {
        val animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight)
        animator.duration = 300
        //animator.start()
        animator.addUpdateListener {
            setVisibleHeight(it.animatedValue as Int)
        }
        animator.start()
    }
}