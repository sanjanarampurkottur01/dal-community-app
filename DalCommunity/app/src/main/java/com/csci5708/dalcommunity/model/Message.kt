package com.csci5708.dalcommunity.model

class Message {
    var message: String? = null
    var senderId: String? = null
    var sentTime: Long? = null

    constructor()

    constructor(message: String?, senderId: String?, sentTime: Long?) {
        this.message = message
        this.senderId = senderId
        this.sentTime = sentTime
    }
}