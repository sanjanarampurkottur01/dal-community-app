package com.csci5708.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.Petition
import com.example.dalcommunity.R

/**
 * Adapter for displaying petitions in a RecyclerView.
 * @param petitions The list of petitions to display
 * @param itemClickListener The click listener interface for handling item clicks
 */
class PetitionAdapter(
    private val petitions: List<Petition>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PetitionAdapter.PetitionViewHolder>() {

    /**
     * Interface for handling item clicks in the RecyclerView.
     */
    interface OnItemClickListener {
        /**
         * Called when a petition item is clicked.
         * @param petition The petition that was clicked
         */
        fun onItemClick(petition: Petition)
    }

    /**
     * ViewHolder class for a petition item.
     */
    inner class PetitionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.pokeToUserMessage)
        val signedTextView: TextView = itemView.findViewById(R.id.textViewSigned)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(petitions[position])
                }
            }
        }
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The type of the new View
     * @return Returns a new PetitionViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetitionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_petition, parent, false)
        return PetitionViewHolder(itemView)
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param holder The PetitionViewHolder
     * @param position The position of the item within the adapter's data set
     */
    override fun onBindViewHolder(holder: PetitionViewHolder, position: Int) {
        val currentPetition = petitions[position]
        holder.titleTextView.text = currentPetition.title
        holder.signedTextView.text = "Signed: ${currentPetition.number_signed}"
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return Returns the total number of petitions
     */
    override fun getItemCount(): Int {
        return petitions.size
    }
}
