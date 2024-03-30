package com.csci5708.dalcommunity.model

import com.google.firebase.firestore.Exclude

/**
 * Represents a single option in a poll.
 */
class PollValue {
    var title: String = ""
    var votes: Int = 0
    @Exclude
    var isSelected: Boolean = false
    @Exclude
    var percentage: String = ""
    @Exclude
    var progress: Int = 0

    constructor()

    /**
     * Constructor to initialize PollValue object with provided parameters.
     *
     * @param title The title of the poll option.
     * @param votes The number of votes received by this option.
     */
    constructor(title: String, votes: Int) {
        this.title = title
        this.votes = votes
    }
}