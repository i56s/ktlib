package com.i56s.ktlib.views

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
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
class TitleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

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
    var backImgRes = R.drawable.ic_title_view_back
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

    init {

        //初始化属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleView)

        //获取标题文字
        title = typedArray.getString(R.styleable.TitleView_tvTitle)
        //是否显示返回按钮
        isShowBack = typedArray.getBoolean(R.styleable.TitleView_tvShowBack, true)
        //是否添加状态栏高度
        val isAddStatusBarHeight =
            typedArray.getBoolean(R.styleable.TitleView_tvAddStatusBarHeight, false)
        //返回按钮图片
        backImgDrawable = typedArray.getDrawable(R.styleable.TitleView_tvBackImg)
        //标题文字大小
        mBinding.titleTitle.setTextSize(
            TypedValue.COMPLEX_UNIT_PX, typedArray.getDimension(
                R.styleable.TitleView_tvTitleSize,
                context.resources.getDimension(R.dimen.titleview_title_size)
            )
        )
        //标题高度
        mBinding.root.layoutParams.height = typedArray.getDimensionPixelSize(
            R.styleable.TitleView_tvHeight,
            context.resources.getDimensionPixelSize(R.dimen.titleview_height)
        )
        //标题文字默认颜色
        titleTextColor = typedArray.getColor(
            R.styleable.TitleView_tvTextColor,
            ContextCompat.getColor(context, R.color.titleview_title)
        )
        //背景默认颜色
        val bgColor = typedArray.getColor(
            R.styleable.TitleView_tvBgColor, ContextCompat.getColor(context, R.color.titleview_bg)
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