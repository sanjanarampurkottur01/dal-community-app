package com.csci5708.dalcommunity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.csci5708.dalcommunity.model.Petition
import com.example.dalcommunity.R

class PetitionAdapter(
    private val petitions: List<Petition>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<PetitionAdapter.PetitionViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(petition: Petition)
    }

    inner class PetitionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.petitionTitle)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetitionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_petition, parent, false)
        return PetitionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PetitionViewHolder, position: Int) {
        val currentPetition = petitions[position]
        holder.titleTextView.text = currentPetition.title
        holder.signedTextView.text = "Signed: ${currentPetition.number_signed}"
    }

    override fun getItemCount(): Int {
        return petitions.size
    }
}