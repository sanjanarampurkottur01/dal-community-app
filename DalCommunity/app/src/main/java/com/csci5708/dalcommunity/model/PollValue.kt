package com.csci5708.dalcommunity.model

class PollValue {
    var title: String = ""
    var votes: Int = 0
    var isSelected: Boolean = false
    var percentage: String = ""
    var progress: Int = 0

    constructor()

    constructor(title: String, votes: Int, isSelected: Boolean) {
        this.title = title
        this.votes = votes
        this.isSelected = isSelected
    }
}