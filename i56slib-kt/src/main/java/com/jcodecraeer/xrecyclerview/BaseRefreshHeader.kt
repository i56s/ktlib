package com.jcodecraeer.xrecyclerview

/**
 * ### 创建者：wxr
 * ### 创建时间：2021-11-26 14:51
 * ### 描述：
 */
interface BaseRefreshHeader {

    companion object {
        /** 正常状态 */
        const val STATE_NORMAL = 0

        /** 准备刷新 */
        const val STATE_RELEASE_TO_REFRESH = 1

        /** 刷新中 */
        const val STATE_REFRESHING = 2

        /** 刷新完成 */
        const val STATE_DONE = 3
    }

    fun onMove(delta: Float)

    fun releaseAction(): Boolean

    fun refreshComplete()
}