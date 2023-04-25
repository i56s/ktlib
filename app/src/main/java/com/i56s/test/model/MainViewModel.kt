package com.i56s.test.model

import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.BaseViewModel

/**
 * ### 创建者： wxr
 * ### 创建时间： 2023-04-24 20:03
 * ### 描述： ViewModel测试
 */
class MainViewModel() : BaseViewModel() {

    fun log() {
        LogUtils.d("测试", "哈哈哈哈")
    }
}