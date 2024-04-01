package com.csci5708.dalcommunity.model

/**
 * Model class for Message.
 */
class Message {
    var message: String? = null
    var senderId: String? = null
    var sentTime: Long? = null

    /**
     * Default constructor.
     */
    constructor()

    /**
     * Parameterized constructor.
     * @param message The content of the message.
     * @param senderId The sender's ID.
     * @param sentTime The timestamp when the message was sent.
     */
    constructor(message: String?, senderId: String?, sentTime: Long?) {
        this.message = message
        this.senderId = senderId
        this.sentTime = sentTime
    }
}