package com.i56s.ktlib.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.i56s.ktlib.I56sLib

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:55
 * ### 描述：SharedPreferences工具类，需要先调用init进行初始化
 */
object SpUtils {

    private lateinit var sp: SharedPreferences

    /**
     * 初始化SpUtil工具类
     * @param spName 设置SharedPreferences文件名(不用添加后缀)，默认名称：SpUtil.xml
     */
    internal fun init() {
        sp = I56sLib.context.getSharedPreferences("i56s", Context.MODE_PRIVATE)
    }

    /**
     * 存入字符串
     * @param key     字符串的键
     * @param value   字符串的值
     */
    @JvmStatic
    fun putString(key: String, value: String) = sp.edit().putString(key, value).apply()

    /**
     * 获取字符串
     * @param key     字符串的键
     * @return 得到的字符串
     */
    @JvmStatic
    fun getString(key: String): String? = getString(key, "")

    /**
     * 获取字符串
     * @param key     字符串的键
     * @param defValue   字符串的默认值
     * @return 得到的字符串
     */
    @JvmStatic
    fun getString(key: String, defValue: String): String? = sp.getString(key, defValue)

    /**
     * 保存布尔值
     * @param key     键
     * @param value   值
     */
    @JvmStatic
    fun putBoolean(key: String, value: Boolean) = sp.edit().putBoolean(key, value).apply()

    /**
     * 获取布尔值
     * @param key      键
     * @return 返回保存的值，默认返回false
     */
    @JvmStatic
    fun getBoolean(key: String): Boolean = getBoolean(key, false)

    /**
     * 获取布尔值
     * @param key      键
     * @param defValue 默认值
     * @return 返回保存的值
     */
    @JvmStatic
    fun getBoolean(key: String, defValue: Boolean): Boolean = sp.getBoolean(key, defValue)

    /**
     * 保存long值
     * @param key     键
     * @param value   值
     */
    @JvmStatic
    fun putLong(key: String, value: Long) = sp.edit().putLong(key, value).apply()

    /**
     * 获取long值
     * @param key      键
     * @return 保存的值
     */
    @JvmStatic
    fun getLong(key: String): Long = getLong(key, 0L)

    /**
     * 获取long值
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    @JvmStatic
    fun getLong(key: String, defValue: Long): Long = sp.getLong(key, defValue)

    /**
     * 保存int值
     * @param key     键
     * @param value   值
     */
    @JvmStatic
    fun putInt(key: String, value: Int) = sp.edit().putInt(key, value).apply()

    /**
     * 获取int值
     * @param key      键
     * @return 保存的值
     */
    @JvmStatic
    fun getInt(key: String): Int = getInt(key, 0)

    /**
     * 获取int值
     * @param key      键
     * @param defValue 默认值
     * @return 保存的值
     */
    @JvmStatic
    fun getInt(key: String, defValue: Int): Int = sp.getInt(key, defValue)
}