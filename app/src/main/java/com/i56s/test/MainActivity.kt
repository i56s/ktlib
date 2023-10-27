package com.i56s.test

import android.content.Intent
import android.widget.SeekBar
import com.i56s.ktlib.adapter.TabPagerAdapter
import com.i56s.ktlib.dialog.ConfirmDialog
import com.i56s.ktlib.orders.setOnSingleClickListener
import com.i56s.ktlib.utils.LogUtils
import com.i56s.ktlib.utils.ToastUtils
import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.model.MainViewModel
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.RuntimeException

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun initCreate() {
        val adapter = TestAdapter(mContext, mutableListOf("1", "2", "3", "4"))

        adapter.setOnItemLongClickListener { position, data ->
            ToastUtils.showToast("长按$data")
            true
        }
        adapter.setOnItemClickListener { position, data -> ToastUtils.showToast("点击$data") }
    }

    override fun initEvent() {
        mBinding.mainOpen.setOnSingleClickListener {
            val dialog = ConfirmDialog()
            dialog.show()
        }
        //滑动监听
        mBinding.mainPro.setOnProgressSlideListener({ v ->
            LogUtils.d("滑动开始")
        }) { v ->
            LogUtils.d("滑动结束")
        }
    }
}