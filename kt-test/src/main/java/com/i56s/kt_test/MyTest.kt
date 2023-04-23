package com.i56s.kt_test

fun main() {
    println("测试：")
    //println("测试：${Test.F}")
}

open class Base {

    constructor(a: Int)
    constructor(str: String)
}

class A : Base {
    constructor(a: Int) : super(a = a)

    constructor(str: String):super(str)
}