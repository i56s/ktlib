package com.i56s.test

import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.model.MainViewModel
import java.lang.RuntimeException

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun initCreate() {
        mBinding.open.setOnClickListener {
            throw RuntimeException("测试错误")
        }
    }

    override fun initEvent() {

    }
}