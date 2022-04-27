package com.i56s.ktlib.base

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:46
 * ### 描述：activity的基类
 */
abstract class LibBaseActivity<T : ViewBinding> : AppCompatActivity() {

    /**上下文成员变量*/
    protected lateinit var mContext: Context

    /**activity成员变量*/
    protected lateinit var mActivity: LibBaseActivity<T>

    companion object {
        /**屏幕宽度*/
        var screenX = 0
            private set

        /**屏幕高度*/
        var screenY = 0
            private set
    }

    protected lateinit var mBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isOverstepStatusBar()) setBackGroundColor()
        this.mContext = this
        this.mActivity = this
        if (isRemoveStatusBar()) { //去掉状态栏
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        if (screenX == 0 || screenY == 0) { //获取屏幕宽高
            val point = Point()
            windowManager.defaultDisplay.getSize(point)
            screenX = point.x
            screenY = point.y
        }
        this.onCreateBefore()
        super.onCreate(savedInstanceState)
        this.onCreateAfter()
        mBinding = this.getViewBinding()
        this.setContentView(mBinding.root)
        this.initCreate()
        this.initEvent()
    }

    /**设置状态栏颜色跟 title 颜色一样*/
    private fun setBackGroundColor() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    /**是否去除状态栏true=去除，false=显示*/
    abstract fun isRemoveStatusBar(): Boolean

    /**布局是否超出状态栏 true=超出，false=不超出*/
    abstract fun isOverstepStatusBar(): Boolean

    /**在onCreate之前执行*/
    abstract fun onCreateBefore()

    /**在onCreate之后执行*/
    abstract fun onCreateAfter()

    /** 获取视图绑定对象 */
    abstract fun getViewBinding(): T

    /**activity创建时调用*/
    abstract fun initCreate()

    /**初始化事件监听*/
    abstract fun initEvent()
}