package com.csci5708.dalcommunity.model

import android.content.Context
import android.widget.Toast
import com.csci5708.dalcommunity.firestore.FireStoreSingleton
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.Exclude
import java.util.Calendar
import kotlin.math.roundToInt

/**
 * Represents a Poll Post, which is a type of post containing a poll question and options.
 * Inherits from the Post class.
 */
class PollPost : Post {
    override lateinit var postId: String
    override lateinit var userId: String
    override var type: Int = 2
    lateinit var pollQuestion: String
    lateinit var pollValuesList: List<PollValue>
    lateinit var userName: String
    lateinit var time: String
    override lateinit var comments: MutableList<Comment>
    @Exclude
    var isUserVoteComplete: Boolean = false
    var userToVoteMap: HashMap<String, String> = HashMap()

    constructor() : super() {
        // Default constructor required for Firebase deserialization
    }

    /**
     * Constructor to initialize PollPost object with provided parameters.
     * @param postId The ID of the post.
     * @param userId The ID of the user who created the post.
     * @param pollQuestion The question associated with the poll.
     * @param pollValuesList The list of poll options and their vote counts.
     * @param isUserVoteComplete Indicates if the current user has voted on this poll.
     * @param userName The name of the user who created the post.
     * @param userToVoteMap A map containing user IDs and their corresponding vote options.
     */
    constructor(
        postId: String,
        userId: String,
        pollQuestion: String,
        pollValuesList: List<PollValue>,
        isUserVoteComplete: Boolean,
        userName: String,
        userToVoteMap: HashMap<String, String>,
        comments: MutableList<Comment>
    ) : super() {
        this.postId = postId
        this.userId = userId
        this.pollQuestion = pollQuestion
        this.pollValuesList = pollValuesList
        this.isUserVoteComplete = isUserVoteComplete
        this.time = Calendar.getInstance().time.toString()
        this.userName = userName
        this.userToVoteMap = userToVoteMap
        this.comments = comments
        refreshPollData()
    }

    /**
     * Get the size of the poll values list.
     */
    val pollValuesSize: Int
        get() = pollValuesList.size

    /**
     * Update the vote for a particular option and refresh poll data.
     *
     * @param position The position of the poll value item that the user has clicked on.
     * @param context The context used to display toast messages.
     */
    fun updateVote(position: Int, context: Context) {
        pollValuesList[position].votes += 1
        isUserVoteComplete = true
        userToVoteMap[Firebase.auth.currentUser?.email.toString()] = pollValuesList[position].title

        // Set all the isSelected to false before uploading to Firestore
        for (pv in pollValuesList) {
            pv.isSelected = false
        }
        FireStoreSingleton.updateData("post", postId, this) {
            Toast.makeText(context, "Your vote was recorded.", Toast.LENGTH_SHORT).show()
        }

        refreshPollData()
    }

    /**
     * Refresh all the percentage votes and set values correctly so that it is displayed accurately
     * in the timeline
     */
    fun refreshPollData() {
        val totalVotes = pollValuesList.sumOf { it.votes }
        var percent: Float

        // Check if current logged in user has voted on the post
        if (userToVoteMap.containsKey(Firebase.auth.currentUser?.email.toString())) {
            val selectedOption = userToVoteMap[Firebase.auth.currentUser?.email.toString()]
            for (pv in pollValuesList) {
                if (pv.title == selectedOption) {
                    pv.isSelected = true
                }
            }
            isUserVoteComplete = true
        } else
            isUserVoteComplete = false

        if (totalVotes != 0) {
            for (pollVal in pollValuesList) {
                percent = (pollVal.votes / totalVotes.toFloat()) * 100
                pollVal.progress = percent.roundToInt()
                pollVal.percentage = "${pollVal.progress}%"
            }
        }
    }

    /**
     * Get the position of the voted option. Dummy method to indicate user has already voted.
     */
    fun getVotedOptionPosition(): Int {
        if (isUserVoteComplete) {
            return 1
        }
        return -1
    }
}