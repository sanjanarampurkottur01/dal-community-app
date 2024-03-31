package com.csci5708.dalcommunity.model

class ImagePost : Post {
    override var postId: String = ""
    override var userId: String = ""
    override var type: Int = 1
    var userName: String = ""
    var time: String = ""
    var imageUrl: String = ""
    var caption: String = ""
    var taggedUsers: List<String> = listOf()
    var latLocation: Double = 0.0
    var longLocation: Double = 0.0
    var place: String = ""

    constructor()

    constructor(
        postId: String,
        userId: String,
        type: Int,
        userName: String,
        time: String,
        imageUrl: String,
        caption: String,
        taggedUsers: List<String>,
        latLocation: Double,
        longLocation: Double,
        place: String
    ) {
        this.postId = postId
        this.userId = userId
        this.type = type
        this.userName = userName
        this.time = time
        this.imageUrl = imageUrl
        this.caption = caption
        this.taggedUsers = taggedUsers
        this.latLocation = latLocation
        this.longLocation = longLocation
        this.place = place
    }
}