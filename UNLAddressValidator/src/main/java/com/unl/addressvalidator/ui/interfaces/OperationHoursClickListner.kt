package com.unl.addressvalidator.ui.interfaces

import com.unl.addressvalidator.model.autocomplet.AutocompleteData


interface OperationHoursClickListner {
    fun dayClick(day: String)
    fun itemClick(hour: String,type : String)
}