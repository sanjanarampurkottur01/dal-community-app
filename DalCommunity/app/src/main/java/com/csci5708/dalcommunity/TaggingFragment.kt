package com.csci5708.dalcommunity

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

class TaggingFragment: Fragment(), SearchAdapter.OnClickListener {
    private lateinit var resultList: MutableList<User>
    private lateinit var searchUsers: SearchView
    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var searchAdapter: SearchAdapter

    private var userSelectedListener: OnUserSelectedListener? = null

    var data: String = ""

    companion object {
        private const val ARG_DATA = "DATA"

        fun newInstance(listener: OnUserSelectedListener, data: Bundle?): TaggingFragment {
            val fragment = TaggingFragment()
            fragment.arguments = Bundle().apply {
                putBundle(ARG_DATA, data)
            }
            fragment.setUserSelectedListener(listener)
            return fragment
        }
    }

    fun setUserSelectedListener(listener: OnUserSelectedListener) {
        userSelectedListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        data = arguments?.getBundle(ARG_DATA)!!.getString("activity")!!
        searchUsers = view.findViewById(R.id.searchUsers)
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView)
        searchAdapter = SearchAdapter(this)
        usersRecyclerView.adapter = searchAdapter
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

    /**
     * Retrieves all users from the "users" collection in the Firestore database.
     *
     * @return A list of User objects representing the retrieved users.
     */
    private fun getUsers() {
        FireStoreSingleton.getAllDocumentsOfCollection("users",
            { documents ->
                resultList = mutableListOf<User>()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    //Toast.makeText(requireContext(),userMap.id,Toast.LENGTH_SHORT).show()
                    resultList.add(user!!)
                }
                val query = searchUsers.query
                val searchString = if (query.isNullOrEmpty()) "" else query.toString()
                if (!searchString.isNullOrEmpty()) {
                    searchAdapter.submitList(resultList.filter { item ->
                        if (item.name.isNullOrEmpty()) {
                            false
                        } else {
                            (item.name.replace(" ", "").lowercase()
                                .contains(searchString.replace(" ", "").lowercase())
                                    )
                        }
                    })
                } else {
                    searchAdapter.submitList(resultList)
                }
            },
            { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    interface OnUserSelectedListener {
        fun onUserSelected(user: User)
    }

    /**
     * Handles the click event when an item is clicked in the user list.
     *
     * @param user The user object that was clicked.
     */
    override fun onClickItem(user: User) {
        userSelectedListener?.onUserSelected(user)
    }
}