package com.unl.addressvalidator.ui.addressdetail

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.roomdatabasewithmodelclassess.model.EntranceModel
import com.example.roomdatabasewithmodelclassess.model.LandmarkModel
import com.google.gson.internal.LinkedTreeMap
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import com.unl.addressvalidator.R
import com.unl.addressvalidator.database.UnlAddressDatabase
import com.unl.addressvalidator.databinding.ActivityAddressListBinding
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.ui.adapters.*
import com.unl.addressvalidator.ui.deliveryhours.DeliveryHoursActivity
import com.unl.addressvalidator.ui.interfaces.AddressItemClickListner
import com.unl.addressvalidator.ui.interfaces.EntranceClickListner
import com.unl.addressvalidator.ui.interfaces.LandmarkClickListner
import com.unl.addressvalidator.util.Constant.ELEVATOR_ACCESSIBILITY
import com.unl.addressvalidator.util.Constant.WHEELCHAIR_ACCESSIBILITY
import com.unl.addressvalidator.util.Utility
import java.util.*
import kotlin.collections.ArrayList

/**
 * [AddressListActivity] provide functionality to show your address
 * You can see the details of each address
 * @constructor
 *
 */
class AddressListActivity : AppCompatActivity(), AddressItemClickListner , EntranceClickListner,
    LandmarkClickListner {
    var binding: ActivityAddressListBinding? = null
    lateinit var viewModel: AddressViewModel
    lateinit var database: UnlAddressDatabase
    lateinit var deliveryHoursAdapter: DeliveryHoursAdapter

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
        if(addresses.size<2)
        binding!!.addressesView!!.addressCount.text = ""+addresses.size + " result found"
        else
            binding!!.addressesView!!.addressCount.text = ""+addresses.size + " results found"

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

           val layoutManager = LinearLayoutManager(this)
           binding!!.addressesDetailView.rvEntrances.layoutManager = layoutManager
           binding!!.addressesDetailView.rvEntrances.setBackgroundResource(R.color.white)

           val layoutManager1 = LinearLayoutManager(this)
           binding!!.addressesDetailView.rvLandmark.layoutManager = layoutManager1
           binding!!.addressesDetailView.rvLandmark.setBackgroundResource(R.color.white)




           binding!!.addressesDetailView!!.closeViewer.setOnClickListener {
               binding!!.addressesDetailView!!.imageViewer.visibility = View.GONE
           }

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


               val adapter = ImageAdapter(this)
               adapter.setData(addresses.images)
               binding!!.addressesDetailView.pager.adapter = adapter
               //viewPager.currentItem = adapter.count - 1
               binding!!.addressesDetailView.tabDots.setViewPager( binding!!.addressesDetailView.pager)

           }else
           {
               binding!!.addressesDetailView!!.ivAddress.visibility = View.GONE
           }

           if(addresses.openingHoursSpecificationModel!!.size>0)
           {
              // var openTime = addresses.openingHoursSpecificationModel!!.get(0).opens
             //  var closeTime = addresses.openingHoursSpecificationModel!!.get(0).closes

/*
               val getrow: Any = addresses.openingHoursSpecificationModel!!.get(0)
               val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
               val opens = t["opens"].toString()
               val closes = t["closes"].toString()

               binding!!.addressesDetailView.tvOpenClosehours.text = ""+ opens+"-"+closes*/


               deliveryHoursAdapter = DeliveryHoursAdapter(addresses.openingHoursSpecificationModel!!)
               binding!!.addressesDetailView.rvDeliverHours.adapter = deliveryHoursAdapter
               val layoutManager = LinearLayoutManager(this)
               binding!!.addressesDetailView.rvDeliverHours.layoutManager = layoutManager
               binding!!.addressesDetailView.rvDeliverHours.setBackgroundResource(R.color.white)
               binding!!.addressesDetailView.rvDeliverHours.adapter!!.notifyDataSetChanged()


           }
           if(addresses!!.accessibility!= null && addresses!!.accessibility!!.size>0)
           {
               if(addresses!!.accessibility!!.contains(WHEELCHAIR_ACCESSIBILITY))
               {
                   binding!!.addressesDetailView.wheelChairView.visibility = View.VISIBLE
               }else
               {
                   binding!!.addressesDetailView.wheelChairView.visibility = View.GONE
               }
               if(addresses!!.accessibility!!.contains(ELEVATOR_ACCESSIBILITY))
               {
                   binding!!.addressesDetailView.elevatorView.visibility = View.VISIBLE
               }else
               {
                   binding!!.addressesDetailView.elevatorView.visibility = View.GONE
               }

               binding!!.addressesDetailView.accessibilityHeading.visibility = View.VISIBLE
               binding!!.addressesDetailView.accessibilityValue.visibility = View.VISIBLE
           }else
           {
               binding!!.addressesDetailView.accessibilityHeading.visibility = View.GONE
               binding!!.addressesDetailView.accessibilityValue.visibility = View.GONE
           }
       }
       catch (e:java.lang.Exception)
       {
           e.printStackTrace()
       }
        showEntrances(addresses)
        showLandmark(addresses)
    }
    fun showEntrances(addresses: CreateAddressModel)
    {

     try {
         binding!!.addressesDetailView.rvEntrances.visibility = View.VISIBLE
         binding!!.addressesDetailView.rvEntrances.adapter = EntrancesListAdapter(addresses.entranceModel!!, this)
         binding!!.addressesDetailView.rvEntrances.adapter!!.notifyDataSetChanged()
     }
     catch (e:java.lang.Exception)
     {
         e.printStackTrace()
     }
    }

    fun showLandmark(addresses: CreateAddressModel)
    {
      try {
              binding!!.addressesDetailView.rvLandmark.adapter = LandMarksAdapter(addresses.landmarkModel!!, this)
              binding!!.addressesDetailView.rvLandmark.adapter!!.notifyDataSetChanged()
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

    override fun entranceEditClick(position: Int, entranceModel: ArrayList<EntranceModel>) {

    }

    override fun entranceDeleteClick(position: Int, entranceModel: ArrayList<EntranceModel>) {

    }

    override fun entranceImageClick(position: Int, entranceModel: ArrayList<EntranceModel>) {
       try {
           val getrow: Any = entranceModel.get(position)
           val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>

           val imageArray : ArrayList<String> = t["imageArray"] as  ArrayList<String>

           val adapter = ImageAdapter(this)
           adapter.setData(imageArray)
           binding!!.addressesDetailView.imagePager.adapter = adapter
           binding!!.addressesDetailView.pagerDots.setViewPager( binding!!.addressesDetailView.pager)
           binding!!.addressesDetailView!!.imageViewer.visibility = View.VISIBLE
       }
       catch (e:java.lang.Exception)
       {
           e.printStackTrace()
       }
    }

    override fun landmarkItemClick(reverseGeoCodeResponse: LandmarkDataList) {

    }

    override fun uploadLandmarkPic(position: Int, resulttList: ArrayList<LandmarkDataList>) {


    }

    override fun viewLandMarkPic(position: Int, resulttList: ArrayList<LandmarkModel>)
    {
        val getrow: Any = resulttList.get(position)
        val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
        val list : ArrayList<String> = t["imageList"] as  ArrayList<String>
        val adapter = ImageAdapter(this)
        adapter.setData(list)
        binding!!.addressesDetailView.imagePager.adapter = adapter
        binding!!.addressesDetailView.pagerDots.setViewPager( binding!!.addressesDetailView.pager)
        binding!!.addressesDetailView!!.imageViewer.visibility = View.VISIBLE
    }


}