package com.example.roomdatabasewithmodelclassess.model

import androidx.room.TypeConverters


data class OpeningHoursSpecificationModel(
    var opens : String,
    var dayOfWeek : String,
    var isHoliday : Boolean,
    var closes : String,
)
