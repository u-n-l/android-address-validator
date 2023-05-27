package com.example.roomdatabasewithmodelclassess.model

import androidx.room.TypeConverters


data class LandmarkModel(
    val landmark_name : String,
    val type : String,
    val lattitude : String,
    val longitude : String,
    val img_url : String
)
