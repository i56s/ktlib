package com.i56s.kt_test

import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

fun main() {
   println("""
************* Log Head ****************
Date of Log        : 2023_08_14
Rom Info           : RomInfo{name=huawei, version=8.0.0}
Device Manufacturer: HUAWEI
Device Model       : BZT-W09
Android Version    : 8.0.0
Android SDK        : 26
App VersionName    : 1.62.24debug
App VersionCode    : 124
************* Log Head ****************
""".trimIndent())
}

data class Bean(var age: Int, var name: String?, var h: Int = 19)