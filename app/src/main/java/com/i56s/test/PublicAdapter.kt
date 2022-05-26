package com.i56s.test

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.base.BaseRecyclerAdapter
import com.i56s.ktlib.utils.LogUtils
import com.i56s.test.databinding.ListItemBinding

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-05-13 15:13
 * ### 描述：公共的适配器
 */
class PublicAdapter constructor(context: Context) :
    BaseRecyclerAdapter<Any>(context, mutableListOf()) {

    var count = 4

    override fun getItemCount(): Int = count

    override fun onCreate(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewBinding = ListItemBinding.inflate(layoutInflater).apply {
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onBind(
        holder: RecyclerView.ViewHolder,
        binding: ViewBinding,
        position: Int,
        data: Any
    ) {
        binding as ListItemBinding
        if (position % 2 == 0) {
            binding.avatar.setImageResource(R.drawable.a6)
        } else {
            binding.avatar.setImageResource(R.drawable.a5)
        }
    }
}