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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.I56sLib
import com.i56s.ktlib.base.LibBaseActivity
import com.i56s.ktlib.base.LibBaseDialog
import com.i56s.ktlib.views.LoadingSwordView
import com.i56s.ktlib.views.LoadingView

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:59
 * ### 描述：加载弹框，有两种形状- TYPE_DEFAULT 和 TYPE_SWORD
 */
class LoadingDialog @JvmOverloads constructor(private var type: LoadingType = LoadingType.TYPE_DEFAULT) :
    LibBaseDialog<ViewBinding>() {

    enum class LoadingType {
        /**默认加载框*/
        TYPE_DEFAULT,

        /**剑气加载*/
        TYPE_SWORD
    }

    /**是否显示遮罩层*/
    private var isShowMask = true

    /**加载视图*/
    private var loadingView: View? = null

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val linView = LinearLayout(inflater.context)
        linView.gravity = Gravity.CENTER
        linView.orientation = LinearLayout.VERTICAL

        linView.addView(
            if (loadingView != null) loadingView else
                when (type) {
                    LoadingType.TYPE_SWORD -> LoadingSwordView(inflater.context)
                    else -> LoadingView(inflater.context)
                }
        )
        return linView
    }

    override fun getViewBinding(container: ViewGroup?): ViewBinding = ViewBinding { TextView(null) }

    override fun setDialogProperties(dialog: Dialog?) {
        if (!isShowMask) {
            dialog?.window?.setDimAmount(0f)
        }
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun initData() {}

    override fun initEvent() {}

    override fun show() {
        if (!isShowing()) super.show()
    }

    /**设置是否显示遮罩层*/
    fun setShowMask(isShowMask: Boolean): LoadingDialog {
        this.isShowMask = isShowMask
        return this
    }

    /**设置加载视图*/
    fun setLoadingView(view: View): LoadingDialog {
        loadingView = view
        return this
    }

    /**显示加载框*/
    fun show(type: LoadingType = this.type) {
        this.type = type
        this.show()
    }
}