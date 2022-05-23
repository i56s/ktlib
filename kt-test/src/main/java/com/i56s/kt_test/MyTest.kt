package com.i56s.kt_test

fun main() {
    var b: B? = null
    println("打印输出：${b is A?}")
    b = B()
    println("打印输出2：${b is A?}")
    println("name = ${b.name()} ,age = ${b.age()}")
}

interface A {

    fun name() = "哈哈"

    fun age(): Int
}

class B : A {

    override fun age(): Int = 10
}