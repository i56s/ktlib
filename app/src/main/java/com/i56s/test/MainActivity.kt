package com.i56s.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.i56s.test.databinding.ViewHeaderBinding
import com.jcodecraeer.xrecyclerview.XRecyclerView

class MainActivity : BaseActivity<ActivityMainBinding>(), XRecyclerView.LoadingListener {

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private val mList = mutableListOf<Bean>()
    private lateinit var mAdapter: MyAdapter

    override fun initCreate() {
        for (i in 0..20) {
            mList.add(Bean("${i}测试"))
        }
        mAdapter = MyAdapter(mContext, mList)
        mAdapter.addHeaderView(ViewHeaderBinding.inflate(layoutInflater))
        mBinding.recycler.layoutManager = LinearLayoutManager(mContext)
        mBinding.recycler.adapter = mAdapter
        //mBinding.recycler.addHeaderView(ViewHeaderBinding.inflate(layoutInflater).root)
        //mBinding.recycler.addHeaderView(ViewHeaderBinding.inflate(layoutInflater).root)
        //mBinding.recycler.setFootView(ViewHeaderBinding.inflate(layoutInflater).root)
        mBinding.recycler.setFootViewText("加载中...","没有更多了亲")
    }

    override fun initEvent() {
        mAdapter.setOnItemClickListener { position, data ->
            data.count++
            LogUtils.d("测试", "点击了 p = $position ，count = ${data.count}")
            //mAdapter.notifyItemChanged(position+2)
            mBinding.recycler.notifyItemChanged(position)
            //mAdapter.notifyDataSetChanged()
        }
        mBinding.recycler.setLoadingListener(this)
        mBinding.itemCount.setOnClickListener {
            //LogUtils.d("测试", "总数 = ${mAdapter.itemCount}")
            startActivity(Intent(this,SecondActivity::class.java))
        }
    }

    override fun onRefresh() {
        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.recycler.refreshComplete()
        },2000)
    }

    override fun onLoadMore() {
        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.recycler.loadMoreComplete()
            //mBinding.recycler.setNoMore(true)
        },2000)
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

    class MyAdapter2 constructor(context: Context, list: MutableList<Bean>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val mCon = context
        private val mList = list
        private lateinit var binding: ItemTestBinding

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            object : RecyclerView.ViewHolder(
                ItemTestBinding.inflate(LayoutInflater.from(mCon)).also { binding = it }.root
            ) {}

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val data = mList[position]
            binding.itemName.text = "${data.name} -- ${data.count}"
            holder.itemView.setOnClickListener {
                data.count++
                LogUtils.d("测试", "点击了 p = $position ，count = ${data.count}")
                notifyItemChanged(position)
            }
        }

        override fun getItemCount(): Int = mList.size

    }

    data class Bean(val name: String, var count: Int = 0)
}