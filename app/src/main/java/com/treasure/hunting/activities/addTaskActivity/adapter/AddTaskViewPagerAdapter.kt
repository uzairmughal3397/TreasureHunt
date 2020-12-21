package com.treasure.hunting.activities.addTaskActivity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlin.collections.ArrayList

class AddTaskViewPagerAdapter(fragmentActivity: FragmentActivity):FragmentStateAdapter(
    fragmentActivity){

    private val mFragmentList: ArrayList<Fragment> = ArrayList()
    private val mFragmentTitleList: ArrayList<String> = ArrayList()

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
            return mFragmentList[position]
    }

    fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }
    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
}