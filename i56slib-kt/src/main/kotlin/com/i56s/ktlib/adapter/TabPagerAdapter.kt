package com.i56s.ktlib.adapter

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-03-24 16:53
 * ### 描述：TabLayout+ViewPager滑动适配器
 */
class TabPagerAdapter constructor(fm: FragmentManager, list: List<Bean>?) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var mCurrentItem: Fragment? = null

    private val mLazyItems: SparseArray<Fragment> = SparseArray()
    private val mList: MutableList<Bean> = mutableListOf()
    private var mCurTransaction: FragmentTransaction? = null

    private val mFragmentManager: FragmentManager = fm

    constructor(fm: FragmentManager) : this(fm, null)

    init {
        if (list != null) mList.addAll(list)
    }

    override fun startUpdate(container: ViewGroup) {}

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }

        val itemId = getItemId(position)

        // Do we already have this fragment?
        val name = makeFragmentName(container.id, itemId)
        var fragment: Fragment? = mFragmentManager.findFragmentByTag(name)
        if (fragment == null) {
            fragment = this.getItem(position)
            mLazyItems.put(position, fragment)
        }
        if (fragment != mCurrentItem) {
            fragment.setMenuVisibility(false)
            fragment.userVisibleHint = false
        }
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction()
        }
        val itemId = getItemId(position)
        val name = makeFragmentName(container.id, itemId)
        if (mFragmentManager.findFragmentByTag(name) == null) {
            mCurTransaction?.remove(`object` as Fragment)
        } else {
            mLazyItems.remove(position)
        }
    }

    /**添加懒加载*/
    fun addLazyItem(container: ViewGroup, position: Int): Fragment? {
        val fragment = mLazyItems.get(position) ?: return null

        val itemId = getItemId(position)
        val name = makeFragmentName(container.id, itemId)
        if (mFragmentManager.findFragmentByTag(name) == null) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction()
            }
            mCurTransaction?.add(container.id, fragment, name)
            mLazyItems.remove(position)
        }
        return fragment
    }

    override fun finishUpdate(container: ViewGroup) {
        mCurTransaction?.commitAllowingStateLoss()
        mCurTransaction = null
        mFragmentManager.executePendingTransactions()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean =
        (`object` as Fragment).view == view

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getItem(position: Int): Fragment = mList[position].fragment

    override fun getCount(): Int = mList.size

    override fun getPageTitle(position: Int): CharSequence = mList[position].title

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        mCurrentItem = addLazyItem(container, position)
    }

    private fun makeFragmentName(viewId: Int, id: Long): String = "android:switcher:$viewId:$id"

    /** 添加数据 */
    fun addBean(title: String, fragment: Fragment) = mList.add(Bean(title, fragment))

    data class Bean(val title: String, val fragment: Fragment)
}