package com.i56s.test

import android.content.Intent
import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.model.MainViewModel
import java.lang.RuntimeException

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun initCreate() {

    }

    override fun initEvent() {
        mBinding.open.setOnClickListener {
            startService(Intent(mContext,MyService::class.java))
        }
        mBinding.close.setOnClickListener {
            stopService(Intent(mContext,MyService::class.java))
        }
    }
}