package com.unl.addressvalidator.ui.interfaces


import com.unl.addressvalidator.model.dbmodel.CreateAddressModel


interface AddressItemClickListner {
    fun addressItemClick(createAddressModel: CreateAddressModel)
}