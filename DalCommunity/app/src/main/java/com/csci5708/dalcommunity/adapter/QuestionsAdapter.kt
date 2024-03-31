package com.csci5708.dalcommunity.adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.csci5708.dalcommunity.model.Question
import com.csci5708.dalcommunity.util.BroadcastQuestionsSharedValues
import com.example.dalcommunity.R

class QuestionsAdapter(private val context : Context, private val questions: List<Question>) :
    RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.broadcast_question_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.questionAsked.text = questions[position].questionString
        holder.itemView.setOnClickListener{
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.dialog_background))
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.see_responses_dialog)
            dialog.findViewById<TextView>(R.id.statement).text = questions[position].questionString
            val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerView)
            val emptyAnimation = dialog.findViewById<LottieAnimationView>(R.id.emptyAnimation)

            if (BroadcastQuestionsSharedValues.questions.isEmpty()) {
                emptyAnimation.visibility = View.VISIBLE
            } else {
                emptyAnimation.visibility = View.GONE
                recyclerView.layoutManager = LinearLayoutManager(context)
                val adapter = BroadcastAnswersAdapter(context, questions[position].answers)
                recyclerView.adapter = adapter
            }
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionAsked: TextView = itemView.findViewById(R.id.questionAsked)
    }
}