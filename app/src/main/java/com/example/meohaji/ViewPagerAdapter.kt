package com.example.meohaji

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.meohaji.fragment.HomeFragment
import com.example.meohaji.fragment.MyPageFragment
import com.example.meohaji.fragment.SearchFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private val fragmentList = listOf(
        SearchFragment(),
        HomeFragment(),
        MyPageFragment()
    )

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment = fragmentList[position]

}