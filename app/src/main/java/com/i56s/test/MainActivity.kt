package com.i56s.test

import android.widget.Toast
import com.i56s.test.databinding.ActivityMainBinding

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