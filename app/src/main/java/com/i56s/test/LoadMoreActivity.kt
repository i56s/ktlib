package com.i56s.test

import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i56s.ktlib.utils.ToastUtils
import com.i56s.ktlib.views.xrefresh.MaterialLoaderView
import com.i56s.test.databinding.ActivityLoadMoreBinding

/**
 * 创建者：wxr
 * 创建时间：2022-05-07 11:31
 * 描述：
 */
class LoadMoreActivity : BaseActivity<ActivityLoadMoreBinding, BaseViewModel>() {

    override fun getViewBinding(): ActivityLoadMoreBinding =
        ActivityLoadMoreBinding.inflate(layoutInflater)

    override fun getViewModel(): Class<BaseViewModel>? = null

    override fun initCreate() {
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