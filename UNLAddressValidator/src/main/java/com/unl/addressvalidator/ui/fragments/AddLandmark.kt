package com.unl.addressvalidator.ui.fragments

import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasewithmodelclassess.model.EntranceModel
import com.example.roomdatabasewithmodelclassess.model.LandmarkModel
import com.example.roomdatabasewithmodelclassess.model.LocationModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.ui.adapters.LandMarkResultAdapter
import com.unl.addressvalidator.ui.landmark.LandmarkActivity

fun HomeFragment.openLandmarkPopup() {
  //  binding!!.addNewAdd!!.root.visibility = View.GONE
    binding!!.addLandmark!!.root.visibility = View.VISIBLE
    callNearbyLandMark()

    binding!!.addLandmark!!.ivClose.setOnClickListener {
        binding!!.addLandmark!!.root.visibility = View.GONE

    }
    binding!!.addLandmark!!.tvConfirm.setOnClickListener {
        binding!!.addLandmark!!.root.visibility = View.GONE
        initEntrance()
        updateLandmark()
    }

    binding!!.addLandmark!!.tvSkip.setOnClickListener {
        binding!!.addLandmark!!.root.visibility = View.GONE
        initEntrance()
    }
}
private fun HomeFragment.callNearbyLandMark()
{
    var circleObject = JsonObject()
    var jsonObject = JsonObject()
    var jsonArray = JsonArray()
    jsonArray.add(pinLong)
    jsonArray.add(pinLat)
    jsonArray.add(5000)
    jsonObject.add("Point", jsonArray)
    circleObject.add("Circle",jsonObject)
    viewModel.getNearbyLandmark(circleObject)
}

fun HomeFragment.showNearbyLandmark(landmarkList: ArrayList<ReverseGeoCodeResponse>) {

    try {
        if(landmarkList.size <= 0)
        {
            binding!!.addNewAdd!!.root.visibility = View.VISIBLE
            binding!!.addLandmark!!.root.visibility = View.GONE

        }else
        {
            var landmarkDataList = ArrayList<LandmarkDataList>()

            landmarkList.forEach {
                landmarkDataList.add(LandmarkDataList("","",it!!,ArrayList<String>()))
            }
            initLandmarkList()
            binding!!.addLandmark.rvLandmark.adapter = LandMarkResultAdapter(landmarkDataList, this)
            binding!!.addLandmark.rvLandmark.adapter!!.notifyDataSetChanged()
        }
     //   Toast.makeText(context,"size : "+landmarkList.size,Toast.LENGTH_SHORT).show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun HomeFragment.initLandmarkList()
{

        val layoutManager = LinearLayoutManager(activity)
        binding!!.addLandmark!!.rvLandmark.layoutManager = layoutManager
    //    val separator = DividerItemDecoration(activity, layoutManager.orientation)
      //  binding!!.addLandmark!!.rvLandmark.addItemDecoration(separator)
        binding!!.addLandmark!!.rvLandmark.setBackgroundResource(R.color.white)


       // val lateralPadding = resources.getDimension(R.dimen.big_padding).toInt()
      //  binding!!.addLandmark!!.rvLandmark.setPadding(lateralPadding, 0, lateralPadding, 0)

}

fun HomeFragment.updateLandmark()
{



    var landmarkModelList = ArrayList<LandmarkModel>()

    selectedLandmarkDataList.forEach {
        val landmarkModel = LandmarkModel(
            it!!.addressInfo!!.features.get(0).properties!!.postal_address.get(0).house_number,
            it!!.addressInfo!!.features.get(0).type,
            "" + it!!.addressInfo!!.features.get(0).geometry.coordinates.get(0),
            "" + it!!.addressInfo!!.features.get(0).geometry.coordinates.get(0),
            "", it!!.imageList
        )
        landmarkModelList.add(landmarkModel)
    }


  /*  var entranceList: ArrayList<EntranceModel> = ArrayList()
    val entranceModel = EntranceModel("main gate", "1")
    entranceList.add(entranceModel)*/

    createAddressModel!!.landmarkModel = landmarkModelList
}