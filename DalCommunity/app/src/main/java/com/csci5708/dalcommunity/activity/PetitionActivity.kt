package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.csci5708.dalcommunity.adapter.PetitionPagerAdapter
import com.csci5708.dalcommunity.fragment.petitionsfragments.TrackPetitionFragment
import com.csci5708.dalcommunity.fragment.petitionsfragments.ViewPetitionFragment
import com.example.dalcommunity.R
import com.google.android.material.tabs.TabLayout
/**
 * Activity for managing petitions, including creation, viewing, and tracking.
 */
class PetitionActivity : AppCompatActivity() {

    lateinit var petitionImage: ImageView
    private lateinit var petitionTitleEditText: EditText
    private lateinit var petitionDescEditText: EditText
    private lateinit var petitionImageView: ImageView
    private val PREF_NAME = "user_details"
    private val KEY_USER_ID = "user_id"

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }
    /**
     * Initializes the activity and sets up necessary UI components and listeners.
     * This function configures the layout, TabLayout, ViewPager, and their interactions.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_petition)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        val pagerAdapter = PetitionPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter

        tabLayout.setupWithViewPager(viewPager)

        // Set custom views for each tab
        tabLayout.getTabAt(0)?.customView = createTabView("Create Petition", true) // Selecting the first tab
        tabLayout.getTabAt(1)?.customView = createTabView("View All Petitions", false)
        tabLayout.getTabAt(2)?.customView = createTabView("Track Petition", false)

        // Add listener for tab selection changes
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                setTabActive(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                setTabInactive(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if (position == 1) {
                    val fragment = pagerAdapter.instantiateItem(viewPager, position) as? ViewPetitionFragment
                    fragment?.fetchPetitions()
                }
                if (position == 2) {
                    val fragment = pagerAdapter.instantiateItem(viewPager, position) as? TrackPetitionFragment
                    fragment?.fetchPetitions()
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }
    /**
     * Sets the appearance of the active tab.
     */
    private fun setTabActive(tab: TabLayout.Tab) {
        val tabTextView = tab.customView?.findViewById<TextView>(R.id.tab_text)
        tabTextView?.setTextColor(ContextCompat.getColor(this, R.color.text_color))
    }
    /**
     * Sets the appearance of the inactive tab.
     */
    private fun setTabInactive(tab: TabLayout.Tab) {
        val tabTextView = tab.customView?.findViewById<TextView>(R.id.tab_text)
        tabTextView?.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    /**
     * Creates a custom view for a tab.
     */
    private fun createTabView(tabTitle: String, isSelected: Boolean): View {
        val tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
        val tabTextView = tabView.findViewById<TextView>(R.id.tab_text)
        tabTextView.text = tabTitle
        if (isSelected) {
            tabTextView.setTextColor(ContextCompat.getColor(this, R.color.text_color))
        } else {
            tabTextView.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        return tabView
    }
}
