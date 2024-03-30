package com.csci5708.dalcommunity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.SearchAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.User
import com.example.dalcommunity.R

class SearchFragment: Fragment(),SearchAdapter.OnClickListener {
    private lateinit var resultList:MutableList<User>
    private lateinit var searchUsers: SearchView
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        searchUsers = view.findViewById(R.id.searchUsers)
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView)
        searchAdapter = SearchAdapter(this)
        usersRecyclerView.adapter=searchAdapter
        usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        usersRecyclerView.setHasFixedSize(true)
        searchUsers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getUsers()

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getUsers()

                return false
            }
        })
        getUsers()

        return view;
    }

    private fun getUsers() {
        FireStoreSingleton.getAllDocumentsOfCollection("users",
            { documents ->
                resultList = mutableListOf<User>()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    //Toast.makeText(requireContext(),userMap.id,Toast.LENGTH_SHORT).show()
                    resultList.add(user!!)
                }
                val query  = searchUsers.query
                val searchString = if (query.isNullOrEmpty()) "" else query.toString()
                if(!searchString.isNullOrEmpty()) {
                    searchAdapter.submitList(resultList.filter { item ->
                        if(item.name.isNullOrEmpty()) {
                            false
                        }
                        else {
                            (item.name.replace(" ", "").lowercase()
                                .contains(searchString.replace(" ", "").lowercase())
                                    )
                        }
                    })
                }
                else {
                    searchAdapter.submitList(resultList)
                }
            },
            { exception ->
                Toast.makeText(requireContext(), "Failed to load: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onClickItem(user: User) {
        val userDetailsFragment = UserDetailsFragment()
        val bundle = Bundle()
        bundle.putSerializable("EXTRA_USER", user)
        userDetailsFragment.arguments = bundle

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.home_fragment_container, userDetailsFragment)
            addToBackStack(null) // Add this line if you want to add the transaction to the back stack
            commit()

        }
    }
}