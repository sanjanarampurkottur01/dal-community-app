package com.csci5708.dalcommunity.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
/**
 * Adapter for managing fragments in the ViewPager for login and sign up.
 * @param activity The FragmentActivity hosting this adapter.
 */
class LoginSIgnupViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val fragments: MutableList<Fragment> = ArrayList()
    private val titles: MutableList<String> = ArrayList()
    /**
     * Returns the number of fragments managed by the adapter.
     */
    override fun getItemCount(): Int = fragments.size
    /**
     * Creates a new fragment instance based on its position.
     * @param position The position of the fragment.
     * @return The created fragment.
     */
    override fun createFragment(position: Int): Fragment = fragments[position]
    /**
     * Adds a new fragment to the adapter.
     * @param fragment The fragment to add.
     * @param title The title associated with the fragment.
     */
    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }
    /**
     * Adds a new fragment to the adapter.
     * @param fragment The fragment to add.
     * @param title The title associated with the fragment.
     */
    fun getTitle(position: Int): String = titles[position]
}
