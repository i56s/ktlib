package com.i56s.ktlib.utils

import android.content.Context
import android.net.*

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-09-18 16:53
 * ### 描述：网络状态工具类
 */
object NetworkUtils {

    private val TAG = "网络监听"

    /**没有网络*/
    const val NETWORK_NONE = -1

    /**移动网络*/
    const val NETWORK_MOBILE = 0

    /**无线网络*/
    const val NETWORK_WIFI = 1

    /**获取网络状态*/
    @JvmStatic
    fun getNetWorkState(context: Context): Int {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo?.isConnected == true) {
            if (connectivityManager.activeNetworkInfo?.type == ConnectivityManager.TYPE_WIFI) {
                return NETWORK_WIFI
            } else if (connectivityManager.activeNetworkInfo?.type == ConnectivityManager.TYPE_MOBILE) {
                return NETWORK_MOBILE
            }
        }
        return NETWORK_NONE
    }

    /**监听网络变化*/
    @JvmStatic
    fun monitorNetworkChange(context: Context, listener: OnNetworkChangeListener?) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(
            NetworkRequest.Builder().build(),
            object : ConnectivityManager.NetworkCallback() {
                /**网络可用的回调 */
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    listener?.onNetworkAvailable(network)
                    LogUtils.e(TAG, "当前网络可用onAvailable")
                }

                /**网络丢失的回调*/
                override fun onLost(network: Network) {
                    super.onLost(network)
                    listener?.onNetworkLost(network)
                    LogUtils.e(TAG, "当前网络不可用onLost")
                }

                /**当建立网络连接时，回调连接的属性*/
                override fun onLinkPropertiesChanged(
                    network: Network,
                    linkProperties: LinkProperties
                ) {
                    super.onLinkPropertiesChanged(network, linkProperties)
                    LogUtils.e(TAG, "onLinkPropertiesChanged")
                }

                /**按照官方的字面意思是，当我们的网络的某个能力发生了变化回调，那么也就是说可能会回调多次*/
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    LogUtils.e(TAG, "onCapabilitiesChanged")
                }

                /**在网络失去连接的时候回调，但是如果是一个生硬的断开，他可能不回调*/
                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    LogUtils.e(TAG, "onLosing")
                }

                /**按照官方注释的解释，是指如果在超时时间内都没有找到可用的网络时进行回调*/
                override fun onUnavailable() {
                    super.onUnavailable()
                    LogUtils.e(TAG, "onUnavailable")
                }
            })
    }

    /**网络变化监听器*/
    interface OnNetworkChangeListener {

        /**网络可用时回调*/
        fun onNetworkAvailable(network: Network)

        /**网络丢失时回调*/
        fun onNetworkLost(network: Network)
    }
}