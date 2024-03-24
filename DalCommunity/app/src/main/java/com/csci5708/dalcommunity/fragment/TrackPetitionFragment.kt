package com.csci5708.dalcommunity.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csci5708.dalcommunity.adapter.PetitionAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Petition
import com.example.dalcommunity.R


class TrackPetitionFragment : Fragment(), PetitionAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var petitionAdapter: PetitionAdapter
    private val petitions: MutableList<Petition> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track_petition, container, false)
        recyclerView = view.findViewById(R.id.petitionRecyclerView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        petitionAdapter = PetitionAdapter(petitions, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = petitionAdapter
        }
    }

    private fun fetchPetitions() {
        petitions.clear()
        FireStoreSingleton.getAllDocumentsOfCollection("petitions",
            { documents ->
                for (document in documents) {
                    val petition = document.toObject(Petition::class.java)
                    if (petition != null) {
                        petition.id = document.id
                    }
                    if (petition != null) {
                        petitions.add(petition)
                    }
                }
                petitionAdapter.notifyDataSetChanged()
            },
            { exception ->
                Toast.makeText(requireContext(), "Failed to load petitions: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
    override fun onItemClick(petition: Petition) {
        showDialogWithPetitionDetails(petition)
    }
    private fun showDialogWithPetitionDetails(petition: Petition) {
        val dialogView = layoutInflater.inflate(R.layout.view_petition, null)
        val titleOfPetition = dialogView.findViewById<TextView>(R.id.viewPetitionTitle)
        val descriptionOfPetition = dialogView.findViewById<TextView>(R.id.viewDescriptionPetition)
        val numberOfPetition = dialogView.findViewById<TextView>(R.id.viewPetitionSignedNumber)
        val imageOfPetition = dialogView.findViewById<ImageView>(R.id.viewImagePetition)
        val close = dialogView.findViewById<RelativeLayout>(R.id.close_icon)

        titleOfPetition.text = petition.title
        descriptionOfPetition.text = petition.description
        numberOfPetition.text = "Total Signatures: "+petition.number_signed.toString()
        if (petition.imgUrl != "") {
            Glide.with(requireContext())
                .load(petition.imgUrl)
                .into(imageOfPetition)
        } else {
            imageOfPetition.visibility = View.GONE
        }

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)
//        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = width
        dialog.window?.attributes = layoutParams


        close.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    override fun onResume() {
        super.onResume()
        fetchPetitions()
    }
}