package com.i56s.ktlib.utils

import android.util.Log

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:49
 * ### 描述：日志工具类
 */
object LogUtils {

    /**默认的tag*/
    private const val TAG = "LogUtils"

    /**最大打印长度*/
    private const val MAX_LEN = 1000

    /**日志打印等级 默认e级*/
    var level: Level = Level.ERROR

    /**是否输出日志*/
    var isOut: Boolean = true

    /**
     * 初始化日志打印
     * @param isOut true=输出日志 false=不输出
     * @param level 日志等级
     */
    internal fun init(isOut: Boolean, level: Level) {
        this.isOut = isOut
        this.level = level
    }

    /**
     * 按等级打印日志
     * @param level 日志等级
     * @param tag 日志标记
     * @param message 日志内容
     * @param throwable 错误对象
     */
    @JvmStatic
    fun log(level: Level? = null, tag: String?, message: String?, throwable: Throwable? = null) {
        if (!isOut) return

        val stack = Thread.currentThread().stackTrace
        val sbf = StringBuilder()
        var tagName = tag
        if (stack.size >= 6) {
            try {
                tagName = tag ?: stack[5].fileName.substring(0, stack[5].fileName.indexOfLast {
                    it == '.'
                })
            } catch (_: Exception) {
            }
            sbf.append("打印位置：")
                .append('(')
                .append(stack[5].fileName)
                .append(':')
                .append(stack[5].lineNumber)
                .append(')')
            //.append('\n')
        }
        message?.let {
            sbf.append(" 总字数：")
            sbf.append(it.length)
            sbf.append('，')
            sbf.append("已打印数：")
            val str = sbf.toString()
            var count = 0 //已打印数
            var residue = 0 //剩余
            if (it.length > MAX_LEN) {
                var msg = it
                while (msg.length > MAX_LEN) {
                    count += MAX_LEN
                    residue = it.length - count
                    printOut(
                        level, tagName, "$str$count，剩余数：$residue\n${msg.substring(0, MAX_LEN)}",
                        throwable
                    )
                    msg = msg.substring(MAX_LEN)
                }
                if (msg.isNotEmpty()) {
                    printOut(level, tagName, "$str${it.length}，剩余数：0\n$msg", throwable)
                }
            } else {
                sbf.append(it.length)
                sbf.append('，')
                sbf.append("剩余数：")
                sbf.append('0')
                sbf.append('\n')
                sbf.append(it)
                printOut(level, tagName, sbf.toString(), throwable)
            }
        }
    }

    private fun printOut(
            level: Level? = null,
            tag: String?,
            message: String?,
            throwable: Throwable? = null
    ) {
        val t = tag ?: TAG
        when (level ?: this.level) {
            Level.SYSTEM -> println("$t --> $message --> ${throwable?.message}")
            Level.VERBOS -> Log.v(t, message, throwable)
            Level.DEBUG -> Log.d(t, message, throwable)
            Level.INFO -> Log.i(t, message, throwable)
            Level.WARN -> Log.w(t, message, throwable)
            else -> Log.e(t, message, throwable)
        }
    }

    /**i级日志*/
    @JvmStatic
    fun i(message: String?, throwable: Throwable? = null) =
        log(Level.INFO, null, message, throwable)

    /**i级日志*/
    @JvmStatic
    fun i(tag: String?, message: String?, throwable: Throwable? = null) =
        log(Level.INFO, tag, message, throwable)

    /**e级日志*/
    @JvmStatic
    fun e(message: String?, throwable: Throwable? = null) =
        log(Level.ERROR, null, message, throwable)

    /**e级日志*/
    @JvmStatic
    fun e(tag: String?, message: String?, throwable: Throwable? = null) =
        log(Level.ERROR, tag, message, throwable)

    /**d级日志*/
    @JvmStatic
    fun d(message: String?, throwable: Throwable? = null) =
        log(Level.DEBUG, null, message, throwable)

    /**d级日志*/
    @JvmStatic
    fun d(tag: String?, message: String?, throwable: Throwable? = null) =
        log(Level.DEBUG, tag, message, throwable)

    /**w级日志*/
    @JvmStatic
    fun w(message: String?, throwable: Throwable? = null) =
        log(Level.WARN, null, message, throwable)

    /**w级日志*/
    @JvmStatic
    fun w(tag: String?, message: String?, throwable: Throwable? = null) =
        log(Level.WARN, tag, message, throwable)

    /**v级日志*/
    @JvmStatic
    fun v(message: String?, throwable: Throwable? = null) =
        log(Level.VERBOS, null, message, throwable)

    /**v级日志*/
    @JvmStatic
    fun v(tag: String?, message: String?, throwable: Throwable? = null) =
        log(Level.VERBOS, tag, message, throwable)

    /**s级日志*/
    @JvmStatic
    fun s(message: String?, throwable: Throwable? = null) =
        log(Level.SYSTEM, null, message, throwable)

    /**s级日志*/
    @JvmStatic
    fun s(tag: String?, message: String?, throwable: Throwable? = null) =
        log(Level.SYSTEM, tag, message, throwable)

    /**日志等级枚举*/
    enum class Level {
        SYSTEM, VERBOS, DEBUG, INFO, WARN, ERROR
    }
}