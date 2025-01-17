package com.i56s.test

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i56s.ktlib.utils.LogUtils
import com.i56s.ktlib.utils.ToastUtils
import com.i56s.ktlib.views.xrefresh.MaterialLoaderView
import com.i56s.test.databinding.ActivityLoadMoreBinding

/**
 * 创建者：wxr
 * 创建时间：2022-05-07 11:31
 * 描述：
 */
class LoadMoreActivity : BaseActivity<ActivityLoadMoreBinding, BaseViewModel>() {

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            LogUtils.d("绑定成功2：$service")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            LogUtils.d("断开绑定2")
        }
    }

    override fun getViewBinding(): ActivityLoadMoreBinding =
        ActivityLoadMoreBinding.inflate(layoutInflater)

    override fun getViewModel(): Class<BaseViewModel>? = null

    override fun initCreate() {
        bindService(Intent(this, MyService::class.java), connection, Context.BIND_AUTO_CREATE)
        mBinding.refresh.setOnRefreshListener {
            ToastUtils.showToast("刷新了")
            it.postDelayed({
                it.finishRefresh()
            }, 1500)
        }
        mBinding.refresh.setOnLoadMoreListener {
            ToastUtils.showToast("加载了")
            it.postDelayed({
                it.finishLoadMore()
            }, 1500)
        }

        mBinding.refresh.post {
            mBinding.refresh.getDefaultHeaderView()?.isShowWave = false
        }

        setupRecyclerView(mBinding.recyclerview)

        LiveDataBus.with<Int>("a").observe(this) {
            LogUtils.d("第二页面：$it")
        }
    }

    override fun initEvent() {
        mBinding.autoRefresh.setOnClickListener {
            LiveDataBus.with<Int>("a").postValue(40)
            //mBinding.refresh.autoRefresh()

        }
        mBinding.autoLoadmore.setOnClickListener { mBinding.refresh.autoLoadMore() }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = PublicAdapter(mContext)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}