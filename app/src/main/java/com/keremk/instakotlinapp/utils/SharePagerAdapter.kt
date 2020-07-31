package com.keremk.instakotlinapp.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SharePagerAdapter(fragmentManager: FragmentManager,tabAdlari:ArrayList<String>) : FragmentPagerAdapter(fragmentManager) {
    private var mFragmentList: ArrayList<Fragment> = ArrayList()
    private var mTabAdlari: ArrayList<String> = tabAdlari
    override fun getItem(position: Int): Fragment {
        return mFragmentList.get(position)
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return mTabAdlari.get(position)
    }
}