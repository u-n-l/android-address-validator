package com.example.roomdatabasewithmodelclassess.model

import androidx.room.TypeConverters


data class OpeningHoursSpecificationModel(
    val closes : String,
    val dayOfWeek : String,
    val isHoliday : Boolean,
    val opens : String,
)
