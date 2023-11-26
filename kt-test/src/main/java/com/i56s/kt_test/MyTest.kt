package com.i56s.kt_test

import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun main() {
    val list = mutableListOf("a", "b", "c", "d", "c")
    println("列表：${list.joinToString { "%s..".format(it) }}")
}

data class Bean(var age: Int, var name: String?, var h: Int = 19)