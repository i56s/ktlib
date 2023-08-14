package com.i56s.ktlib.orders

import android.app.Application
import com.i56s.ktlib.activity.CrashActivity
import kotlin.system.exitProcess

/**
 * ### 创建者： wxr
 * ### 创建时间： 2023-08-11 10:25
 * ### 描述：Crash 处理类
 */
class CrashHandler(application: Application) : Thread.UncaughtExceptionHandler {

    companion object {
        fun register(application: Application) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(application))
        }
    }

    private val mApplication = application
    private val mNextHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        if (javaClass.name == mNextHandler::class.java.name) {
            // 请不要重复注册 Crash 监听
            throw IllegalStateException("请勿重复注册 Crash 监听")
        }
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        CrashActivity.start(mApplication, e)
        // 不去触发系统的崩溃处理（com.android.internal.os.RuntimeInit$KillApplicationHandler）
        mNextHandler?.let {
            if (!it.javaClass.name.startsWith("com.android.internal.os")) {
                it.uncaughtException(t, e)
            }
        }
        // 杀死进程（这个事应该是系统干的，但是它会多弹出一个崩溃对话框，所以需要我们自己手动杀死进程）
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}