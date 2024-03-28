package com.csci5708.dalcommunity.model

class TextPost(
    override var postId: String,
    override var userId: String,
    override val type: Int = 0,
    var userName: String,
    var time: String,
    var caption: String,
    var taggedUsers: List<String>,
    var latLocation: Double,
    var longLocation: Double
): Post() {
}