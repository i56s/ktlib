package com.i56s.test

import com.i56s.test.databinding.ActivitySecondBinding

/**
 * 创建者：wxr
 * 创建时间：2022-05-07 10:16
 * 描述：
 */
class SecondActivity : BaseActivity<ActivitySecondBinding>() {

    override fun getViewBinding(): ActivitySecondBinding =
        ActivitySecondBinding.inflate(layoutInflater)

    override fun initCreate() {
    }

    override fun initEvent() {
    }
}