package com.csci5708.dalcommunity.fragment.CommunityFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.csci5708.dalcommunity.adapter.CommunityFragmentAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.fragment.CommentFragment
import com.csci5708.dalcommunity.model.ChatMessage
import com.csci5708.dalcommunity.model.CommunityChannel
import com.example.dalcommunity.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class CommunityFragment : Fragment() {


    @SuppressLint("MissingInflatedId")
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

            val dialogview = layoutInflater.inflate(R.layout.bottom_dialog_community, null)

            val nameEditText:EditText=dialogview.findViewById(R.id.communityCreateNameEt)
            val descEditText:EditText=dialogview.findViewById(R.id.communityCreateDescEt)
            val ruleEditText:EditText=dialogview.findViewById(R.id.communityCreateRuleEt)

            val saveBtn = dialogview.findViewById<Button>(R.id.communitySaveBt)
            val closeBtn = dialogview.findViewById<Button>(R.id.communityCloseBt)

            saveBtn.setOnClickListener {
                val cname=nameEditText.text.toString()
                val desc=descEditText.text.toString()
                val rules=ruleEditText.text.toString()

                if(cname.isNotEmpty() && desc.isNotEmpty() && rules.isNotEmpty()){
                    createCommunityInFirestore(cname,rules,desc)
                    dialog.dismiss()
                }else{
                    Toast.makeText(requireContext(), "Please complete all fields", Toast.LENGTH_SHORT).show()
                }

            }

            closeBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(dialogview)

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

    private fun  createCommunityInFirestore(communityName:String,communityRules:String,communityDesc:String){
        val firestore = FirebaseFirestore.getInstance()
        val groupsRef = firestore.collection("community_groups")
        val currentUser = Firebase.auth.currentUser!!

        val groupId = groupsRef.document().id

        val groupData = hashMapOf(
            "name" to communityName,
            "rules" to communityRules,
            "desc" to communityRules,
            "lastMessage" to "",
            "lastMessageSenderEmail" to "",
            "lastMessageSenderName" to "",
            "lastMessageTime" to System.currentTimeMillis(),
            "messages" to emptyList<ChatMessage>(),
            "users" to hashMapOf(currentUser.email!! to "name")
        )

        val onComplete = { b: Boolean ->
            if (b) {
                Toast.makeText(requireContext(), "Community Channel created in Firestore!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "Failed to create Community Channel in Firestore!", Toast.LENGTH_LONG).show()
            }
        }

        FireStoreSingleton.addData(
            "community-groups",
            groupId,
            groupData,
            onComplete
        )
    }




}