package com.example.meohaji.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.meohaji.home.HomeFragment
import com.example.meohaji.mypage.MyPageFragment
import com.example.meohaji.search.SearchFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    val mypage = MyPageFragment()
    private val fragmentList = listOf(
        SearchFragment(),
        HomeFragment(),
        mypage
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun notifyChangeData() {
        mypage.checkSharedPreference()
    }
}