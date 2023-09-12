package com.i56s.kt_test

import com.google.gson.Gson

fun main() {
    /*val bean: Bean? = Bean(20,"张三")
    bean?.let {
        if (it.age == 20) {
            println("数值20")
            return
        }
    }
    val bytes = byteArrayOf(-10, -11, -12, 1, 2, 0xA)
    println("测试${bytes.joinToString(", ") { "0x%02X".format(it) }}")*/

}

data class Bean(var age: Int, var name: String?, var h: Int = 19)