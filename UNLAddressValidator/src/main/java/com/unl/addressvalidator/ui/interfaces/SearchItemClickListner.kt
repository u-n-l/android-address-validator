package com.unl.addressvalidator.ui.interfaces

import com.unl.addressvalidator.model.autocomplet.AutocompleteData


interface SearchItemClickListner {
    fun searchItemClick(searchResultDTO: AutocompleteData)
}