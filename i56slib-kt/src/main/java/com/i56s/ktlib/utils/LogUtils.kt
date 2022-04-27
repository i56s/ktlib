package com.i56s.ktlib.utils

import android.util.Log

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:49
 * ### 描述：日志工具类
 */
object LogUtils {

    /**默认的tag*/
    private const val TAG: String = "LogUtils"

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
     * 打印日志(日志等级默认是E级)
     * @param tag 日志标记
     * @param message 日志内容
     */
    @JvmStatic
    fun log(tag: String?, message: String) = log(null, tag, message, null)

    /**
     * 打印日志
     * @param level 日志等级
     * @param tag 日志标记
     * @param message 日志内容
     * @param throwable 错误信息
     */
    @JvmStatic
    fun log(level: Level?, tag: String?, message: String) = log(level, tag, message, null)

    /**
     * 按等级打印日志
     * @param level 日志等级
     * @param tag 日志标记
     * @param message 日志内容
     */
    @JvmStatic
    fun log(level: Level?, tag: String?, message: String, throwable: Throwable?) {
        if (!isOut) return
        val t = if (tag == null) TAG else tag

        when (if (level == null) this.level else level) {
            Level.SYSTEM -> System.out.println("$t --> $message --> ${throwable?.message}")
            Level.VERBOS -> Log.v(t, message, throwable)
            Level.DEBUG -> Log.d(t, message, throwable)
            Level.INFO -> Log.i(t, message, throwable)
            Level.WARN -> Log.w(t, message, throwable)
            else -> Log.e(t, message, throwable)
        }
    }

    /**i级日志*/
    @JvmStatic
    fun i(tag: String, message: String) = i(tag, message, null)

    /**i级日志*/
    @JvmStatic
    fun i(tag: String, message: String, throwable: Throwable?) =
        log(Level.INFO, tag, message, throwable)

    /**e级日志*/
    @JvmStatic
    fun e(tag: String, message: String) = e(tag, message, null)

    /**e级日志*/
    @JvmStatic
    fun e(tag: String, message: String, throwable: Throwable?) =
        log(Level.ERROR, tag, message, throwable)

    /**d级日志*/
    @JvmStatic
    fun d(tag: String, message: String) = d(tag, message, null)

    /**d级日志*/
    @JvmStatic
    fun d(tag: String, message: String, throwable: Throwable?) =
        log(Level.DEBUG, tag, message, throwable)

    /**w级日志*/
    @JvmStatic
    fun w(tag: String, message: String) = w(tag, message, null)

    /**w级日志*/
    @JvmStatic
    fun w(tag: String, message: String, throwable: Throwable?) =
        log(Level.WARN, tag, message, throwable)

    /**v级日志*/
    @JvmStatic
    fun v(tag: String, message: String) = v(tag, message, null)

    /**v级日志*/
    @JvmStatic
    fun v(tag: String, message: String, throwable: Throwable?) =
        log(Level.VERBOS, tag, message, throwable)

    /**s级日志*/
    @JvmStatic
    fun s(tag: String, message: String) = s(tag, message, null)

    /**s级日志*/
    @JvmStatic
    fun s(tag: String, message: String, throwable: Throwable?) =
        log(Level.SYSTEM, tag, message, throwable)

    /**日志等级枚举*/
    enum class Level {
        SYSTEM, VERBOS, DEBUG, INFO, WARN, ERROR
    }
}