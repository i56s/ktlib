package com.i56s.ktlib.dialog

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.i56s.ktlib.base.LibBaseDialog
import com.i56s.ktlib.databinding.DialogConfirmBinding
import com.i56s.ktlib.utils.HtmlUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：6/9/21 11:35 AM
 * ### 描述：确认弹框
 */
class ConfirmDialog : LibBaseDialog<DialogConfirmBinding>() {

    /**设置参数选项*/
    var option: Options = Options()

    private var mCancelListener: (() -> Unit)? = null
    private var mOkListener: (() -> Unit)? = null

    /**标记对象*/
    var tagObj: Any? = null

    override fun getViewBinding(container: ViewGroup?): DialogConfirmBinding =
        DialogConfirmBinding.inflate(layoutInflater)

    override fun initData() {
        mBinding.confirmTitle.text = option.title
        mBinding.confirmContent.setTextColor(option.contentTextColor)
        mBinding.confirmContent.gravity = option.contentGravity
        mBinding.confirmContent.text = HtmlUtils.fromHtml(option.content)

        if (TextUtils.isEmpty(option.cancelText)) {
            mBinding.confirmCancel.visibility = View.GONE
            mBinding.line6.visibility = View.GONE
        } else {
            mBinding.confirmCancel.text = option.cancelText
            mBinding.confirmCancel.setTextColor(option.cancelTextColor)
        }

        if (TextUtils.isEmpty(option.okText)) {
            mBinding.confirmOk.visibility = View.GONE
            mBinding.line6.visibility = View.GONE
        } else {
            mBinding.confirmOk.text = option.okText
            mBinding.confirmOk.setTextColor(option.okTextColor)
        }
    }

    override fun initEvent() {
        mBinding.confirmCancel.setOnClickListener {
            if (option.isClickBtnDismiss) dismiss()
            mCancelListener?.invoke()
        }
        mBinding.confirmOk.setOnClickListener {
            if (option.isClickBtnDismiss) dismiss()
            mOkListener?.invoke()
        }
    }

    /**设置取消按钮点击监听器*/
    fun setOnCancelClickListener(listener: () -> Unit) {
        mCancelListener = listener
    }

    /**设置确定按钮点击监听器*/
    fun setOnOkClickListener(listener: () -> Unit) {
        mOkListener = listener
    }

    /**
     * 配置参数
     * @property title 标题
     * @property content 内容文本
     * @property contentTextColor 内容文本颜色
     * @property contentGravity 内容文本位置
     * @property cancelText 左边按钮文本
     * @property cancelTextColor 左边按钮文本颜色
     * @property okText 右边按钮文本
     * @property okTextColor 右边按钮文本颜色
     * @property isClickBtnDismiss 点击按钮是否关闭弹框
     */
    data class Options(
        var title: String = "提示", var content: String = "内容",
        var contentTextColor: Int = 0xFF999999.toInt(), var contentGravity: Int = Gravity.CENTER,
        var cancelText: String? = "取消", var cancelTextColor: Int = 0xFF333333.toInt(),
        var okText: String? = "确定", var okTextColor: Int = 0xFF5395fe.toInt(),
        var isClickBtnDismiss: Boolean = true
    )
}