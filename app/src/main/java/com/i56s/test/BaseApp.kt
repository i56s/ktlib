package com.i56s.test

import android.app.Application
import com.i56s.ktlib.I56sLib
import com.i56s.ktlib.utils.LogUtils

/**
 * 创建者：wxr
 *
 * 创建时间：2021-09-18 17:41
 *
 * 描述：
 */
class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        I56sLib.init(this,  BuildConfig.DEBUG)
    }
}