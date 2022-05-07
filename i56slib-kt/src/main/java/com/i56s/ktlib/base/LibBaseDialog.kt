package com.i56s.ktlib.base

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.LibDialogFragment
import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.R
import com.i56s.ktlib.utils.LogUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:47
 * ### 描述：dialog基类
 */
abstract class LibBaseDialog<T : ViewBinding> : LibDialogFragment() {

    private val cTag = "LibDialog基类"

    /**数据开始加载的时间*/
    private var mLoadTime: Long = 0

    /**数据刷新时间*/
    private var mFragmentLoadDataTime: Long = 5 * 60 * 1000
    private var mDismissListener: ((dialog: DialogInterface) -> Unit)? = null
    private var mBackDismissListener: (() -> Unit)? =
        null
    var isCancelOutSide = true

    protected lateinit var mBinding: T

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireActivity(), R.style.Dialog)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = getViewBinding(container)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initData()
        this.initEvent()
        this.loadData()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setCanceledOnTouchOutside(isCancelOutSide)

        val windowWidth = dialog?.window?.windowManager?.defaultDisplay?.width
        val windowHeight = dialog?.window?.windowManager?.defaultDisplay?.height
        val width = getDialogWidth()
        val height = getDialogHeight()
        //设置宽度
        if (width != 0f && windowWidth != null) {
            dialog?.window?.attributes?.width = (windowWidth * width).toInt()
        } else {
            dialog?.window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        //设置高度
        if (height != 0f && windowHeight != null) {
            dialog?.window?.attributes?.height = (windowHeight * height).toInt()
        }
        dialog?.window?.attributes?.gravity = Gravity.CENTER
        dialog?.setOnKeyListener { _, keyCode, event ->
            //点击返回键关闭
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                mBackDismissListener?.invoke()
            }
            false
        }
        this.setDialogProperties(dialog)
    }

    override fun onStart() {
        super.onStart()
        this.loadData()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        this.onHiddenChanged(!userVisibleHint)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (context == null) return
            //再次刷新
            this.loadData()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mDismissListener?.invoke(dialog)
    }

    /**加载数据*/
    private fun loadData() {
        if (System.currentTimeMillis() - mLoadTime > mFragmentLoadDataTime) {
            LogUtils.e(cTag, "定时加载数据${javaClass.name}")
            this.refreshLoadTime()
            this.loadDataOfTimer()
        }
    }

    /**刷新加载数据的时间*/
    fun refreshLoadTime() {
        mLoadTime = System.currentTimeMillis()
    }

    /**主线程运行*/
    fun runOnUiThread(run: Runnable) = activity?.runOnUiThread(run)

    /**销毁页面*/
    fun finish() = activity?.finish()

    /**定时加载数据 当fragment可见时调用该方法*/
    open fun loadDataOfTimer() {}

    /**用于设置dialog的属性*/
    open fun setDialogProperties(dialog: Dialog?) {}

    /**设置数据刷新时间*/
    fun setFragmentLoadDataTime(fragmentLoadDataTime: Long) {
        mFragmentLoadDataTime = fragmentLoadDataTime
    }

    /**获取dialog宽度
     * @return 屏幕的百分比 0=自适应*/
    open fun getDialogWidth(): Float = 0.9f

    /**获取dialog高度
     * @return 屏幕的百分比 0=自适应*/
    open fun getDialogHeight(): Float = 0f

    /**@return dialog是否显示*/
    fun isShowing(): Boolean = if (dialog != null) dialog?.isShowing!! else false

    /**显示dialog*/
    open fun show(manager: FragmentManager) = super.show(manager, hashCode().toString())

    /**设置弹框关闭监听器*/
    fun setOnDismissListener(listener: (dialog: DialogInterface) -> Unit) {
        mDismissListener = listener
    }

    /**设置点击返回键关闭监听*/
    fun setOnBackDismissListener(listener: () -> Unit) {
        mBackDismissListener = listener
    }

    /** 获取视图绑定对象 */
    abstract fun getViewBinding(container: ViewGroup?): T

    /**初始化数据*/
    abstract fun initData()

    /**初始化事件监听*/
    abstract fun initEvent()
}