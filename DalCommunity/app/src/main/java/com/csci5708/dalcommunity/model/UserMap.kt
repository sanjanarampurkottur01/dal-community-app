package com.example.dalcommunity

import java.io.Serializable

data class UserMap(var title:String,val places:MutableList<Place>):Serializable
