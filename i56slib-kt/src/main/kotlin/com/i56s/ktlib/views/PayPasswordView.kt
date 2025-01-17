package com.i56s.ktlib.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.i56s.ktlib.R

/**
 * @author wxr
 * @createtime 2023-02-13 09:25
 * @desc 交易密码输入控件
 */
class PayPasswordView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var input: InputMethodManager? = null
    private var size = 0f //格子大小
    private var lineHeight = 0f //横线高度
    private var lineColor = 0 //边框颜色
    private var dotColor = 0 //圆点颜色
    private var num = 0 //绘制数量，多少个框
    private var borderGap = 0f //边框间距
    private var bgPaint = Paint() //背景画笔
    private var dot: String? = null //密码样式
    private var dotPaint = Paint() //圆点画笔
    private var result = mutableListOf<Int>()
    private var paintRect = Rect()
    private var onInputDoneListener: ((view: View, result: String) -> Unit)? = null
    private var onInputtingListener: ((view: View, result: String) -> Unit)? = null
    private val mBuilder = StringBuilder()
    private val mTextRect = Rect()

    /**输入时是否显示数据 true=显示 false=不显示*/
    var isShowNumber = false

    /**密码*/
    var password = ""
        private set

    /**是否自动清除密码*/
    var isAutoClear = false

    init {
        isFocusable = true
        isFocusableInTouchMode = true
        input = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.PayPasswordView)
        lineColor = typedArray.getColor(R.styleable.PayPasswordView_ppv_lineColor, Color.BLACK)
        dot = typedArray.getString(R.styleable.PayPasswordView_ppv_dot)
        borderGap = typedArray.getDimension(R.styleable.PayPasswordView_ppv_borderGap, 0f)
        lineHeight = typedArray.getDimension(R.styleable.PayPasswordView_ppv_lineHeight, -1f)
        size = typedArray.getDimension(R.styleable.PayPasswordView_ppv_size, getDp(40f))
        dotColor = typedArray.getColor(R.styleable.PayPasswordView_ppv_dotColor, Color.GRAY)
        isAutoClear = typedArray.getBoolean(R.styleable.PayPasswordView_ppv_autoClear, false)
        isShowNumber = typedArray.getBoolean(R.styleable.PayPasswordView_ppv_isShowNumber, false)
        num = typedArray.getInt(R.styleable.PayPasswordView_ppv_num, 6)
        typedArray.recycle()

        bgPaint.color = lineColor
        bgPaint.style = Paint.Style.FILL

        dotPaint.color = dotColor
        dotPaint.style = Paint.Style.FILL
        dotPaint.textSize = getSp(15f)
        dotPaint.strokeWidth = getDp(3f)
        dotPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        var wSize = MeasureSpec.getSize(widthMeasureSpec)
        //只根据宽度来控制控件的宽和高
        if (wMode == MeasureSpec.AT_MOST) {
            wSize = ((size + borderGap) * num).toInt()
        } else {
            size = wSize / num - borderGap
        }
        setMeasuredDimension(wSize, size.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        //下面两行是新加
        val measuredWidth = measuredWidth
        val rectWidth = (measuredWidth - (num + 1) - (num - 1) * borderGap) / num
        //画边框
        for (i in 0 until num) {
            //下面是新加的
            //画背景
            val x1 = i * rectWidth + i + i * borderGap
            var x2 = x1 + rectWidth
            if (i == num - 1) {
                x2 = measuredWidth.toFloat()
            }
            if (lineHeight == -1f) {
                lineHeight = 0f
            } else if (lineHeight > height) {
                lineHeight = height.toFloat()
            }
            paintRect.set(x1.toInt(), (height - lineHeight).toInt(), x2.toInt(), height)
            canvas?.drawRect(paintRect, bgPaint)
            //画圆
            if (result.size > 0 && i <= result.size - 1) {
                if (isShowNumber) {
                    drawText(canvas, result[i].toString())
                } else if (!TextUtils.isEmpty(dot)) {
                    drawText(canvas, dot!!)
                } else {
                    paintRect.set(x1.toInt(), 0, x2.toInt(), height)
                    canvas?.drawCircle(
                        paintRect.centerX().toFloat(),
                        paintRect.centerY().toFloat(),
                        paintRect.width() / 5f,
                        dotPaint
                    )
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //点击控件时获取焦点弹出软键盘输入
        event?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                requestFocus()
                input?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onFocusChanged(gainFocus: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect)
        if (gainFocus) {
            input?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        } else {
            input?.hideSoftInputFromWindow(windowToken, 0)
        }
    }

    override fun onCheckIsTextEditor(): Boolean = true

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        outAttrs?.inputType = InputType.TYPE_CLASS_NUMBER
        outAttrs?.imeOptions = EditorInfo.IME_ACTION_DONE
        return BaseInputConnection(this, false)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        event?.let {
            if (it.isShiftPressed) return false
            if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                if (result.size < num) {
                    result.add(keyCode - 7)
                    mBuilder.append(keyCode - 7)
                    onInputtingListener?.invoke(this, mBuilder.toString())
                    invalidate()
                }
                if (result.size >= num) {
                    if (!isAutoClear) {
                        finishInput()
                    } else {
                        //如果没有这个延迟,并且设置了AutoClear,因为执行太快了,界面上看起来像是不会显示最后一位密码的圆点而直接clear
                        //很像没有输入最后一位就结束了...看起来总觉得不得劲..
                        Handler(Looper.getMainLooper()).postDelayed({
                            finishInput()
                            clear()
                        }, 160)
                    }
                }
                return true
            }
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                if (result.isNotEmpty()) {
                    result.removeAt(result.size - 1)
                    if (mBuilder.isNotEmpty()) {
                        mBuilder.deleteCharAt(mBuilder.length - 1)
                    }
                    password = mBuilder.toString()
                    onInputtingListener?.invoke(this, mBuilder.toString())
                    invalidate()
                }
                return true
            }
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun finishInput() {
        val sb = StringBuilder()
        result.forEach {
            sb.append(it)
        }
        password = sb.toString()
        onInputDoneListener?.invoke(this, password)
    }

    /** 清空输入 */
    fun clear() {
        if (result.isNotEmpty()) {
            result.clear()
        }
        invalidate()
    }

    /**添加密码
     * @param password 密码的单个字符(只能数字类型)*/
    fun add(password: Array<String>) {
        result.clear()
        password.forEach {
            if (it.matches(Regex("\\d"))) {
                result.add(Integer.parseInt(it))
            }
        }
        invalidate()
    }

    /** 设置监听回调 */
    fun setOnInputDoneListener(onInputDoneListener: ((view: View, result: String) -> Unit)?) {
        this.onInputDoneListener = onInputDoneListener
    }

    /** 设置输入中监听 */
    fun setOnInputtingListener(inputtingListener: ((view: View, result: String) -> Unit)?) {
        this.onInputtingListener = inputtingListener
    }

    /**画文本*/
    private fun drawText(canvas: Canvas?, text: String) {
        dotPaint.getTextBounds(text, 0, text.length, mTextRect)
        canvas?.drawText(
            text,
            paintRect.centerX().toFloat() - mTextRect.width() / 2 + 10f,
            paintRect.centerY().toFloat() - mTextRect.height() / 2,
            dotPaint
        )
    }

    private fun getSp(sp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)

    private fun getDp(dp: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)

}