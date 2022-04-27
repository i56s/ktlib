package com.i56s.ktlib.utils

import java.security.MessageDigest

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:51
 * ### 描述：字符串转MD5 MD5加密/解密(以16进制形式)
 */
object Md5Utils {

    /**
     * 字符串转换成MD5 生成32位md5码
     * @param inStr 需要转换的字符串
     * @return 转换后的MD5码
     */
    @JvmStatic
    fun string2MD5(inStr: String): String? {
        try {
            val digest = MessageDigest.getInstance("MD5")
            digest.reset()
            digest.update(inStr.toByteArray(Charsets.UTF_8))
            val a = digest.digest()
            val sb = StringBuilder(a.size shl 1)
            for (i in a.indices) {
                sb.append(Character.forDigit((a[i].toInt() and 0xf0) shr 4, 32))
                sb.append(Character.forDigit(a[i].toInt() and 0x0f, 32))
            }
            return sb.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 字符串加密
     * @param inStr 需要加密的字符串
     * @return 加密后的字符串(以16进制形式返回)
     */
    @JvmStatic
    fun decryption(inStr: String): String {
        val a = inStr.toCharArray()
        for (i in a.indices) {
            a[i] = (a[i].toInt() xor '>'.toInt()).toChar()
        }
        return str2HexStr(String(a))
    }

    /**
     * 字符串解密
     * @param inStr 加密后的字符串
     * @return 加密前的字符串
     */
    @JvmStatic
    fun encryption(inStr: String): String {
        val str = hexStr2Str(inStr)
        val a = inStr.toCharArray()
        for (i in a.indices) {
            a[i] = (a[i].toInt() xor '>'.toInt()).toChar()
        }
        return String(a)
    }

    /**字符串转换成十六进制字符串*/
    @JvmStatic
    fun str2HexStr(str: String): String {
        val chars = "0123456789ABCDEF".toCharArray()
        val sb = StringBuilder()
        val bs = str.toByteArray()
        var bit: Int
        for (i in bs.indices) {
            bit = (bs[i].toInt() and 0x0f0) shr 4
            sb.append(chars[bit])
            bit = bs[i].toInt() and 0x0f
            sb.append(chars[bit])
        }
        return sb.toString()
    }

    /**十六进制转换字符串*/
    @JvmStatic
    fun hexStr2Str(hexStr: String): String {
        val str = "0123456789ABCDEF"
        val hexs = hexStr.toCharArray()
        val bytes = ByteArray(hexStr.length / 2)
        var n: Int
        for (i in bytes.indices) {
            n = str.indexOf(hexs[2 * i]) * 16
            n += str.indexOf(hexs[2 * i + 1])
            bytes[i] = (n and 0xff).toByte()
        }
        return String(bytes)
    }
}