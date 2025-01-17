package com.i56s.test

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

/**
 * ### 创建者：wxr
 * ### 创建时间：2023-03-10 16:02
 * ### 描述：修改LiveData粘性事件
 */
class SingleLiveData<T> : MutableLiveData<T>() {

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
        hook(observer)
    }

    fun observeStick(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
    }

    /**反射获取 mLastVersion 修改值*/
    private fun hook(observer: Observer<in T>) {
        val liveDataClass = LiveData::class.java
        val mObserversField = liveDataClass.getDeclaredField("mObservers")
        mObserversField.isAccessible = true
        val mObservers = mObserversField.get(this)
        val observerClass = mObservers::class.java

        val get = observerClass.getDeclaredMethod("get", Any::class.java)
        get.isAccessible = true
        val entry = get.invoke(mObservers, observer)
        val observerWrapper = (entry as Map.Entry<*, *>).value
        val wrapperClass = observerWrapper?.javaClass?.superclass

        val mLastVersion = wrapperClass?.getDeclaredField("mLastVersion")
        mLastVersion?.isAccessible = true
        val mVersion = liveDataClass.getDeclaredField("mVersion")
        mVersion.isAccessible = true
        val mVersionValue = mVersion.get(this)
        mLastVersion?.set(observerWrapper, mVersionValue)
    }
}