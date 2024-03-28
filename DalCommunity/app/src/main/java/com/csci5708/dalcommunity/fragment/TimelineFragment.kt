package com.csci5708.dalcommunity.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.activity.CreatePostActivity
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.PollPost
import com.csci5708.dalcommunity.model.PollValue
import com.csci5708.dalcommunity.model.Post
import com.csci5708.dalcommunity.model.TextPost
import com.example.dalcommunity.R
import com.google.android.material.floatingactionbutton.FloatingActionButton


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TimelineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimelineFragment : Fragment(), HomeAdapter.OnImageInItemClickListener, FragmentManager.OnBackStackChangedListener {

    // Fragment initialization parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onResume() {
        super.onResume()

        // Make FloatingActionButton visible when fragment is resumed
        view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve fragment arguments
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onBackStackChanged() {
        // Check if back stack is empty
        val backStackEmpty = activity?.supportFragmentManager?.backStackEntryCount == 0
        // Make FloatingActionButton visible when back stack is empty
        if (backStackEmpty) {
            view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Register back stack listener
        activity?.supportFragmentManager?.addOnBackStackChangedListener(this)
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_timeline, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val addPostButton = view.findViewById<FloatingActionButton>(R.id.create_post_fab)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val posts = mutableListOf<Post>()

        // Retrieve posts from Firestore
        FireStoreSingleton.getAllDocumentsOfCollection("post", {
                documents ->
            for (document in documents) {
                if (document.get("type") == 0L) {
                    val post = document.toObject(TextPost::class.java)
                    posts.add(post as TextPost)
                } else if (document.get("type") == 1L) {
                    val post = document.toObject(ImagePost::class.java)
                    posts.add(post as ImagePost)
                } else if (document.get("type") == 2L) {
                    val post = document.toObject(PollPost::class.java)
                    posts.add(post as PollPost)
                }
            }
            val adapter = HomeAdapter(requireContext(), posts)
            adapter.setOnImageInItemClickListener(this)
            recyclerView.adapter = adapter
        }, {
            Log.e("TAG", it.toString())
        })

        // Handle click event for add post button
        addPostButton.setOnClickListener {
            val intent = Intent(activity, CreatePostActivity::class.java)
            startActivity(intent)
        }

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TimelineFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    // Handle click event for comment button
    override fun onCommentClick(position: Int) {
        view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.INVISIBLE
        Toast.makeText(activity,"test", Toast.LENGTH_LONG).show()
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.setCustomAnimations(R.anim.slide_in_top_comment, R.anim.slide_out_down_comment)
        fragmentTransaction?.replace(R.id.fragment_container, CommentFragment())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

    // Handle click event for report button
    override fun onReportClick(position: Int) {
        view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.INVISIBLE
        Toast.makeText(activity,"Report Clicked",Toast.LENGTH_LONG).show()
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.setCustomAnimations(R.anim.slide_in_top_comment, R.anim.slide_out_down_comment)
        fragmentTransaction?.replace(R.id.fragment_container, ReportFragment())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister back stack listener
        activity?.supportFragmentManager?.removeOnBackStackChangedListener(this)
    }
}