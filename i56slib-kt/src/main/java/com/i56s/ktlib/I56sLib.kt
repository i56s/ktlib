package com.i56s.ktlib

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.i56s.ktlib.utils.LogUtils
import com.i56s.ktlib.utils.SpUtils

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:42
 * ### 描述：初始化类库
 */
@SuppressLint("StaticFieldLeak")
object I56sLib {
    /**当前的页面*/
    @JvmStatic
    lateinit var activity: Activity
        private set

    /**APP上下文*/
    @JvmStatic
    lateinit var context: Context
        private set

    /**状态栏高度*/
    @JvmStatic
    var statusBarHeight: Int = -1
        private set

    /**页面显示计数器*/
    private var activityShowCount: Int = 0

    /**所有页面集合*/
    @JvmStatic
    val activityList: MutableList<Activity> = mutableListOf()

    /**初始化
     * @param spName 设置SharedPreferences文件名(不用添加后缀)，默认名称：SpUtil.xml
     * @param isOut true=输出日志 false=不输出
     * @param level 日志等级*/
    @SuppressLint("PrivateApi")
    fun init(application: Application, spName: String, isOut: Boolean, level: LogUtils.Level) {
        context = application.applicationContext //初始化日志
        LogUtils.init(isOut, level) //初始化sp存储
        SpUtils.init(spName)

        //获取状态栏高度
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }

        application.registerActivityLifecycleCallbacks(object :
                                                           Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityList.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {
                activityShowCount++
            }

            override fun onActivityResumed(activity: Activity) {
                I56sLib.activity = activity
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                activityShowCount--
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

            override fun onActivityDestroyed(activity: Activity) {
                activityList.remove(activity)
            }
        })
    }

    /**判断APP是否在后台*/
    @JvmStatic
    fun isBackground(): Boolean = activityShowCount <= 0

    /**移除所有页面*/
    @JvmStatic
    fun finishAll() = activityList.forEach { it.finish() }
}