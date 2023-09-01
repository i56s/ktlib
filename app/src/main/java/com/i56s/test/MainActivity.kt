package com.i56s.test

import android.content.Intent
import com.i56s.ktlib.adapter.TabPagerAdapter
import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.model.MainViewModel
import java.lang.RuntimeException

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun initCreate() {}

    override fun initEvent() {
        mBinding.show.setOnClickListener {
            val list = mutableListOf<String>()
            list.add("哈哈哈")
            list.add("呵呵呵")
            mBinding.list.adapter = TestAdapter(mContext, list)
        }
    }
}