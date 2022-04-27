package com.i56s.kt_test

import java.net.ServerSocket


fun main() {
    //1.创建服务器的套接字
    val ss = ServerSocket(20086)
    //2.监听客户端的套接字，并且返回客户端的套接字
    val s = ss.accept()//阻塞式方法
    //3.获取输入流
    val iss = s.getInputStream()
    //4.读取数据
    val by = ByteArray(1024)
    var num = 0
    /*while ((num=iss.read(by)) != -1) {
        println("输出数据：${String(by,0,num)}")
    }*/
    s.close()
}