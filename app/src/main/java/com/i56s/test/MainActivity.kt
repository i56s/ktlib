package com.i56s.test

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.i56s.ktlib.orders.setOnSingleClickListener
import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.model.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            LogUtils.d("绑定成功：$service")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtils.d("断开绑定")
        }
    }

    override fun initCreate() {
        LiveDataBus.with<Int>("a").observe(this) {
            LogUtils.d("首页：$it")
        }
    }

    var index = 0

    override fun initEvent() {
        mBinding.serviceBind.setOnSingleClickListener {
            //bindService(Intent(this, MyService::class.java), connection, Context.BIND_AUTO_CREATE)
            LiveDataBus.with<Int>("a").postValue(index++)
        }
        mBinding.serviceJump.setOnSingleClickListener {
            //startActivity(Intent(this, LoadMoreActivity::class.java))
            //mBinding.mainWeb.loadUrl("file:///android_asset/test.html")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}