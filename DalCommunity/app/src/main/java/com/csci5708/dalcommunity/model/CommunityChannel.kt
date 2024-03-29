package com.csci5708.dalcommunity.model

data class CommunityChannel(
    val id:String,
    val name: String,
    val rules: String,
    val desc: String,
    val lastMessage: String,
    val lastMessageSenderEmail: String,
    val lastMessageSenderName: String,
    val lastMessageTime: Long,
    val messages: List<ChatMessage>,
    val users: HashMap<String,String>
)
