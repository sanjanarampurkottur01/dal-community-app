package com.example.dalcommunity.fragment


import android.view.View
import android.os.Bundle
import android.view.ViewGroup
import android.text.TextWatcher
import com.example.dalcommunity.R
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.animation.ObjectAnimator
import android.text.Editable
import com.example.dalcommunity.model.Comment
import androidx.recyclerview.widget.RecyclerView
import com.example.dalcommunity.adapter.CommentAdapter
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText


/**
 * A fragment representing comments.
 */
class CommentFragment : Fragment() {
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

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewComments)
        val comments: List<Comment> = listOf(Comment(1, "test"), Comment(2, "test123"))
        val adapter = CommentAdapter(comments)
        recyclerView.adapter = adapter
        val editText = view.findViewById<EditText>(R.id.comment_text_box)
        val button = view.findViewById<Button>(R.id.button_id)

        // Initially, hide the button
        button.visibility = View.GONE

        // Add TextWatcher to listen for text changes
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not needed
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check if EditText has text, if yes, show the button, else hide it
                if (s.isNullOrEmpty()) {
                    button.visibility = View.GONE
                } else {
                    button.visibility = View.VISIBLE
                }
            }
        })
    }
    override fun onResume() {
        super.onResume()
        animateFragment()
    }
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
