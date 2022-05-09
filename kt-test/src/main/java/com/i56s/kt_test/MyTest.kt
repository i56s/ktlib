package com.i56s.kt_test

fun main() {
    val b=B()
    println("name = ${b.name()} ,age = ${b.age()}")
}

interface A {

    fun name() = "哈哈"

    fun age(): Int
}

class B : A {

    override fun age(): Int = 10
}