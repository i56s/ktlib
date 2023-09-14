package com.i56s.ktlib.views

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
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
    var backDrawable: Drawable? = null
        set(value) {
            mBinding.titleBack.setImageDrawable(value)
            field = value
        }

    /** 标题 */
    var text: CharSequence? = null
        get() = mBinding.titleTitle.text
        set(value) {
            mBinding.titleTitle.text = value
            field = value
        }

    /**标题文字大小*/
    var textSize: Float = 0F
        set(value) {
            mBinding.titleTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            field = value
        }

    /**标题文字颜色*/
    @ColorInt
    var textColor: Int = 0
        set(value) {
            mBinding.titleTitle.setTextColor(value)
            field = value
        }

    init {

        //初始化属性
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.TitleView, 0, R.style.TitleView)

        //获取标题文字
        text = typedArray.getText(R.styleable.TitleView_android_text)
        //标题文字大小
        textSize = typedArray.getDimension(R.styleable.TitleView_android_textSize, 0f)
        //标题文字默认颜色
        textColor = typedArray.getColor(R.styleable.TitleView_android_textColor, 0)
        //是否显示返回按钮
        isShowBack = typedArray.getBoolean(R.styleable.TitleView_showBack, true)
        //是否添加状态栏高度
        val isAddStatusBarHeight =
            typedArray.getBoolean(R.styleable.TitleView_addStatusBarHeight, false)
        //返回按钮图片
        backDrawable = typedArray.getDrawable(R.styleable.TitleView_backDrawable)
        //标题高度
        mBinding.root.layoutParams.height =
            typedArray.getDimensionPixelSize(R.styleable.TitleView_android_height, 0)
        //背景
        background = typedArray.getDrawable(R.styleable.TitleView_android_background)

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