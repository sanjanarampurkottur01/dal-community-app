package com.csci5708.dalcommunity.model

data class User(
    val name: String,
    val email: String,
    val description: String,
    val firstInterest: String,
    val secondInterest: String,
    val thirdInterest: String,
    val photoUri: String
)