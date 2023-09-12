package com.i56s.test

import android.content.Intent
import com.i56s.ktlib.adapter.TabPagerAdapter
import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.model.MainViewModel
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.RuntimeException

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun initCreate() {
        LogUtils.d("ssss")
    }

    override fun initEvent() {
        mBinding.show.setOnClickListener {
            try {
                val open = assets.open("china_city_data.json")
                val read = InputStreamReader(open)
                read.readLines().forEach(LogUtils::d)
                read.close()
                open.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}