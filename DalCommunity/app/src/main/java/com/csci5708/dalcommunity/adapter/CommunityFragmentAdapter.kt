package com.csci5708.dalcommunity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.csci5708.dalcommunity.fragment.CommunityFragments.CommunityChatFragment
import com.csci5708.dalcommunity.fragment.CommunityFragments.CommunityListFragment


class CommunityFragmentAdapter(val fa:FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        return when (position){
            0 -> CommunityChatFragment()
            else -> CommunityListFragment()
        }
    }

}