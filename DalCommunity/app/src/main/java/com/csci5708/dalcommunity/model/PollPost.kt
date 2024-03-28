package com.csci5708.dalcommunity.model

import kotlin.math.roundToInt

class PollPost : Post {
    override lateinit var postId: String
    override lateinit var userId: String
    override var type: Int = 2
    lateinit var pollQuestion: String
    lateinit var pollValuesList: List<PollValue>
    lateinit var userName: String
    lateinit var time: String
    var isUserVoteComplete: Boolean = false

    constructor() : super() {
        // Default constructor required for Firebase deserialization
    }

    constructor(
        postId: String,
        userId: String,
        type: Int,
        pollQuestion: String,
        pollValuesList: List<PollValue>,
        isUserVoteComplete: Boolean
    ) : super() {
        this.postId = postId
        this.userId = userId
        this.type = type
        this.pollQuestion = pollQuestion
        this.pollValuesList = pollValuesList
        this.isUserVoteComplete = isUserVoteComplete
        calculatePercentages()
    }

    val pollValuesSize: Int
        get() = pollValuesList.size

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