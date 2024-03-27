package com.csci5708.dalcommunity.fragment

import android.content.Intent
import android.os.Bundle
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
import com.csci5708.dalcommunity.model.ImagePost
import com.csci5708.dalcommunity.model.PollPost
import com.csci5708.dalcommunity.model.PollValue
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

    private var param1: String? = null
    private var param2: String? = null

    override fun onResume() {
        super.onResume()
        view?.findViewById<FloatingActionButton>(R.id.create_post_fab)?.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        activity?.supportFragmentManager?.addOnBackStackChangedListener(this)
        val view = inflater.inflate(R.layout.fragment_timeline, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val addPostButton = view.findViewById<FloatingActionButton>(R.id.create_post_fab)

        recyclerView.layoutManager = LinearLayoutManager(activity)

        val posts = listOf(
            TextPost("", "", 0, "", listOf("", ""), 1.1, 1.1),
            ImagePost("", "", 1, "", "", listOf("", ""), 1.1, 1.1),
            ImagePost("", "", 1, "", "", listOf("", ""), 1.1, 1.1),
            PollPost("", "", 2,
                "What is 2+2?",
                arrayListOf(
                    PollValue("4", 75, false),
                    PollValue("6", 20, false),
                    PollValue("2", 2, false),
                    PollValue("0", 3, true)
                ),
                false
            ),
            TextPost("", "", 0, "", listOf("", ""), 1.1, 1.1),
            TextPost("", "", 0, "", listOf("", ""), 1.1, 1.1)
        )

        val adapter = HomeAdapter(requireContext(), posts)
        adapter.setOnImageInItemClickListener(this)
        recyclerView.adapter = adapter

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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TimelineFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

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
        activity?.supportFragmentManager?.removeOnBackStackChangedListener(this)
    }
}