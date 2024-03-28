package com.csci5708.dalcommunity.model

import com.google.firebase.firestore.Exclude

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

    constructor(title: String, votes: Int) {
        this.title = title
        this.votes = votes
    }
}