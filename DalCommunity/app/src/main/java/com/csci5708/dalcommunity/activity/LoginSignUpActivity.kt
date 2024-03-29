package com.csci5708.dalcommunity.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.csci5708.dalcommunity.adapter.LoginSIgnupViewPagerAdapter
import com.csci5708.dalcommunity.constants.AppConstants
import com.csci5708.dalcommunity.fragment.LoginFragment
import com.csci5708.dalcommunity.fragment.SignUpFragment
import com.example.dalcommunity.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
/**
 * This class handles the functionality related to the login and sign up activities.
 */
class LoginSignUpActivity : AppCompatActivity() {
    /**
     * Initializes the activity and sets up necessary UI components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_sign_up)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val sharedPreferences = getSharedPreferences(AppConstants.APP_SHARED_PREFERENCES, MODE_PRIVATE)
        if (sharedPreferences.getBoolean(AppConstants.SP_IS_SIGNED_IN_KEY, false)) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        val tabLayout: TabLayout = findViewById(R.id.loginSignupLayout)

        val adapter = LoginSIgnupViewPagerAdapter(this)
        adapter.addFragment(LoginFragment(), "Login")
        adapter.addFragment(SignUpFragment(), "Sign Up")
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTab = layoutInflater.inflate(R.layout.custom_tab, null) as TextView
            customTab.text = adapter.getTitle(position)
            tab.customView = customTab
        }.attach()
    }
}
