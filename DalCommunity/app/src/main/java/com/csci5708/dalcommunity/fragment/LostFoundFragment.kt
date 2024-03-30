package com.csci5708.dalcommunity.fragment

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.LostAndFoundAdapter
import com.csci5708.dalcommunity.adapter.LostAndFoundPostsAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.example.dalcommunity.R
import com.example.dalcommunity.UserMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LostFoundFragment: Fragment(),LostAndFoundPostsAdapter.OnClickListener {

    private lateinit var listingButton:AppCompatButton
    private lateinit var userPostListingRecyclerView: RecyclerView
    private lateinit var lostAndFoundPostsAdapter: LostAndFoundPostsAdapter
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var searchItems: SearchView
    private lateinit var resultList:MutableList<UserMap>
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lost_found, container, false)
        firebaseFirestore = FirebaseFirestore.getInstance()
        getUserListings()
        resultList = mutableListOf<UserMap>()
        searchItems = view.findViewById(R.id.searchItems)
        spinner = view.findViewById(R.id.spinner)

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dropdown_items, // This is the string array we defined earlier
            android.R.layout.simple_spinner_item // This is the layout for each dropdown item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Use position to know the selected item
                val selectedItem = parent.getItemAtPosition(position).toString()
                val selectedValue = when (selectedItem) {
                    "All Items" -> ""
                    "Lost Items" -> "lost"
                    else -> "found"
                }
                // Use selectedValue as needed
                if(searchItems.query.toString().isNullOrEmpty()) {
                    lostAndFoundPostsAdapter.submitList(resultList.filter { item ->
                        item.category.contains(
                            selectedValue
                        )
                    })
                }
                else {
                    val searchString  = searchItems.query.toString()
                    lostAndFoundPostsAdapter.submitList(resultList.filter { item ->
                        item.category.contains(
                            selectedValue
                        ) && (item.itemName.lowercase().contains(searchString.lowercase()) ||
                        item.description.lowercase().contains(searchString.lowercase()))
                    })
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }

        searchItems.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Perform final search when search button is pressed
                // For example, querying a database or filtering a data list
                val selectedItem = spinner.selectedItem.toString()
                val selectedValue = when (selectedItem) {
                    "All Items" -> ""
                    "Lost Items" -> "lost"
                    else -> "found"
                }
                val searchString = searchItems.query.toString()
                lostAndFoundPostsAdapter.submitList(resultList.filter {
                    item -> (item.itemName.lowercase().contains(searchString.lowercase()) ||
                        item.description.lowercase().contains(searchString.lowercase()))
                        && (item.category.equals(selectedValue))
                })

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val isCleared = newText.isNullOrEmpty()
                val selectedItem = spinner.selectedItem.toString()
                val selectedValue = when (selectedItem) {
                    "All Items" -> ""
                    "Lost Items" -> "lost"
                    else -> "found"
                }

                if (isCleared) {
                    // Handle the case when the search view is cleared
                    // For example, reset the adapter to show all items
                    lostAndFoundPostsAdapter.submitList(resultList.filter{item->item.category.contains(selectedValue)})
                }
                else {
                    // Perform live search as the user types
                    // For example, filtering a RecyclerView adapter as the query text changes
                    val searchString = newText.toString()
                    lostAndFoundPostsAdapter.submitList(resultList.filter {
                            item -> (item.itemName.lowercase().contains(searchString.lowercase()) ||
                            item.description.lowercase().contains(searchString.lowercase()))
                            && (item.category.contains(selectedValue))
                    })
                }

                return false
            }
        })

        listingButton = view.findViewById(R.id.listingButton)
        userPostListingRecyclerView = view.findViewById(R.id.userPostListingRecyclerView)
        lostAndFoundPostsAdapter = LostAndFoundPostsAdapter(this)
        userPostListingRecyclerView.adapter = lostAndFoundPostsAdapter
        userPostListingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        userPostListingRecyclerView.setHasFixedSize(true)


        listingButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.home_fragment_container, LostFoundUserListingFragment())
                addToBackStack(null) // Add this line if you want to add the transaction to the back stack
                commit()
            }
        }

        parentFragmentManager.setFragmentResultListener("result", this) { _, bundle ->
            // Handle the result. For example, you could update UI or initiate some action based on 'result'.
        }
        return view;
    }

    private fun getUserListings() {
        val collectionRef = firebaseFirestore.collection("lostAndFound")

        collectionRef
            .orderBy("dateTime", Query.Direction.DESCENDING) // Or Query.Direction.ASCENDING for ascending order
            .get()
            .addOnSuccessListener { documents ->
                // Iterate through the documents
                resultList = mutableListOf<UserMap>()
                for (document in documents) {
                    // Access document data here
                    val userMap = document.toObject(UserMap::class.java)
                    userMap.id = document.id
                    //Toast.makeText(requireContext(),userMap.id,Toast.LENGTH_SHORT).show()
                    resultList.add(userMap)
                    // Process document data as needed
                }
                val query  = searchItems.query
                val searchString = if (query.isNullOrEmpty()) "" else query.toString()
                val selectedItem = spinner.selectedItem.toString()
                val selectedValue = when (selectedItem) {
                    "All Items" -> ""
                    "Lost Items" -> "lost"
                    else -> "found"
                }

                lostAndFoundPostsAdapter.submitList(resultList.filter { item ->
                    item.category.contains(
                        selectedValue
                    ) && (item.itemName.lowercase().contains(searchString.lowercase()) ||
                            item.description.lowercase().contains(searchString.lowercase()))
                })
//                textView.text = resultList.toString()
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e("Lost", "Error getting data: ", exception)
                Toast.makeText(requireContext(),exception.toString(), Toast.LENGTH_LONG).show()
            }
    }

    override fun onClickItem(userMap: UserMap) {
        val mapFragment = MapsFragment()
        val bundle = Bundle()
        bundle.putSerializable("EXTRA_USER_MAP", userMap)
        mapFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.home_fragment_container, mapFragment)
            addToBackStack(null) // Add this line if you want to add the transaction to the back stack
            commit()

        }
    }

    override fun onResume() {

        // Check if imageUri is not null and imageView is initialized
        //getUserListings()

        //lostAndFoundPostsAdapter.submitList(resultList)
        super.onResume()
    }
}