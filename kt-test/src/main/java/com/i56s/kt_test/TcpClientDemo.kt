package com.i56s.kt_test

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.util.*

fun main() {
    //1.创建客户端的套接字，做连接，指定服务器的ip+端口
    val s = Socket("127.0.0.1", 20086)
    //2.获取输出流
  val bw=BufferedWriter(OutputStreamWriter(s.getOutputStream()))
    //3.获取输入流
    val br=BufferedReader(InputStreamReader(s.getInputStream()))
    val sc=Scanner(System.`in`)
    println("请输入用户名")

}