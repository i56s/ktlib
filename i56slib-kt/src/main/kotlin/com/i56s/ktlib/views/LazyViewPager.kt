package com.i56s.ktlib.views

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.i56s.ktlib.R
import com.i56s.ktlib.adapter.TabPagerAdapter

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-03-24 17:41
 * ### 描述：实现懒加载
 */
class LazyViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs) {

    private val defaultOffset = 0.5f

    private var mLazyPagerAdapter: TabPagerAdapter? = null

    /**懒加载偏移值(滑动多少开始加载)*/
    var lazyItemOffset = defaultOffset
        set(value) {
            field = if (value > 0 && value <= 1) value else defaultOffset
        }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LazyViewPager)
        lazyItemOffset =
            typedArray.getFloat(R.styleable.LazyViewPager_lazy_item_offset, defaultOffset)
        typedArray.recycle()
    }

    override fun setAdapter(adapter: PagerAdapter?) {
        super.setAdapter(adapter)
        if (adapter is TabPagerAdapter) {
            mLazyPagerAdapter = adapter
        }
    }

    override fun onPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        if (currentItem == position) {
            val lazyPosition = position + 1
            if (offset >= lazyItemOffset) {
                mLazyPagerAdapter?.addLazyItem(this, lazyPosition)
                mLazyPagerAdapter?.finishUpdate(this)
            }
        } else if (currentItem > position) {
            if (1 - offset >= lazyItemOffset) {
                mLazyPagerAdapter?.addLazyItem(this, position)
                mLazyPagerAdapter?.finishUpdate(this)
            }
        }
        super.onPageScrolled(position, offset, offsetPixels)
    }
}