package com.csci5708.dalcommunity.fragment.petitionsfragments

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
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
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.csci5708.dalcommunity.adapter.PetitionAdapter
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.csci5708.dalcommunity.model.Petition
import com.example.dalcommunity.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A Fragment to display a list of petitions and their details.
 * Handles fetching petitions from Firestore, displaying petition details in a dialog, and signing petitions.
 */
open class ViewPetitionFragment : Fragment(), PetitionAdapter.OnItemClickListener {

    var provideContext: (() -> Context)? = null

    // Method to get the context
    fun getContextOfView(): Context {
        return provideContext?.invoke() ?: throw IllegalStateException("Context not set")
    }

    // RecyclerView to display petitions
    lateinit var recyclerView: RecyclerView

    // Adapter for the RecyclerView
    lateinit var petitionAdapter: PetitionAdapter

    // List of petitions to display
    val petitions: MutableList<Petition> = mutableListOf()

    /**
     * Inflates the layout for this fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return Returns the root view of the inflated layout
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_view_petition, container, false)
        recyclerView = view.findViewById(R.id.petitionViewRecyclerViewOfView)
        return view
    }

    /**
     * Called immediately after onCreateView() has returned, and allows you to start
     * interacting with the fragment's view hierarchy.
     * @param view The View returned by onCreateView()
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        petitionAdapter = PetitionAdapter(petitions, this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = petitionAdapter
        }
    }

    /**
     * Fetches petitions from Firestore and updates the UI.
     */
    fun fetchPetitions() {
        petitions.clear()
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        FireStoreSingleton.getAllDocumentsOfCollection("petitions",
            { documents ->
                for (document in documents) {
                    val petition = document.toObject(Petition::class.java)
                    if (petition != null) {
                        petition.id = document.id
                    }
                    if (petition != null) {
                        if (currentUser != null) {
                            petitions.add(petition)
                        }
                    }
                }
                petitions.sortByDescending { it.creation_date }
                petitionAdapter.notifyDataSetChanged()
            },
            { exception ->
                Toast.makeText(requireContext(), "Failed to load petitions: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    /**
     * Handles the click event on a petition item.
     * @param petition The petition item clicked
     */
    override fun onItemClick(petition: Petition) {
        showDialogWithPetitionDetails(petition)
    }

    /**
     * Displays a dialog showing details of the given petition.
     * Allows users to sign the petition.
     * @param petition The petition to display details for
     */
    fun showDialogWithPetitionDetails(petition: Petition) {
        val dialogView = layoutInflater.inflate(R.layout.view_petition, null)
        val titleOfPetition = dialogView.findViewById<TextView>(R.id.viewPetitionTitle)
        val descriptionOfPetition = dialogView.findViewById<TextView>(R.id.viewDescriptionPetition)
        val numberOfPetition = dialogView.findViewById<TextView>(R.id.viewPetitionSignedNumber)
        val imageOfPetition = dialogView.findViewById<ImageView>(R.id.viewImagePetition)
        val close = dialogView.findViewById<RelativeLayout>(R.id.close_icon)
        val checkboxSignIn = dialogView.findViewById<CheckBox>(R.id.petitionSignIn)
        val signInPetitionBtn = dialogView.findViewById<Button>(R.id.signInPetitionBtn)
        signInPetitionBtn.visibility = View.GONE
        checkboxSignIn.setOnCheckedChangeListener { _, isChecked ->
            signInPetitionBtn.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
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
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialog.window?.attributes)
        layoutParams.width = width
        dialog.window?.attributes = layoutParams
        close.setOnClickListener {
            dialog.dismiss()
        }
        signInPetitionBtn.setOnClickListener {
            val auth = Firebase.auth
            val currentUser = auth.currentUser
            val currentUserEmail = currentUser?.email
            val firestore = FirebaseFirestore.getInstance()
            if (currentUserEmail != null) {
                FireStoreSingleton.getAllDocumentsOfCollection("petitions",
                    onSuccess = { documents ->
                        var petitionDoc: DocumentSnapshot? = null
                        for (document in documents) {
                            if (document.id == petition.id) {
                                petitionDoc = document
                                break
                            }
                        }
                        if (petitionDoc != null) {
                            val signedUsers: MutableList<String> = (petitionDoc.get("signed_user") as? List<String>)?.toMutableList() ?: mutableListOf()
                            if (!signedUsers.contains(currentUserEmail)) {
                                signedUsers.add(currentUserEmail)
                                petitionDoc.id.let { petitionId ->
                                    firestore.collection("petitions").document(petitionId)
                                        .update(mapOf(
                                            "signed_user" to signedUsers,
                                            "number_signed" to signedUsers.size
                                        ))
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Signed petition successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e(TAG, "Error updating signed_user field: $e")
                                            Toast.makeText(context, "Failed to sign petition. Please try again later.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                Toast.makeText(context, "You have already signed this petition.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Petition not found.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Error fetching petition document: $exception")
                        Toast.makeText(context, "Failed to fetch petition. Please try again later.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "User not authenticated. Please sign in.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}
