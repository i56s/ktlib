package com.i56s.ktlib.utils

import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.i56s.ktlib.I56sLib

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:56
 * ### 描述：吐司工具类
 */
object ToastUtils {

    private var oldMsg: String? = null
    private var toast: Toast? = null
    private var oneTime = 0L
    private var twoTime = 0L

    /**显示吐司*/
    private fun showToast(view: View, msg: String, @BaseTransientBottomBar.Duration duration: Int) =
        Snackbar.make(view, msg, duration).show()

    /**显示吐司*/
    private fun showToast2(context: Activity, msg: String?, duration: Int) {
        if (context.isFinishing || context.isDestroyed) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) { //判断是否为9.0
            Toast.makeText(context, msg, duration).show()
        } else {
            if (toast == null) {
                toast = Toast.makeText(context, msg, duration)
                toast?.show()
                oneTime = System.currentTimeMillis()
            } else {
                twoTime = System.currentTimeMillis()
                if (msg.equals(oldMsg)) {
                    if (twoTime - oneTime > if (duration == Toast.LENGTH_SHORT) Toast.LENGTH_SHORT else Toast.LENGTH_LONG) {
                        toast?.cancel()
                        toast = Toast.makeText(context, msg, duration)
                        toast?.show()
                    }
                } else {
                    oldMsg = msg
                    toast?.cancel()
                    toast = Toast.makeText(context, msg, duration)
                    toast?.show()
                }
            }
            oneTime = twoTime
        }
    }

    /*@Deprecated(
        "由Toast改为Snackbar",
        ReplaceWith("ToastUtils.showToast(view, string)", "ToastUtils.showToast"),
        level = DeprecationLevel.WARNING
    )*/
    /** 显示短吐司 */
    @JvmStatic
    fun showToast(msg: String?) = showToast2(I56sLib.activity, msg, Toast.LENGTH_SHORT)

    /** 显示短吐司 */
    @JvmStatic
    fun showToast(@StringRes resId: Int) = showToast(I56sLib.context.getString(resId))

    /** 显示长吐司 */
    @JvmStatic
    fun showToastLong(msg: String?) = showToast2(I56sLib.activity, msg, Toast.LENGTH_LONG)

    /** 显示长吐司 */
    @JvmStatic
    fun showToastLong(@StringRes resId: Int) = showToast(I56sLib.context.getString(resId))

    /** 显示短吐司 */
    @JvmStatic
    fun showToast(view: View, msg: String) = showToast(view, msg, Snackbar.LENGTH_SHORT)

    /** 显示短吐司 */
    @JvmStatic
    fun showToast(view: View, @StringRes resId: Int) =
        showToast(view, I56sLib.context.getString(resId))

    /** 显示长吐司 */
    @JvmStatic
    fun showToastLong(view: View, msg: String) = showToast(view, msg, Snackbar.LENGTH_LONG)

    @JvmStatic
    fun showToastLong(view: View, @StringRes resId: Int) =
        showToastLong(view, I56sLib.context.getString(resId))
}