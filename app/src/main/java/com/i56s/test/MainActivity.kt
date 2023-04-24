package com.i56s.test

import com.i56s.ktlib.dialog.LoadingDialog
import com.i56s.test.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initCreate() {
        mBinding.open.setOnClickListener {
            LoadingDialog().setShowMask(false).show()
        }
    }

    override fun initEvent() {

    }
}