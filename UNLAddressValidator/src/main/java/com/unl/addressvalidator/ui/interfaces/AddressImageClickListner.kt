package com.unl.addressvalidator.ui.interfaces

import com.unl.addressvalidator.model.autocomplet.AutocompleteData


interface AddressImageClickListner {
    fun addressImageClick(replaceIndex : Int, isReplace : Boolean)
}