package com.i56s.ktlib.views

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.i56s.ktlib.I56sLib
import com.i56s.ktlib.R
import com.i56s.ktlib.databinding.ViewTitleBinding

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 17:04
 * ### 描述：标题控件
 */
class TitleView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private var mListener: (() -> Boolean)? = null
    private var mTitleListener: (() -> Unit)? = null
    private var mBinding = ViewTitleBinding.inflate(LayoutInflater.from(context), this, true)

    /** true=显示返回键 false=隐藏返回键 默认是显示 */
    var isShowBack = true
        set(value) {
            mBinding.titleBack.visibility = if (value) VISIBLE else GONE
            field = value
        }

    /** 返回按钮资源图片 */
    @DrawableRes
    var backImgRes = R.drawable.ic_baseline_keyboard_arrow_left_40
        set(value) {
            mBinding.titleBack.setImageResource(value)
            field = value
        }

    /** 返回按钮Drawable图片 */
    var backImgDrawable: Drawable? = null
        set(value) {
            if (value != null) mBinding.titleBack.setImageDrawable(value)
            field = value
        }

    /** 标题 */
    var title: String? = null
        get() = mBinding.titleTitle.text.toString()
        set(value) {
            mBinding.titleTitle.text = value
            field = value
        }

    /**标题文字颜色*/
    var titleTextColor = Color.WHITE
        set(value) {
            mBinding.titleTitle.setTextColor(value)
            field = value
        }

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {

        //初始化属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView)

        title = typedArray.getString(R.styleable.TitleView_tvTitle) //获取标题文字
        isShowBack = typedArray.getBoolean(R.styleable.TitleView_tvShowBack, true) //是否显示返回按钮
        val isAddStatusBarHeight =
            typedArray.getBoolean(R.styleable.TitleView_tvAddStatusBarHeight, false) //是否添加状态栏高度
        backImgDrawable = typedArray.getDrawable(R.styleable.TitleView_tvBackImg) //返回按钮图片
        titleTextColor = typedArray.getColor(
            R.styleable.TitleView_tvTextColor,
            ContextCompat.getColor(context, R.color.titleview_title) //标题文字默认颜色
        ) //背景颜色
        val bgColor = typedArray.getColor(
            R.styleable.TitleView_tvBgColor,
            ContextCompat.getColor(context, R.color.titleview_bg) //背景默认颜色
        )
        mBinding.root.setBackgroundColor(bgColor)
        typedArray.recycle()

        //设置数据
        if (isAddStatusBarHeight) { //添加状态栏高度
            setPadding(
                paddingLeft, paddingTop + I56sLib.statusBarHeight, paddingRight, paddingBottom
            )
        }

        //添加点击事件
        mBinding.titleBack.setOnClickListener {
            val isIntercept = mListener?.invoke() ?: false
            if (isIntercept) return@setOnClickListener
            (context as Activity).finish()
        }
        mBinding.titleTitle.setOnClickListener { mTitleListener?.invoke() }
    }

    /**设置返回监听器 会覆盖以前的点击事件(返回按钮事件拦截)*/
    fun setOnBackClickListener(listener: () -> Boolean) {
        this.mListener = listener
    }

    /**设置标题点击监听器*/
    fun setOnTitleClickListener(listener: () -> Unit) {
        this.mTitleListener = listener
    }
}