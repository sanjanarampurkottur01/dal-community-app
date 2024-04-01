package com.csci5708.dalcommunity.model

class Comment {
    var userId: String = ""
    var comment: String = ""

    constructor()

    constructor(
        userId: String,
        comment: String
    ) {
        this.userId = userId
        this.comment = comment
    }
}