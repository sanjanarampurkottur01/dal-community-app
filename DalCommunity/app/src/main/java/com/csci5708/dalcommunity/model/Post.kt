package com.csci5708.dalcommunity.model

abstract class Post {
    abstract val postId: String
    abstract val userId: String
    abstract val type: Int
    open var comments: MutableList<Comment> = mutableListOf()
}