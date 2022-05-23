package com.i56s.test

import androidx.recyclerview.widget.LinearLayoutManager
import com.i56s.ktlib.utils.ToastUtils
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
        mBinding.recycler.recyclerView.layoutManager = LinearLayoutManager(mContext)
        mBinding.recycler.recyclerView.adapter = PublicAdapter(mContext)
    }

    override fun initEvent() {
        mBinding.recycler.setMaterialRefreshListener {
            onRefresh = {
                ToastUtils.showToast("刷新了")
                it.postDelayed({
                    it.finishRefresh()
                }, 1500)
            }
            onLoadMore = {
                ToastUtils.showToast("加载了")
                it.postDelayed({
                    it.finishLoadMore()
                }, 1500)
            }
        }
    }
}