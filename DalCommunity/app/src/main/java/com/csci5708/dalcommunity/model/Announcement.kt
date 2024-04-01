package com.csci5708.dalcommunity.model

class Announcement {
    var title: String? = ""
    var content: String? = ""
    var timestamp: Long? = 0
    var senderName: String? = ""

    constructor()

    constructor(title: String?, content: String?, timestamp: Long?, senderName: String?) {
        this.title = title
        this.content = content
        this.timestamp = timestamp
        this.senderName = senderName
    }
}
