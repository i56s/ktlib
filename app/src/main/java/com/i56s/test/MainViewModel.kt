package com.i56s.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 创建者：wxr
 *
 * 创建时间：2021-09-26 15:24
 *
 * 描述：
 */
class MainViewModel : ViewModel() {

    private val _counter = MutableLiveData<Int>()

    val count: LiveData<Int> get() = _counter

    init {
        _counter.value = 0
    }

    fun plusOne() {
        val size = _counter.value ?: 0
        _counter.value = size + 1
    }
}