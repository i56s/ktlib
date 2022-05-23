package com.i56s.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.base.BaseRecyclerAdapter
import com.i56s.ktlib.base.LibBaseActivity
import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.databinding.ItemTestBinding
import com.i56s.test.databinding.ViewHeaderBinding
import com.jcodecraeer.xrecyclerview.XRecyclerView

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun initCreate() {
        mBinding.refresh.setMaterialRefreshListener {

            onRefresh = {
                Toast.makeText(this@MainActivity, "下拉刷新", Toast.LENGTH_SHORT).show()
                mBinding.refresh.postDelayed({
                    mBinding.refresh.finishRefresh()
                }, 1500)
            }

            onLoadMore = {
                Toast.makeText(this@MainActivity, "加载更多", Toast.LENGTH_SHORT).show()
                mBinding.refresh.postDelayed({
                    mBinding.refresh.finishLoadMore()
                }, 1500)
            }
        }
        mBinding.refresh.post {
            mBinding.refresh.getDefaultHeaderView()?.isShowWave = false
        }

        //mBinding.recyclerview.layoutManager = LinearLayoutManager(mBinding.recyclerview.context)
        //mBinding.recyclerview.adapter = PublicAdapter(mContext)
        //mBinding.recyclerview.itemAnimator = DefaultItemAnimator()
    }

    override fun initEvent() {

    }
}