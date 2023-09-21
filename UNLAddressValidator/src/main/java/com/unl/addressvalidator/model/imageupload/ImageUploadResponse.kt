package com.example.roomdatabasewithmodelclassess.model

import androidx.room.TypeConverters



data class ImageUploadResponse(
    var asset_name : String,
    var catalog_item_ids : ArrayList<String>
)
