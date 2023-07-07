package com.unl.addressvalidator.ui.fragments

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.ui.adapters.AddressListAdapter

fun HomeFragment.initAddressList(addresses : List<CreateAddressModel>)
{
    binding!!.cvPintHint.visibility = View.GONE
    binding!!.ivShowAddreess.visibility = View.GONE
    binding!!.addressesView.root.visibility = View.VISIBLE

    val layoutManager = LinearLayoutManager(activity)
    binding!!.addressesView!!.rvAddress.layoutManager = layoutManager
    binding!!.addressesView!!.rvAddress.setBackgroundResource(R.color.white)
    binding!!.addressesView!!.rvAddress.adapter = AddressListAdapter(addresses!!, this)
    binding!!.addressesView!!.rvAddress.adapter!!.notifyDataSetChanged()
    binding!!.addressesView!!.ivClose.setOnClickListener {
        binding!!.cvPintHint.visibility = View.VISIBLE
        binding!!.ivShowAddreess.visibility = View.VISIBLE
        binding!!.addressesView.root.visibility = View.GONE
    }

}