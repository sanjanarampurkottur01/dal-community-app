package com.csci5708.dalcommunity.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.activity.AddStudentIDActivity
import com.csci5708.dalcommunity.activity.CreatePostActivity
import com.csci5708.dalcommunity.adapter.HomeAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.PollPost
import com.csci5708.dalcommunity.model.Post
import com.csci5708.dalcommunity.model.TextPost
import com.example.dalcommunity.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val USER_EMAIL_PARAM = "USER_EMAIL"

/**
 * A simple [Fragment] subclass.
 * Use the [TimelineFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TimelineFragment : Fragment(), HomeAdapter.OnImageInItemClickListener,
    FragmentManager.OnBackStackChangedListener {

    // Fragment initialization parameters
    var userEmail: String? = null
    private lateinit var homeAdapter: HomeAdapter
    private lateinit var recyclerView: RecyclerView

    // Declare posts as a property of the class
    private var posts = mutableListOf<Post>()

    override fun onResume() {
        super.onResume()

        // Make FloatingActionButton visible when fragment is resumed
        view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve fragment arguments
        arguments?.let {
            userEmail = it.getString(USER_EMAIL_PARAM)
        }
    }

    override fun onBackStackChanged() {
        // Check if back stack is empty
        val backStackEmpty = activity?.supportFragmentManager?.backStackEntryCount == 0
        // Make FloatingActionButton visible when back stack is empty
        if (backStackEmpty) {
            view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility =
                View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Register back stack listener
        activity?.supportFragmentManager?.addOnBackStackChangedListener(this)
        // Inflate the layout for this fragment
        val view: View = if (userEmail != null)
            inflater.inflate(R.layout.fragment_timeline_user, container, false)
        else
            inflater.inflate(R.layout.fragment_timeline, container, false)
        val addPostButton = view.findViewById<FloatingActionButton>(R.id.create_post_fab)

        if (userEmail != null) {
            addPostButton.visibility = View.GONE
        }

        if (userEmail == null) {
            /* NOTE: Set student ID icon listener only in the case where this fragment is created
             * for the HomeActivity.
             */
            val addStudentIdIcon = view.findViewById<ImageView>(R.id.add_student_id_icon)
            addStudentIdIcon.setOnClickListener {
                startActivity(Intent(activity, AddStudentIDActivity::class.java))
            }
        }

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        // Fetch all the posts from Firestore and load it if successfully fetched
        getAllPosts()

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
         * @param userEmail display posts created by given user email ID
         * @return A new instance of fragment TimelineFragment.
         */
        @JvmStatic
        fun newInstance(userEmail: String) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_EMAIL_PARAM, userEmail)
                }
            }
    }

    // Handle click event for comment button
    override fun onCommentClick(position: Int) {
        view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.INVISIBLE

        val postId = posts[position].postId

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.setCustomAnimations(
            R.anim.slide_in_top_comment,
            R.anim.slide_out_down_comment
        )

        // Pass the posts list to the CommentFragment
        val commentFragment = CommentFragment()
        val bundle = Bundle().apply {
            putString("postId", postId)
        }
        commentFragment.arguments = bundle

        fragmentTransaction?.replace(R.id.fragment_container, commentFragment)
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

    // Handle click event for report button
    override fun onReportClick(position: Int) {
        view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.INVISIBLE
        Toast.makeText(activity, "Report Clicked", Toast.LENGTH_LONG).show()
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        fragmentTransaction?.setCustomAnimations(
            R.anim.slide_in_top_comment,
            R.anim.slide_out_down_comment
        )
        fragmentTransaction?.replace(R.id.fragment_container, ReportFragment())
        fragmentTransaction?.addToBackStack(null)
        fragmentTransaction?.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister back stack listener
        activity?.supportFragmentManager?.removeOnBackStackChangedListener(this)
    }

    private fun getAllPosts() {
        if (userEmail == null) {
            // Retrieve posts from Firestore
            FireStoreSingleton.getAllDocumentsOfCollection("post", { documents ->
                getAllPostsOnSuccess(documents)
            }, {
                Log.e("TIMELINE_FRAGMENT", it.toString())
            })
        } else {
            // Retrieve user specific posts
            FireStoreSingleton.getAllDocumentsOfCollectionWhereEqualTo("post",
                "userId",
                Firebase.auth.currentUser?.email.toString(),
                { documents ->
                    getAllPostsOnSuccess(documents)
                }, {
                    Log.e("TIMELINE_FRAGMENT", it.toString())
                })
        }
    }

    private fun getAllPostsOnSuccess(documents: List<DocumentSnapshot>) {
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
        // Initialize homeAdapter only after posts are fetched
        homeAdapter = HomeAdapter(requireContext(), posts)
        homeAdapter.setOnImageInItemClickListener(this)
        recyclerView.adapter = homeAdapter
    }
}
