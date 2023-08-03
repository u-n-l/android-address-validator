package com.example.roomdatabasewithmodelclassess.model

import androidx.room.TypeConverters



data class EntranceModel(
    var entranceName : String,
    var entranceNo : String,
    var entranceCategory : String,
    var entranceInfo : String,
    var  imgCount : String,
    var entranceId : Long,
    var lattitude : Double,
    var longitude : Double,
    var url : String,
    var imageArray : ArrayList<String>
)
