package com.i56s.test

import com.i56s.ktlib.dialog.ConfirmDialog
import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    val mDialog = ConfirmDialog()

    override fun initCreate() {
        mBinding.openDialog.setOnClickListener {
            mDialog.show(supportFragmentManager)
        }
        mBinding.save.setOnClickListener {
            //MMKVUtils.putString("test","哈哈哈")
        }
        mBinding.get.setOnClickListener {
            //ToastUtils.showToast(MMKVUtils.getString("test"))
        }

        //是否选中监听
        mBinding.switchBtn.setOnCheckedListener { button, checked ->
            LogUtils.d("isChecked", checked.toString())
        }
    }

    override fun initEvent() {

    }
}