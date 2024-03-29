package com.csci5708.dalcommunity.model

import com.google.firebase.Timestamp
/**
 * Data class representing a petition.
 * @property id The ID of the petition.
 * @property title The title of the petition.
 * @property description The description of the petition.
 * @property imgUrl The URL of the image associated with the petition.
 * @property number_signed The number of users who have signed the petition.
 * @property user The user who created the petition.
 * @property creation_date The creation date of the petition.
 * @property community The community to which the petition belongs.
 * @property signed_user The list of users who have signed the petition.
 */
data class Petition(
    var id: String? = null,
    val title: String = "",
    val description: String = "",
    val imgUrl: String = "",
    val number_signed: Int = 0,
    val user: String? = null,
    val creation_date: Timestamp? = null,
    val community: String? = null,
    val signed_user: List<String>? = null
)
