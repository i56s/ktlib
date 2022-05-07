package com.i56s.kt_test

fun main() {
    var x = 411
    val a = when {
        x > 420 -> 420
        x < 0 -> 0
        else -> x
    }
    println("输出打印：$a")
}