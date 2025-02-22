package com.i56s.ktlib.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:58
 * ### 描述：增加定时加载数据方法
 */
abstract class LibBaseFragment<T : ViewBinding> : Fragment() {

    /**视图对象*/
    protected val mBinding: T by lazy { getViewBinding(layoutInflater) }
    protected val mContext: Context by lazy { requireContext() }
    protected val mActivity: Activity by lazy { requireActivity() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding
        this.initData()
        this.initEvent()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    /**主线程运行*/
    fun runOnUiThread(run: Runnable) = activity?.runOnUiThread(run)

    /**销毁页面*/
    fun finish() = activity?.finish()

    /** 获取视图绑定对象 */
    abstract fun getViewBinding(layoutInflater: LayoutInflater): T

    /**初始化数据*/
    abstract fun initData()

    /**初始化事件监听*/
    abstract fun initEvent()
}