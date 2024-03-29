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
import com.example.dalcommunity.R
import com.google.android.material.tabs.TabLayout
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
        tabLayout.getTabAt(0)?.customView = createTabView("Create Petition")
        tabLayout.getTabAt(1)?.customView = createTabView("View All Petitions")
        tabLayout.getTabAt(2)?.customView = createTabView("Track Petition")

    }
    private fun createTabView(tabTitle: String): View {
        val tabView = LayoutInflater.from(this).inflate(R.layout.custom_tab, null)
        val tabTextView = tabView.findViewById<TextView>(R.id.tab_text)
        tabTextView.text = tabTitle
        return tabView
    }
}
