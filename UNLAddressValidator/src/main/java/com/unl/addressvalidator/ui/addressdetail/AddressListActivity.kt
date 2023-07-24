package com.unl.addressvalidator.ui.addressdetail

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.internal.LinkedTreeMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.database.UnlAddressDatabase
import com.unl.addressvalidator.databinding.ActivityAddressListBinding
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.ui.adapters.AddressListAdapter
import com.unl.addressvalidator.ui.interfaces.AddressItemClickListner
import java.util.*


class AddressListActivity : AppCompatActivity(), AddressItemClickListner {
    var binding: ActivityAddressListBinding? = null
    lateinit var viewModel: AddressViewModel
    lateinit var database: UnlAddressDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressListBinding.inflate(getLayoutInflater())
        setContentView(binding!!.root)
        database = UnlAddressDatabase.getInstance(this)
        initiateViewModel()
        viewModel.getAddress(database)

        binding!!.addressesView!!.ivClose.setOnClickListener {
            finish()
            // binding!!.addressesView.root.visibility = View.GONE
        }
  Handler().postDelayed(Runnable {
      getAddressCreated()
      viewModel.getAddress(database) },100)
    }

    private fun initiateViewModel() {
        viewModel = ViewModelProvider(this)[AddressViewModel::class.java]
    }

    fun getAddressCreated() {
        viewModel.addresslist.observe(this, { users ->
            try {
                if (users != null && users.size > 0) {
                    Collections.reverse(users)
                    initAddressList(users)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        })
    }
    fun initAddressList(addresses: List<CreateAddressModel>) {
        val layoutManager = LinearLayoutManager(this)
        binding!!.addressesView!!.rvAddress.layoutManager = layoutManager
        binding!!.addressesView!!.rvAddress.setBackgroundResource(R.color.white)
        binding!!.addressesView!!.rvAddress.adapter = AddressListAdapter(addresses!!, this)
        binding!!.addressesView!!.rvAddress.adapter!!.notifyDataSetChanged()

    }

    fun initAddressDetails(addresses: CreateAddressModel) {
       try{
           binding!!.addressesDetailView.root.visibility = View.VISIBLE
           binding!!.addressesDetailView!!.ivClose.setOnClickListener {
               binding!!.addressesDetailView.root.visibility = View.GONE
           }

           binding!!.addressesDetailView.tvCategory.text = addresses.addressType
           binding!!.addressesDetailView.tvPlaceName.text = addresses.address

           if (addresses.addressType.equals("home")) {
               binding!!.addressesDetailView.ivAdressType.setImageResource(R.drawable.home)
           }else  if (addresses.addressType.equals("office"))
           {
               binding!!.addressesDetailView.ivAdressType.setImageResource(R.drawable.ic_office)
           }

           if (addresses.images != null && addresses.images!!.size > 0) {
               // imgCount.text = "" + resulttList.get(position)!!.images!!.size + " of 9"
               Glide.with(this)
                   .load(addresses.images!!.get(0))
                   .placeholder(R.drawable.photos) // Set a placeholder image if needed
                   .error(R.drawable.photos) // Set an error image if loading fails
                   .into(binding!!.addressesDetailView!!.ivAddress)
           }else
           {
               binding!!.addressesDetailView!!.ivAddress.visibility = View.GONE
           }

           if(addresses.openingHoursSpecificationModel!!.size>0)
           {
              // var openTime = addresses.openingHoursSpecificationModel!!.get(0).opens
             //  var closeTime = addresses.openingHoursSpecificationModel!!.get(0).closes


               val getrow: Any = addresses.openingHoursSpecificationModel!!.get(0)
               val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
               val opens = t["opens"].toString()
               val closes = t["closes"].toString()

               binding!!.addressesDetailView.tvOpenClosehours.text = ""+ opens+"-"+closes
           }
       }
       catch (e:java.lang.Exception)
       {
           e.printStackTrace()
       }
    }

    override fun addressItemClick(createAddressModel: CreateAddressModel) {
        initAddressDetails(createAddressModel)
    }

    override fun onBackPressed() {
        if(binding!!.addressesDetailView.root.isVisible)
        {
            binding!!.addressesDetailView.root.visibility = View.GONE
        }else
        {
            super.onBackPressed()
        }
    }
}