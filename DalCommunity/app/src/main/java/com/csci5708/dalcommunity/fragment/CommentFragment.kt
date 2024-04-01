package com.csci5708.dalcommunity.fragment

import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.text.TextWatcher
import com.example.dalcommunity.R
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.animation.ObjectAnimator
import android.text.Editable
import android.util.Log
import com.csci5708.dalcommunity.model.Comment
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.adapter.CommentAdapter
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot


/**
 * A fragment representing comments.
 */
class CommentFragment : Fragment() {

    private lateinit var commentsList: RecyclerView
    private lateinit var commentEditText: EditText
    private lateinit var postCommentBtn: Button
    private lateinit var postId: String

    val comments: MutableList<Comment> = mutableListOf()
    val adapter = CommentAdapter(comments)

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * as given here.
     * @return The root view of the inflated layout.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_comment, container, false)
    }
    /**
     * Called immediately after onCreateView(LayoutInflater, ViewGroup, Bundle) has returned, but before any
     * saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle).
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * as given here.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentsList = view.findViewById(R.id.recyclerViewComments)

        commentsList.adapter = adapter
        commentsList.layoutManager = LinearLayoutManager(activity)

        commentEditText = view.findViewById(R.id.comment_text_box)
        postCommentBtn = view.findViewById(R.id.button_id)

        // Initially, hide the button
        postCommentBtn.visibility = View.GONE

        arguments?.let {
            postId = it.getString("postId", "")
            FireStoreSingleton.getData("post", postId, {post ->
                displayListOfComments(post)
            }, {})
        }

        // Add TextWatcher to listen for text changes
        commentEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // To establish default behaviour
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // To establish default behaviour
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if EditText has text, if yes, show the button, else hide it
                if (s.isNullOrEmpty()) {
                    postCommentBtn.visibility = View.GONE
                } else {
                    postCommentBtn.visibility = View.VISIBLE
                }
            }
        })

        postCommentBtn.setOnClickListener {
            comments.add(Comment(Firebase.auth.currentUser?.email.toString(), commentEditText.text.toString()))
            adapter.notifyDataSetChanged()
            commentEditText.text.clear()

            FireStoreSingleton.updateDataField("post", postId, "comments", comments, {})
        }
    }

    private fun displayListOfComments(post: DocumentSnapshot) {
        val commentsData = post.get("comments") as? List<Map<String, Any>> // Retrieve the list of comments data

        commentsData?.forEach { commentData ->
            val commentText = commentData["comment"] as? String
            val userId = commentData["userId"] as? String

            // Create a Comment object and add it to the list
            commentText?.let { text ->
                userId?.let { user ->
                    val comment = Comment(user, text)
                    comments.add(comment)
                }
            }
        }

        // Now you have the list of Comment objects
        adapter.notifyDataSetChanged()
    }

    /**
     * Called when the fragment resumes.
     * Responsible for animating the fragment.
     */
    override fun onResume() {
        super.onResume()
        animateFragment()
    }
    /**
     * Animates the fragment by translating it from bottom to top.
     */
    private fun animateFragment() {
        view?.let {
            ObjectAnimator.ofFloat(it, "translationY", it.height.toFloat(), 0f).apply {
                duration = 500
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }
    }
}
