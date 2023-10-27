package com.i56s.ktlib.activity

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.i56s.ktlib.base.LibBaseActivity
import com.i56s.ktlib.base.LibBaseViewModel
import com.i56s.ktlib.databinding.ActivityCrashBinding
import java.io.PrintWriter
import java.io.StringWriter
import java.lang.StringBuilder
import java.net.InetAddress
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.system.exitProcess

/**
 * ### 创建者： wxr
 * ### 创建时间： 2023-08-11 11:01
 * ### 描述：崩溃捕捉界面
 */
class CrashActivity : LibBaseActivity<ActivityCrashBinding, LibBaseViewModel>() {
    override fun isRemoveStatusBar(): Boolean = false

    override fun isOverstepStatusBar(): Boolean = false

    override fun onCreateBefore() = Unit

    override fun onCreateAfter() = Unit

    override fun getViewBinding(): ActivityCrashBinding =
        ActivityCrashBinding.inflate(layoutInflater)

    override fun getViewModel(): Class<LibBaseViewModel>? = null

    private var mStackTrace: String? = null

    companion object {
        /** 报错代码行数正则表达式 */
        private val CODE_REGEX = Pattern.compile("\\(\\w+\\.\\w+:\\d+\\)")

        /** 系统包前缀列表 */
        private val SYSTEM_PACKAGE_PREFIX_LIST = arrayOf(
            "android", "com.android", "androidx", "com.google.android", "java", "javax", "dalvik",
            "kotlin"
        )

        fun start(application: Application, throwable: Throwable) {
            val intent = Intent(application, CrashActivity::class.java)//
                .putExtra("throwable", throwable)//
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            application.startActivity(intent)
        }
    }

    override fun initCreate() {
        val throwable = (intent.getSerializableExtra("throwable") as Throwable?) ?: return
        mBinding.tvCrashTitle.text = throwable::class.java.simpleName
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        throwable.cause?.printStackTrace(printWriter)
        mStackTrace = stringWriter.toString()
        val matcher = CODE_REGEX.matcher(mStackTrace)
        val spannable = SpannableStringBuilder(mStackTrace)
        if (spannable.isNotEmpty()) {
            while (matcher.find()) {
                // 不包含左括号（
                val start = matcher.start() + 1
                // 不包含右括号 ）
                val end = matcher.end() - 1

                // 代码信息颜色
                var codeColor = Color.parseColor("#999999")
                val lineIndex = mStackTrace?.lastIndexOf("at ", start) ?: 0
                if (lineIndex != -1) {
                    val lineData = spannable.subSequence(lineIndex, start).toString()
                    if (lineData.isEmpty()) continue
                    // 是否高亮代码行数
                    var highlight = true
                    for (packagePrefix in SYSTEM_PACKAGE_PREFIX_LIST) {
                        if (lineData.startsWith("at $packagePrefix")) {
                            highlight = false
                            break
                        }
                    }
                    if (highlight) {
                        codeColor = Color.parseColor("#287BDE")
                    }
                }

                // 设置前景
                spannable.setSpan(
                    ForegroundColorSpan(codeColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                // 设置下划线
                spannable.setSpan(UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            mBinding.tvCrashMessage.text = spannable
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        val smallestWidth = screenWidth.coerceAtMost(screenHeight) / displayMetrics.density

        val targetResource = when {
            displayMetrics.densityDpi > 480 -> "xxxhdpi"
            displayMetrics.densityDpi > 320 -> "xxhdpi"
            displayMetrics.densityDpi > 240 -> "xhdpi"
            displayMetrics.densityDpi > 160 -> "hdpi"
            displayMetrics.densityDpi > 120 -> "mdpi"
            else -> "ldpi"
        }
        val builder = StringBuilder()
        builder.append("设备品牌：\t")//
            .append(Build.BRAND)//
            .append("\n设备型号：\t")//
            .append(Build.MODEL)//
            .append("\n设备类型：\t")//
            .append(if (isTablet()) "平板" else "手机")

        builder.append("\n屏幕宽高：\t")//
            .append(screenWidth)//
            .append(" x ")//
            .append(screenHeight)//
            .append("\n屏幕密度：\t")//
            .append(displayMetrics.densityDpi)//
            .append("\n密度像素：\t")//
            .append(displayMetrics.density)//
            .append("\n目标资源：\t")//
            .append(targetResource)//
            .append("\n最小宽度：\t")//
            .append(smallestWidth.toInt())

        builder.append("\n安卓版本：\t")//
            .append(Build.VERSION.RELEASE)//
            .append("\nAPI 版本：\t")//
            .append(Build.VERSION.SDK_INT)//
            .append("\nCPU 架构：\t")//
            .append(Build.SUPPORTED_ABIS[0])

        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            builder.append("\n应用版本：\t")//
                .append(info.versionName)//
                .append("\n版本代码：\t")//
                .append(info.versionCode)

            val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            builder.append("\n首次安装：\t")//
                .append(dateFormat.format(Date(info.firstInstallTime)))//
                .append("\n最近安装：\t")//
                .append(dateFormat.format(Date(info.lastUpdateTime)))//
                .append("\n崩溃时间：\t")//
                .append(dateFormat.format(Date()))

            info.requestedPermissions?.let{
                val permissions = it.toList()

                if (permissions.contains(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) || permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ) {
                    builder.append("\n存储权限：\t")//
                        .append(
                            if (checkedPermission(
                                    Manifest.permission_group.STORAGE
                                )
                            ) "已获得" else "未获得"
                        )
                }

                if (permissions.contains(Manifest.permission.ACCESS_FINE_LOCATION)//
                    || permissions.contains(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    builder.append("\n定位权限：\t")
                    if (checkedPermission(Manifest.permission.ACCESS_FINE_LOCATION)//
                        && checkedPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    ) {
                        builder.append("精确、粗略")
                    } else {
                        if (checkedPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                            builder.append("精确")
                        } else if (checkedPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                            builder.append("粗略")
                        } else {
                            builder.append("未获得")
                        }
                    }
                }

                if (permissions.contains(Manifest.permission.CAMERA)) {
                    builder.append("\n相机权限：\t")//
                        .append(
                            if (checkedPermission(Manifest.permission.CAMERA)) "已获得" else "未获得"
                        )
                }

                if (permissions.contains(Manifest.permission.RECORD_AUDIO)) {
                    builder.append("\n录音权限：\t")//
                        .append(
                            if (checkedPermission(
                                    Manifest.permission.RECORD_AUDIO
                                )
                            ) "已获得" else "未获得"
                        )
                }

                if (permissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                    builder.append("\n悬浮窗权限：\t")//
                        .append(
                            if (checkedPermission(
                                    Manifest.permission.SYSTEM_ALERT_WINDOW
                                )
                            ) "已获得" else "未获得"
                        )
                }

                if (permissions.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                    builder.append("\n安装包权限：\t")//
                        .append(
                            if (checkedPermission(
                                    Manifest.permission.REQUEST_INSTALL_PACKAGES
                                )
                            ) "已获得" else "未获得"
                        )
                }

                /*if (permissions.contains(Manifest.permission.INTERNET)) {
                    builder.append("\n当前网络访问：\t")

                    Thread {
                        try {
                            InetAddress.getByName("www.baidu.com")
                            builder.append("正常")
                        } catch (ignored: UnknownHostException) {
                            builder.append("异常")
                        }
                        runOnUiThread {
                            mBinding.tvCrashInfo.text = builder
                        }
                    }.start()
                } else {
                    mBinding.tvCrashInfo.text = builder
                }*/
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        mBinding.tvCrashInfo.text = builder
    }

    override fun initEvent() {
        mBinding.ivCrashInfo.setOnClickListener {
            mBinding.dlCrashDrawer.openDrawer(GravityCompat.START)
        }
        mBinding.ivCrashShare.setOnClickListener {
            //分享文本
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, mStackTrace)
            startActivity(Intent.createChooser(intent, ""))
        }
        mBinding.ivCrashRestart.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        // 重启应用
        val intent = baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
        exitProcess(0)
    }

    /**判断当前设备是否是平板*/
    private fun isTablet(): Boolean =
        resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

    /**检查权限是否获得
     * @return true=获得 false=被拒绝*/
    private fun checkedPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED
}