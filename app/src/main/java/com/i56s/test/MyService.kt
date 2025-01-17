package com.i56s.test

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.i56s.ktlib.utils.LogUtils

/**
 * 创建者： wxr
 * 创建时间： 2023-08-17 19:01
 * 描述：
 */
class MyService : Service() {

    private val TAG = "服务"
    override fun onBind(intent: Intent?): IBinder = myBinder

    private val myBinder = MyBinder()

    override fun onCreate() {
        super.onCreate()
        LogUtils.d(TAG, "创建了")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        LogUtils.d(TAG, "onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.d(TAG, "停止了")
    }

    class MyBinder :Binder(){

    }
}