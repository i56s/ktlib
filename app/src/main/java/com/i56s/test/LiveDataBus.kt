package com.i56s.test

/**
 * ### 创建者：wxr
 * ### 创建时间：2023-03-10 16:03
 * ### 描述：消息通知工具类
 */
object LiveDataBus {

    private val mBus = hashMapOf<String, SingleLiveData<Any>>()

    fun <T> with(channel: String): SingleLiveData<T> {
        if (!mBus.containsKey(channel)) {
            mBus[channel] = SingleLiveData()
        }
        return mBus[channel]!! as SingleLiveData<T>
    }

    /**是否显示加载框*/
    fun showLoading(isShow: Boolean) = with<Boolean>("isShowLoading").postValue(isShow)

    /**账号状态
     * @param isState Boolean true账号登入 false账号登出*/
    fun accountState(isState: Boolean) = with<Boolean>("isAccountState").postValue(isState)
}