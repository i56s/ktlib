package com.i56s.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.i56s.ktlib.views.xrefresh.MaterialRefreshLayout
import com.i56s.ktlib.views.xrefresh.MaterialRefreshListener
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
        mBinding.refresh.setMaterialRefreshListener(object : MaterialRefreshListener() {

            override fun onRefresh(materialRefreshLayout: MaterialRefreshLayout?) {
                Toast.makeText(this@LoadMoreActivity, "下拉刷新", Toast.LENGTH_SHORT).show()
                mBinding.refresh.postDelayed({
                    mBinding.refresh.finishRefresh()
                }, 1500)
            }

            override fun onLoadMore(materialRefreshLayout: MaterialRefreshLayout?) {
                Toast.makeText(this@LoadMoreActivity, "加载更多", Toast.LENGTH_SHORT).show()
                mBinding.refresh.postDelayed({
                    mBinding.refresh.finishLoadMore()
                }, 1500)
            }
        })

        setupRecyclerView(mBinding.recyclerview)
    }

    override fun initEvent() {
        mBinding.autoRefresh.setOnClickListener { mBinding.refresh.autoRefresh() }
        mBinding.autoLoadmore.setOnClickListener { mBinding.refresh.autoLoadMore() }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
        recyclerView.adapter = SimpleStringRecyclerViewAdapter()
        recyclerView.itemAnimator = DefaultItemAnimator()
    }

    class SimpleStringRecyclerViewAdapter constructor() :
        RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {


        class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

            var mImageView: ImageView = view.findViewById(R.id.avatar)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == 0) {
                holder.mImageView.setImageResource(R.drawable.a6)
            } else if (position == 1) {
                holder.mImageView.setImageResource(R.drawable.a5)
            }
        }

        override fun getItemCount(): Int = 4
    }
}