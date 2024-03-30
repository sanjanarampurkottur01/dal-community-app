package com.csci5708.dalcommunity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.csci5708.dalcommunity.fragment.petitionsfragments.CreatePetitionFragment
import com.csci5708.dalcommunity.fragment.petitionsfragments.TrackPetitionFragment
import com.csci5708.dalcommunity.fragment.petitionsfragments.ViewPetitionFragment

/**
 * A PagerAdapter for managing fragments in a ViewPager to handle different petition-related views.
 * Manages the creation of fragments for creating, viewing, and tracking petitions.
 * @param fm The FragmentManager to handle fragments
 */
class PetitionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    /**
     * Returns the fragment for the specified position.
     * @param position The position of the fragment in the adapter
     * @return Returns the fragment for the specified position
     * @throws IllegalArgumentException if an invalid position is provided
     */
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> CreatePetitionFragment()
            1 -> ViewPetitionFragment()
            2 -> TrackPetitionFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    /**
     * Returns the total number of fragments managed by the adapter.
     * @return Returns the total number of fragments
     */
    override fun getCount(): Int {
        return 3
    }
}

