package com.i56s.ktlib.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.i56s.ktlib.R
import com.i56s.ktlib.databinding.ViewSettingItemBinding
import com.i56s.ktlib.utils.SizeUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：2025-02-05 11:14
 * ### 描述：设置条纹栏
 */
class SettingItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val mBinding = ViewSettingItemBinding.inflate(LayoutInflater.from(context), this, true)

    /**名称（左边文本）*/
    var name: CharSequence?
        set(value) {
            mBinding.barName.text = value
        }
        get() = mBinding.barName.text

    /**名称（左边文本）颜色*/
    @ColorInt
    var nameColor: Int = 0
        set(value) {
            mBinding.barName.setTextColor(value)
            field = value
        }

    /**名称（左边文本）大小，单位px*/
    var nameSize: Float = 0f
        set(value) {
            mBinding.barName.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            field = value
        }

    /**名称（左边文本）边距*/
    var nameMargin: Int = 0
        set(value) {
            val param = mBinding.barName.layoutParams
            if (param is LayoutParams) {
                param.setMargins(value, value, value, value)
            }
            field = value
        }

    /**名称（左边文本）边距-左*/
    var nameMarginStart: Int
        set(value) {
            val param = mBinding.barName.layoutParams
            if (param is LayoutParams) {
                param.marginStart = value
            }
        }
        get() = mBinding.barName.marginStart

    /**名称（左边文本）边距-右*/
    var nameMarginEnd: Int
        set(value) {
            val param = mBinding.barName.layoutParams
            if (param is LayoutParams) {
                param.marginEnd = value
            }
        }
        get() = mBinding.barName.marginEnd

    /**名称（左边文本）边距-上*/
    var nameMarginTop: Int
        set(value) {
            val param = mBinding.barName.layoutParams
            if (param is LayoutParams) {
                param.topMargin = value
            }
        }
        get() = mBinding.barName.marginTop

    /**名称（左边文本）边距-下*/
    var nameMarginBottom: Int
        set(value) {
            val param = mBinding.barName.layoutParams
            if (param is LayoutParams) {
                param.bottomMargin = value
            }
        }
        get() = mBinding.barName.marginBottom

    /**内容（右边文本）*/
    var content: CharSequence?
        set(value) {
            mBinding.barContent.text = value
        }
        get() = mBinding.barContent.text

    /**内容（右边文本）颜色*/
    @ColorInt
    var contentColor: Int = 0
        set(value) {
            mBinding.barContent.setTextColor(value)
            field = value
        }

    /**内容（右边文本）大小，单位px*/
    var contentSize: Float = 0f
        set(value) {
            mBinding.barContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
            field = value
        }

    /**内容（右边文本）边距*/
    var contentMargin: Int = 0
        set(value) {
            val param = mBinding.barContent.layoutParams
            if (param is LayoutParams) {
                param.setMargins(value, value, value, value)
            }
            field = value
        }

    /**内容（右边文本）边距-左*/
    var contentMarginStart: Int
        set(value) {
            val param = mBinding.barContent.layoutParams
            if (param is LayoutParams) {
                param.marginStart = value
            }
        }
        get() = mBinding.barContent.marginStart

    /**内容（右边文本）边距-右*/
    var contentMarginEnd: Int
        set(value) {
            val param = mBinding.barContent.layoutParams
            if (param is LayoutParams) {
                param.marginEnd = value
            }
        }
        get() = mBinding.barContent.marginEnd

    /**内容（右边文本）边距-上*/
    var contentMarginTop: Int
        set(value) {
            val param = mBinding.barContent.layoutParams
            if (param is LayoutParams) {
                param.topMargin = value
            }
        }
        get() = mBinding.barContent.marginTop

    /**内容（右边文本）边距-下*/
    var contentMarginBottom: Int
        set(value) {
            val param = mBinding.barContent.layoutParams
            if (param is LayoutParams) {
                param.bottomMargin = value
            }
        }
        get() = mBinding.barContent.marginBottom

    /**左边图片*/
    var imgLeftDrawable: Drawable?
        set(value) {
            mBinding.barImgLeft.setImageDrawable(value)
        }
        get() = mBinding.barImgLeft.drawable

    /**
     * @hide
     * 可见类型
     */
    @IntDef(View.VISIBLE, View.INVISIBLE, View.GONE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    private annotation class Visibility {}

    /**左边图片可见类型*/
    @Visibility
    var imgLeftVisibility: Int
        set(value) {
            mBinding.barImgLeft.visibility = value
        }
        get() = mBinding.barImgLeft.visibility

    /**右边图片*/
    var imgRightDrawable: Drawable?
        set(value) {
            mBinding.barImgRight.setImageDrawable(value)
        }
        get() = mBinding.barImgRight.drawable

    /**右边图片可见类型*/
    @Visibility
    var imgRightVisibility: Int
        set(value) {
            mBinding.barImgRight.visibility = value
        }
        get() = mBinding.barImgRight.visibility

    /**底部横线是否显示*/
    var lineShow :Boolean
        set(value) {
            mBinding.barLine.visibility = if(value)View.VISIBLE else View.GONE
        }
        get() = mBinding.barLine.visibility == View.VISIBLE

    /**底部横线宽度*/
    var lineWidth: Int
        set(value) {
            val param = mBinding.barLine.layoutParams
            param.height = value
        }
        get() = mBinding.barLine.layoutParams.height

    /**底部横线颜色*/
    @ColorInt
    var lineColor:Int = 0
        set(value) {
            mBinding.barLine.setBackgroundColor(value)
            field = value
        }

    /**底部横线边距-左*/
    var lineMarginStart: Int
        set(value) {
            val param = mBinding.barLine.layoutParams
            if (param is LayoutParams) {
                param.marginStart = value
            }
        }
        get() = mBinding.barLine.marginStart

    /**底部横线边距-右*/
    var lineMarginEnd: Int
        set(value) {
            val param = mBinding.barLine.layoutParams
            if (param is LayoutParams) {
                param.marginEnd = value
            }
        }
        get() = mBinding.barLine.marginEnd

    /**底部横线边距-下*/
    var lineMarginBottom: Int
        set(value) {
            val param = mBinding.barLine.layoutParams
            if (param is LayoutParams) {
                param.bottomMargin = value
            }
        }
        get() = mBinding.barLine.marginBottom

    init {

        //初始化属性
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingItemView, 0, R.style.SettingItemView)
        name = typedArray.getText(R.styleable.SettingItemView_sName)
        nameSize = typedArray.getDimension(R.styleable.SettingItemView_sNameSize, SizeUtils.sp2px(context,14f))
        nameColor = typedArray.getColor(R.styleable.SettingItemView_sNameColor, Color.BLACK)
        nameMargin = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sNameMargin, 0)
        nameMarginStart = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sNameMarginStart, 0)
        nameMarginEnd = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sNameMarginEnd, 0)
        nameMarginTop = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sNameMarginTop, 0)
        nameMarginBottom = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sNameMarginBottom, 0)

        content = typedArray.getText(R.styleable.SettingItemView_sContent)
        contentSize = typedArray.getDimension(R.styleable.SettingItemView_sContentSize, SizeUtils.sp2px(context,14f))
        contentColor = typedArray.getColor(R.styleable.SettingItemView_sContentColor, Color.BLACK)
        contentMargin = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sContentMargin, 0)
        contentMarginStart = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sContentMarginStart, 0)
        contentMarginEnd = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sContentMarginEnd, 0)
        contentMarginTop = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sContentMarginTop, 0)
        contentMarginBottom = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sContentMarginBottom, 0)

        imgLeftDrawable = typedArray.getDrawable(R.styleable.SettingItemView_sImgLeftSrc)
        imgLeftVisibility = typedArray.getInt(R.styleable.SettingItemView_sImgLeftVisibility, View.VISIBLE)

        imgRightDrawable = typedArray.getDrawable(R.styleable.SettingItemView_sImgRightSrc)
        imgRightVisibility = typedArray.getInt(R.styleable.SettingItemView_sImgRightVisibility, View.VISIBLE)

        lineShow = typedArray.getBoolean(R.styleable.SettingItemView_sLineShow,true)
        lineColor = typedArray.getColor(R.styleable.SettingItemView_sLineColor, Color.GRAY)
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sLineWidth, 1)
        lineMarginStart = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sLineMarginStart, 0)
        lineMarginEnd = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sLineMarginEnd, 0)
        lineMarginBottom = typedArray.getDimensionPixelSize(R.styleable.SettingItemView_sLineMarginBottom, 0)
        typedArray.recycle()
    }
}