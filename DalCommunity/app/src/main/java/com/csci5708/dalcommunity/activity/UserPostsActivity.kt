package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.csci5708.dalcommunity.adapter.UserPostsAdapter
import com.example.dalcommunity.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabView
import com.google.android.material.tabs.TabLayoutMediator

class UserPostsActivity : AppCompatActivity() {
    private val tabsArray = arrayOf("Posts", "Comments")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_posts)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val changePasswdToolbar: Toolbar = findViewById(R.id.user_posts_toolbar)
        setSupportActionBar(changePasswdToolbar)
        supportActionBar?.title = "My Posts"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val viewPager: ViewPager2 = findViewById(R.id.user_posts_view_pager)
        val tabLayout: TabLayout = findViewById(R.id.user_posts_tab_layout)

        val userPostsAdapter = UserPostsAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = userPostsAdapter

        TabLayoutMediator(tabLayout, viewPager) {
            tab, position -> tab.text = tabsArray[position]
        }.attach()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            finish()
        return super.onOptionsItemSelected(item)
    }
}