package com.i56s.test

import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i56s.ktlib.views.xrefresh.MaterialLoaderView
import com.i56s.test.databinding.ActivityLoadMoreBinding

/**
 * 创建者：wxr
 * 创建时间：2022-05-07 11:31
 * 描述：
 */
class LoadMoreActivity : BaseActivity<ActivityLoadMoreBinding>() {

    override fun getViewBinding(): ActivityLoadMoreBinding =
        ActivityLoadMoreBinding.inflate(layoutInflater)

    override fun initCreate() {
        /*mBinding.refresh.setMaterialRefreshListener {

            onRefresh = {
                Toast.makeText(this@LoadMoreActivity, "下拉刷新", Toast.LENGTH_SHORT).show()
                mBinding.refresh.postDelayed({
                    mBinding.refresh.finishRefresh()
                }, 1500)
            }

            onLoadMore = {
                Toast.makeText(this@LoadMoreActivity, "加载更多", Toast.LENGTH_SHORT).show()
                mBinding.refresh.postDelayed({
                    mBinding.refresh.finishLoadMore()
                }, 1500)
            }
        }*/
        mBinding.refresh.post {
            mBinding.refresh.getDefaultHeaderView()?.isShowWave = false
        }

        setupRecyclerView(mBinding.recyclerview)
    }

    override fun initEvent() {
        mBinding.autoRefresh.setOnClickListener { mBinding.refresh.autoRefresh() }
        mBinding.autoLoadmore.setOnClickListener { mBinding.refresh.autoLoadMore() }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = PublicAdapter(mContext)
        recyclerView.itemAnimator = DefaultItemAnimator()
    }
}