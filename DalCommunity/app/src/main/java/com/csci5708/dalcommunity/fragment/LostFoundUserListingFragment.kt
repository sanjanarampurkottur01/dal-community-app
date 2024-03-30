package com.csci5708.dalcommunity.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.LostAndFoundAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.example.dalcommunity.R
import com.example.dalcommunity.UserMap
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso

class LostFoundUserListingFragment : Fragment(), LostAndFoundAdapter.OnClickListener {
    private lateinit var addListingButton: AppCompatButton
    private var email = "";
    private lateinit var firebaseFirestore: FirebaseFirestore
//    private lateinit var textView:TextView
    private lateinit var userListingRecyclerView: RecyclerView
    private lateinit var lostAndFoundAdapter: LostAndFoundAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lost_found_user_listing, container, false)
        addListingButton = view.findViewById(R.id.addListingButton)
//        textView = view.findViewById(R.id.logText)
        firebaseFirestore = FirebaseFirestore.getInstance()
        userListingRecyclerView = view.findViewById(R.id.userListingRecyclerView)
        lostAndFoundAdapter = LostAndFoundAdapter(this)
        userListingRecyclerView.adapter = lostAndFoundAdapter
        userListingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userListingRecyclerView.setHasFixedSize(true)
        addListingButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.home_fragment_container, LostFoundItemFragment())
                addToBackStack(null) // Add this line if you want to add the transaction to the back stack
                commit()
            }
        }
        email = Firebase.auth.currentUser?.email.toString()
        getUserListings()
        parentFragmentManager.setFragmentResultListener("itemModified", this) { _, bundle ->

            // Handle the result. For example, you could update UI or initiate some action based on 'result'.
        }
        return view;
    }

    private fun getUserListings() {
        val collectionRef = firebaseFirestore.collection("lostAndFound")

// Query the collection, filtering by email and ordering by the date field
        collectionRef.whereEqualTo("email", email)
             .orderBy("dateTime", Query.Direction.DESCENDING) // Or Query.Direction.ASCENDING for ascending order
            .get()
            .addOnSuccessListener { documents ->
                // Iterate through the documents
                val resultList = mutableListOf<UserMap>()
                for (document in documents) {
                    // Access document data here
                    val userMap = document.toObject(UserMap::class.java)
                    userMap.id = document.id
                    //Toast.makeText(requireContext(),userMap.id,Toast.LENGTH_SHORT).show()
                    resultList.add(userMap)
                    // Process document data as needed
                }
                lostAndFoundAdapter.submitList(resultList)
//                textView.text = resultList.toString()
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Lost", "Error getting documents: ", exception)
               Toast.makeText(requireContext(),exception.toString(),Toast.LENGTH_LONG).show()
            }
    }

    override fun onClickItem(userMap: UserMap) {
        val lostFoundItemFragment = LostFoundItemFragment()
        val bundle = Bundle()
        bundle.putSerializable("EXTRA_USER_ITEM", userMap)
        lostFoundItemFragment.arguments = bundle
        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.home_fragment_container, lostFoundItemFragment)
            addToBackStack(null) // Add this line if you want to add the transaction to the back stack
            commit()

        }
    }

    override fun showImageDialog(userMap:UserMap) {
        val imageDialog = Dialog(requireContext())
        imageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        imageDialog.setContentView(R.layout.image_dialog_layout)

        val image = imageDialog.findViewById<ImageView>(R.id.imageViewDialog)
        Picasso.get().load(userMap.imageUri).into(image)

        // Making the dialog full-screen
        val window = imageDialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Adding click listener to close the dialog
        image.setOnClickListener {
            imageDialog.dismiss()
        }

        imageDialog.show()
    }
}