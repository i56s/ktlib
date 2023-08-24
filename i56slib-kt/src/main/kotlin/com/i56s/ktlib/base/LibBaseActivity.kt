package com.i56s.ktlib.base

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:46
 * ### 描述：activity的基类
 */
abstract class LibBaseActivity<VB : ViewBinding, VM : LibBaseViewModel> : AppCompatActivity() {

    /**上下文成员变量*/
    protected val mContext: Context by lazy { this }

    /**activity成员变量*/
    protected val mActivity: LibBaseActivity<VB, VM> by lazy { this }

    protected val mBinding: VB by lazy { this.getViewBinding() }
    protected val mModel: VM? by lazy {
        getViewModel()?.let {
            ViewModelProvider(this).get(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isOverstepStatusBar()) setBackGroundColor()
        if (isRemoveStatusBar()) { //去掉状态栏
            window.setFlags(
                //API 30以上被弃用
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        this.onCreateBefore()
        super.onCreate(savedInstanceState)
        this.onCreateAfter()

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

    /** 在onCreate之前执行 */
    abstract fun onCreateBefore()

    /** 在onCreate之后执行 */
    abstract fun onCreateAfter()

    /** 获取视图绑定对象 */
    abstract fun getViewBinding(): VB

    /** 获取ViewModel类对象 */
    abstract fun getViewModel(): Class<VM>?

    /** activity创建时调用 */
    abstract fun initCreate()

    /** 初始化事件监听 */
    abstract fun initEvent()
}