package com.i56s.test

import android.widget.Toast
import com.i56s.ktlib.dialog.ConfirmDialog
import com.i56s.test.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    val mDialog= ConfirmDialog()

    override fun initCreate() {
       mBinding.openDialog.setOnClickListener {
           mDialog.show(supportFragmentManager)
       }
    }

    override fun initEvent() {

    }
}