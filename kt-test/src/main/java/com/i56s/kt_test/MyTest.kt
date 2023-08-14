package com.i56s.kt_test

import com.google.gson.Gson

fun main() {
    val json = "{\"age\":10,\"name\":\"张三\",\"h\":128}"
    val bean = Gson().fromJson(json,Bean::class.java)
    println("测试：$bean")
    println("测试2：${bean.h}")
}

data class Bean(var age: Int, var name: String?, var h: Int = 19)