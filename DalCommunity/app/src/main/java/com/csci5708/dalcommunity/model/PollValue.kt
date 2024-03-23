package com.csci5708.dalcommunity.model

class PollValue(
    val title: String,
    var votes: Int,
    var isSelected: Boolean
) {
    var percentage: String = ""
    var progress: Int = 0
}