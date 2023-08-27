package com.i56s.test

import android.view.LayoutInflater
import com.i56s.ktlib.base.LibBaseFragment
import com.i56s.test.databinding.FragmentTestBinding

/**
 * 创建者： wxr
 * 创建时间： 2023-08-27 21:25
 * 描述：
 */
class TestFragment : LibBaseFragment<FragmentTestBinding>() {
    override fun onCreateAfter() {

    }

    override fun getViewBinding(layoutInflater: LayoutInflater): FragmentTestBinding =
        FragmentTestBinding.inflate(layoutInflater)

    override fun initData() {
    }

    override fun initEvent() {
    }
}