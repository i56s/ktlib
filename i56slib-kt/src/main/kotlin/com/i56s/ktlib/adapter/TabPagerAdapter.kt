package com.i56s.ktlib.adapter

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter

/**
 * ### 创建者：wxr
 * ### 创建时间：2022-03-24 16:53
 * ### 描述：TabLayout+ViewPager滑动适配器
 * 修复动态添加不刷新
 */
class TabPagerAdapter constructor(fm: FragmentManager, list: List<Bean>? = null) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var mCurrentItem: Fragment? = null

    private val mLazyItems: SparseArray<Fragment> = SparseArray()
    private val mList = mutableListOf<Bean>()
    private var mCurTransaction: FragmentTransaction? = null
    private val mFragmentManager: FragmentManager = fm

    init {
        list?.let(mList::addAll)
    }

    override fun startUpdate(container: ViewGroup) {}

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Do we already have this fragment?
        val name = makeFragmentName(container.id, position)
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

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        val name = makeFragmentName(container.id, position)
        if (mFragmentManager.findFragmentByTag(name) == null) {
            mCurTransaction?.remove(any as Fragment)
        } else {
            mLazyItems.remove(position)
        }
    }

    /**添加懒加载*/
    fun addLazyItem(container: ViewGroup, position: Int): Fragment? {
        val fragment = mLazyItems.get(position) ?: return null
        val name = makeFragmentName(container.id, position)
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

    override fun isViewFromObject(view: View, any: Any): Boolean = (any as Fragment).view == view

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    override fun getItem(position: Int): Fragment = mList[position].fragment

    override fun getCount(): Int = mList.size

    override fun getPageTitle(position: Int): CharSequence? = mList[position].title

    override fun setPrimaryItem(container: ViewGroup, position: Int, any: Any) {
        mCurrentItem = addLazyItem(container, position)
    }

    private fun makeFragmentName(viewId: Int, id: Int): String = "android:switcher:$viewId:$id"

    /** 添加数据 */
    fun addBean(title: String, fragment: Fragment) = addBean(Bean(title, fragment))

    /** 添加数据 */
    fun addBean(bean: Bean) = mList.add(bean)

    /** 清空数据 */
    fun clear() {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = mFragmentManager.beginTransaction()
        }
        mList.forEach { bean ->
            mCurTransaction?.remove(bean.fragment)
        }
        mCurTransaction?.commitNowAllowingStateLoss()
        mList.clear()
    }

    fun addFragment(fragment: Fragment) = mList.add(Bean(null, fragment))

    data class Bean(val title: String?, val fragment: Fragment)
}