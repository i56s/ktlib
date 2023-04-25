package com.i56s.test

import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.model.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun initCreate() {
        mBinding.open.setOnClickListener {
            mModel?.log()
        }
    }

    override fun initEvent() {

    }
}