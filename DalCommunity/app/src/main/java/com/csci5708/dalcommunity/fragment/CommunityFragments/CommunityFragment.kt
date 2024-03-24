package com.csci5708.dalcommunity.fragment.CommunityFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.csci5708.dalcommunity.adapter.CommunityFragmentAdapter
import com.csci5708.dalcommunity.fragment.CommentFragment
import com.example.dalcommunity.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CommunityFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:View=inflater.inflate(R.layout.fragment_community,container,false)

        val viewPager: ViewPager2 = view.findViewById(R.id.communityVp)
        val tabLayout:TabLayout=view.findViewById(R.id.communityTabLayout)
        val createCommunityBt:ImageButton=view.findViewById(R.id.addCommunityBt)

        createCommunityBt.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext())

            val view = layoutInflater.inflate(R.layout.bottom_dialog_community, null)

            val saveBtn = view.findViewById<Button>(R.id.communitySaveBt)
            val closeBtn = view.findViewById<Button>(R.id.communityCloseBt)

            saveBtn.setOnClickListener {
                Toast.makeText(requireContext(),"Test Save",Toast.LENGTH_SHORT).show()
            }

            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(view)

            dialog.show()
        }

        viewPager.adapter=CommunityFragmentAdapter(requireActivity())
        TabLayoutMediator(tabLayout,viewPager){ tab,positon ->
            when(positon){
                0->tab.text = "Chats"
                1->tab.text = "Channels"
            }
        }.attach()

        return view
    }


}