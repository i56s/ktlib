package com.i56s.kt_test

import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun main() {
   println("${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.CHINA).format(System.currentTimeMillis())}")
   println("${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.CHINA).format(System.currentTimeMillis())}")
}

data class Bean(var age: Int, var name: String?, var h: Int = 19)