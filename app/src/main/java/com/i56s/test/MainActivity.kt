package com.i56s.test

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.base.BaseRecyclerAdapter
import com.i56s.ktlib.base.LibBaseActivity
import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.databinding.ActivityMainBinding
import com.i56s.test.databinding.ItemTestBinding
import com.jcodecraeer.xrecyclerview.XRecyclerView

class MainActivity : LibBaseActivity<ActivityMainBinding>(), XRecyclerView.LoadingListener {

    override fun isRemoveStatusBar(): Boolean = false

    override fun isOverstepStatusBar(): Boolean = false

    override fun onCreateBefore() = Unit

    override fun onCreateAfter() = Unit

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private val mList = mutableListOf<Bean>()
    private lateinit var mAdapter: MyAdapter

    override fun initCreate() {
        for (i in 0..5) {
            mList.add(Bean("${i}测试"))
        }
        mAdapter = MyAdapter(mContext, mList)
        mBinding.recycler.emptyView?.visibility = View.GONE
        mBinding.recycler.layoutManager = LinearLayoutManager(mContext)
        mBinding.recycler.adapter = mAdapter
    }

    override fun initEvent() {
        mAdapter.setOnItemClickListener { position, data ->
            data.count++
            LogUtils.d("测试", "点击了 p = $position ，count = ${data.count}")
            mAdapter.notifyItemChanged(position)
            //mAdapter.notifyDataSetChanged()
        }
        mBinding.recycler.setLoadingListener(this)
        mBinding.itemCount.setOnClickListener {
            LogUtils.d("测试", "总数 = ${mAdapter.itemCount}")
        }
    }

    override fun onRefresh() {
    }

    override fun onLoadMore() {
    }

    class MyAdapter constructor(context: Context, list: MutableList<Bean>) :
        BaseRecyclerAdapter<Bean>(context, list) {
        override fun onCreate(
            layoutInflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ): ViewBinding = ItemTestBinding.inflate(layoutInflater).also {
            it.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        override fun onBind(
            holder: RecyclerView.ViewHolder,
            binding: ViewBinding,
            position: Int,
            data: Bean
        ) {
            binding as ItemTestBinding
            binding.itemName.text = "${data.name} -- ${data.count}"
        }
    }

    data class Bean(val name: String, var count: Int = 0)
}