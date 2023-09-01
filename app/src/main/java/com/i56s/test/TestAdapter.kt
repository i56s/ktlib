package com.i56s.test

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.base.BaseRecyclerAdapter
import com.i56s.test.databinding.ItemTestBinding

/**
 * 创建者： wxr
 * 创建时间： 2023-09-01 15:28
 * 描述：
 */
class TestAdapter(context: Context, datas: MutableList<String>) :
    BaseRecyclerAdapter<String>(context, datas) {
    override fun onCreate(layoutInflater: LayoutInflater,
                          parent: ViewGroup,
                          viewType: Int): ViewBinding = ItemTestBinding.inflate(layoutInflater)

    override fun onBind(holder: RecyclerView.ViewHolder,
                        binding: ViewBinding,
                        position: Int,
                        data: String) {
        binding as ItemTestBinding
        binding.itemName.text = data
    }
}