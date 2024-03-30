package com.example.dalcommunity

import com.google.firebase.firestore.Exclude
import java.io.Serializable

data class UserMap(var name:String, var email:String, var itemName:String, var description:String,
                   var dateTime:String, var category:String, var imageUri:String,
                   var profileImageUri:String="",
                   var places:MutableList<Place> = mutableListOf(),
                    ):Serializable {
    constructor():this("","","","","","","","")

    @Exclude
    var id=""
}

