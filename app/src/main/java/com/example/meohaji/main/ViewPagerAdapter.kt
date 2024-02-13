package com.example.meohaji.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.meohaji.home.HomeFragment
import com.example.meohaji.mypage.MyPageFragment
import com.example.meohaji.search.SearchFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val search = SearchFragment()
    private val home = HomeFragment()
    private val myPage = MyPageFragment()

    private val fragmentList = listOf(
        search,
        home,
        myPage
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment = fragmentList[position]

    fun notifyChangeData() {
        myPage.checkSharedPreference()
    }
}