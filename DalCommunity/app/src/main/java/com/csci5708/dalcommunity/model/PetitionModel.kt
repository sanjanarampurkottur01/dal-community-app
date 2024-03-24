package com.csci5708.dalcommunity.model

import com.google.firebase.Timestamp

data class Petition(
    var id: String? = null,
    val title: String = "",
    val description: String = "",
    val imgUrl: String = "",
    val number_signed: Int = 0,
    val user: String? = null,
    val creation_date: Timestamp? = null,
    val community: String? = null
)
