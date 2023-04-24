package com.i56s.test

import com.i56s.ktlib.dialog.ConfirmDialog
import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initCreate() {
        //设置返回键点击事件
        mBinding.titleView.setOnBackClickListener {
            LogUtils.d("标题", "返回点击")
            true//表示消费事件，点击不会关闭页面
        }
        //设置标题点击事件
        mBinding.titleView.setOnTitleClickListener {
            LogUtils.d("标题", "标题点击")
        }
    }

    override fun initEvent() {

    }
}