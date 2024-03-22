package com.csci5708.dalcommunity.model

import kotlin.math.roundToInt

class PollPost(val pollQuestion: String, val pollValuesList: List<PollValue>, var isUserVoteComplete: Boolean = false) {
    val pollValuesSize: Int = pollValuesList.size

    init {
        calculatePercentages()
    }

    fun updateVote(position: Int) {
        pollValuesList[position].votes += 1
        isUserVoteComplete = true
        calculatePercentages()
    }

    fun calculatePercentages() {
        val totalVotes = pollValuesList.sumOf { it.votes }
        var percent: Float

        if (totalVotes != 0) {
            for (pollVal in pollValuesList) {
                percent = (pollVal.votes / totalVotes.toFloat()) * 100
                pollVal.progress = percent.roundToInt()
                pollVal.percentage = "${pollVal.progress}%"
            }
        }
    }
}