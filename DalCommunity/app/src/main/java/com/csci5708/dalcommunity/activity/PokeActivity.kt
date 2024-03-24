package com.csci5708.dalcommunity.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.UsersAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class PokeActivity : AppCompatActivity(), UsersAdapter.OnItemClickListener {
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var originalUsers: List<Pair<String, String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_poke)
        window.statusBarColor = ContextCompat.getColor(this, R.color.background)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view_users)
        recyclerView.layoutManager = LinearLayoutManager(this)
        usersAdapter = UsersAdapter(emptyList(), this)
        fetchAllUsers(usersAdapter)
        recyclerView.adapter = usersAdapter
        val searchView: SearchView = findViewById(R.id.search_view)
        searchView.queryHint = "Search by name"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterUsers(query, usersAdapter)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                    fetchAllUsers(usersAdapter)
                } else {
                    val query = newText.trim()
                    filterUsers(query, usersAdapter)
                }
                return true
            }
        })
    }

    override fun onItemClick(userName: String, userId: String) {
        showUserInfoDialog(userName, userId)
    }

    private fun showUserInfoDialog(userName: String, userId: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("User Information")
        alertDialogBuilder.setMessage("Name: $userName\nID: $userId")
        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun fetchAllUsers(usersAdapter: UsersAdapter) {
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        FireStoreSingleton.getAllDocumentsOfCollection("users",
            { documents ->
                val users = mutableListOf<Pair<String, String>>()
                for (document in documents) {
                    val name = document.getString("name") ?: "Unknown"
                    val id = document.id
                    val email = document.getString("email") ?: "unknown"
                    if (currentUser != null) {
                        if(currentUser.email != email){
                            users.add(Pair(name, id))
                        }
                    }
                }
                originalUsers = users
                usersAdapter.users = users
                usersAdapter.notifyDataSetChanged()
            },
            { exception ->
                Toast.makeText(this, "Failed to load users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun filterUsers(query: String, usersAdapter: UsersAdapter) {
        if (query.isBlank()) {
            usersAdapter.users = originalUsers
            usersAdapter.notifyDataSetChanged()
            return
        }

        val filteredUsers = originalUsers.filter { user ->
            user.first.contains(query, ignoreCase = true) // Filter based on name
        }
        usersAdapter.users = filteredUsers
        usersAdapter.notifyDataSetChanged()
    }
}