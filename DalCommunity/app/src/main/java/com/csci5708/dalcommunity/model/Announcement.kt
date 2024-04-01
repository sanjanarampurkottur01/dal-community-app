package com.csci5708.dalcommunity.model

/**
 * Model class for Announcement.
 */
class Announcement {
    var title: String? = ""
    var content: String? = ""
    var timestamp: Long? = 0
    var senderName: String? = ""

    /**
     * Default constructor.
     */
    constructor()

    /**
     * Parameterized constructor.
     * @param title The title of the announcement.
     * @param content The content of the announcement.
     * @param timestamp The timestamp of the announcement.
     * @param senderName The sender's name.
     */
    constructor(title: String?, content: String?, timestamp: Long?, senderName: String?) {
        this.title = title
        this.content = content
        this.timestamp = timestamp
        this.senderName = senderName
    }
}