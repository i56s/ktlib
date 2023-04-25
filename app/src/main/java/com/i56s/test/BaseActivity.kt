package com.i56s.test

import androidx.viewbinding.ViewBinding
import com.i56s.ktlib.base.LibBaseActivity

/**
 * 创建者：wxr
 * 创建时间：2022-05-07 11:31
 * 描述：
 */
abstract class BaseActivity<B : ViewBinding,M :BaseViewModel> : LibBaseActivity<B,M>() {

    override fun isRemoveStatusBar(): Boolean = false

    override fun isOverstepStatusBar(): Boolean = true

    override fun onCreateBefore() = Unit

    override fun onCreateAfter() = Unit
}