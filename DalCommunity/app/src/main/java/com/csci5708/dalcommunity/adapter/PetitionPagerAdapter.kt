package com.csci5708.dalcommunity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.csci5708.dalcommunity.fragment.petitionfragments.CreatePetitionFragment
import com.csci5708.dalcommunity.fragment.petitionfragments.TrackPetitionFragment
import com.csci5708.dalcommunity.fragment.petitionfragments.ViewPetitionFragment

class PetitionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CreatePetitionFragment()
            1 -> ViewPetitionFragment()
            2 -> TrackPetitionFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getCount(): Int {
        return 3
    }
}
