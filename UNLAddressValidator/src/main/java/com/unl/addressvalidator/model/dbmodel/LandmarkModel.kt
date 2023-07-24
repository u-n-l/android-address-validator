package com.example.roomdatabasewithmodelclassess.model

import androidx.room.TypeConverters


data class LandmarkModel(
    var landmark_name : String,
    var type : String,
    var lattitude : String,
    var longitude : String,
    var img_url : String,
    var imageList : ArrayList<String>
)
