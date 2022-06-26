package com.i56s.ktlib.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.base.LibBaseDialog
import com.i56s.ktlib.views.LoadingSwordView
import com.i56s.ktlib.views.LoadingView

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:59
 * ### 描述：加载弹框，有两种形状- TYPE_DEFAULT 和 TYPE_SWORD
 */
class LoadingDialog @JvmOverloads constructor(type: LoadingType = LoadingType.TYPE_DEFAULT) :
    LibBaseDialog<ViewBinding>() {

    enum class LoadingType {
        /**默认加载框*/
        TYPE_DEFAULT,

        /**剑气加载*/
        TYPE_SWORD
    }

    var loadingText = "加载中"
    private var mPointCount = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private var mType = LoadingType.TYPE_DEFAULT
    private var mListener: OnLoadingDismissListener? = null

    init {
        mType = type
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val linView = LinearLayout(inflater.context)
        linView.gravity = Gravity.CENTER
        linView.orientation = LinearLayout.VERTICAL

        val loadingView = when (mType) {
            LoadingType.TYPE_SWORD -> LoadingSwordView(inflater.context)
            else -> LoadingView(inflater.context)
        }

        linView.addView(loadingView)

        val loadText = TextView(inflater.context)
        loadText.text = loadingText

        mHandler.postDelayed(object : Runnable {
            override fun run() {
                mPointCount++
                when (mPointCount) {
                    1 -> loadText.text = "$loadingText."
                    2 -> loadText.text = "$loadingText.."
                    3 -> {
                        loadText.text = "$loadingText..."
                        mPointCount = 0
                    }
                }
                mHandler.postDelayed(this, 600)
            }
        }, 600)

        loadText.setTextColor(Color.WHITE)
        loadText.textSize = 15f

        val layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.topMargin = 10
        loadText.layoutParams = layoutParams

        linView.addView(loadText)
        return linView
    }

    override fun getViewBinding(container: ViewGroup?): ViewBinding = ViewBinding { TextView(null) }

    override fun setDialogProperties(dialog: Dialog?) {
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onDestroyView() {
        mHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mListener?.onDismiss(dialog)
    }

    override fun initData() {}

    override fun initEvent() {}

    override fun show(manager: FragmentManager) {
        if (!isShowing()) super.show(manager)
    }

    /**显示加载框*/
    fun show(loadText: String, manager: FragmentManager) {
        loadingText = loadText
        this.show(manager)
    }

    /**设置窗口关闭事件监听器*/
    fun setOnLoadingDismissListener(listener: OnLoadingDismissListener?) {
        mListener = listener; }

    /**窗口关闭事件监听器*/
    interface OnLoadingDismissListener {

        /**窗口关闭回调*/
        fun onDismiss(dialog: DialogInterface)
    }
}