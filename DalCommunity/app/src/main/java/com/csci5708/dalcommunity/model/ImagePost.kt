package com.csci5708.dalcommunity.model

class ImagePost(
    override var postId: String,
    override var userId: String,
    override var type: Int = 1,
    var imageUrl: String,
    var caption: String,
    var taggedUsers: List<String>,
    var latLocation: Double,
    var longLocation: Double) : Post() {
}