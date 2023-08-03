package com.unl.addressvalidator.ui.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasewithmodelclassess.model.EntranceModel
import com.example.roomdatabasewithmodelclassess.model.LandmarkModel
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.ui.adapters.EntrancesAdapter
import com.unl.addressvalidator.ui.adapters.LandMarkResultAdapter
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.util.Utility


fun HomeFragment.initEntrance() {
    isChangeMarker = false
    entranceSelectionEnable = true
    binding!!.addEntrances!!.root.visibility = View.VISIBLE
    binding!!.pinHint!!.text = "Move the pin to the building entrance"

    val layoutManager = LinearLayoutManager(activity)
    binding!!.addEntrances!!.rvEntrances.layoutManager = layoutManager
    binding!!.addEntrances!!.rvEntrances.setBackgroundResource(R.color.white)

    binding!!.addEntrances!!.ivClose.setOnClickListener {
        entranceSelectionEnable = false
        binding!!.addEntrances!!.root.visibility = View.GONE

    }
    binding!!.addEntrances!!.tvConfirm.setOnClickListener {
        entranceSelectionEnable = false
        isChangeMarker = true
        binding!!.addEntrances!!.root.visibility = View.GONE
        binding!!.operationalHours!!.root.visibility = View.VISIBLE
        setOperationalHoursClick()
        updateEntrance()
    }

    binding!!.addEntrances!!.tvSkip.setOnClickListener {
        entranceSelectionEnable = false
        isChangeMarker = true
        binding!!.addEntrances!!.root.visibility = View.GONE
        binding!!.operationalHours!!.root.visibility = View.VISIBLE
        setOperationalHoursClick()
    }
}

fun HomeFragment.updateEntrance()
{
    createAddressModel!!.entranceModel = entranceList
}
fun HomeFragment.addEntrancePoint(lattitude : Double, longitude : Double)
{
    val entranceModel = EntranceModel("Entrance", ""+(entranceList.size+1).toString(),"","","0",
        Utility.returnRandomDigit(),lattitude,longitude,"",ArrayList<String>())
    entranceList.add(entranceModel)
    binding!!.addEntrances.rvEntrances.visibility = View.VISIBLE
    binding!!.addEntrances.rvEntrances.adapter = EntrancesAdapter(entranceList, this)
    binding!!.addEntrances.rvEntrances.adapter!!.notifyDataSetChanged()
}



fun HomeFragment.showEntrancePictureDialog(position : Int,resulttList: ArrayList<EntranceModel>)
{
    binding!!.addPicture!!.root.visibility = View.VISIBLE
    binding!!.addPicture!!.headerTitle.text = "Add Picture to the Entrance"
    binding!!.addPicture!!.addressType.text = addressType
    adapter = AddPicturesAdapter(entranceImageList,this)
    binding!!.addPicture!!.rvAddPictures.adapter = adapter
    binding!!.addPicture!!.rvAddPictures.layoutManager = GridLayoutManager(context, 4)

    binding!!.addPicture!!.tvAddPhotos!!.setOnClickListener {
        replaceIndex = entranceImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
        selectImageForEntrance = true
        selectImageForLandmakr = false
        openImagePicker()
    }

    binding!!.addPicture!!.tvSave.setOnClickListener {
        binding!!.addPicture!!.root.visibility = View.GONE

        var count = 0
        entranceImageList.forEach() {
            var str : String = it.ivPhotos.toString()

            if (str!= null && !str.equals(""))
                count++
        }

        /*  binding!!.confirmAddress!!.imageCount.text = ""+count + " of 9"
          binding!!.addNewAdd!!.imageCount.text = ""+count + " of 9"*/

        var str : String = entranceImageList.get(0).ivPhotos.toString()

        if (str!= null && !str.equals(""))
        {
            resulttList.get(position).url = str
            resulttList.get(position).imgCount = ""+count
            binding!!.addEntrances.rvEntrances.adapter = EntrancesAdapter(resulttList, this)
            binding!!.addEntrances.rvEntrances.adapter!!.notifyDataSetChanged()

        }
    }

    binding!!.addPicture!!.removePicture.setOnClickListener {

        Log.v("CHECKBOX","size : "+adapter!!.removedIndex.size)
        if (adapter!!.removedIndex!!.size > 0 )
        {
            adapter!!.removedIndex!!.forEach {
                dataList.remove(it)
            }
            for (i in entranceImageList.size until 9) {
                entranceImageList.add(AddPicturesModel(Uri.EMPTY))
            }
            showEntrancePictureDialog(position,resulttList)
        }

    }

    binding!!.addPicture!!.tvSkip.setOnClickListener {
        entranceImageList.clear()
        for (i in 0 until 9) {
            entranceImageList.add(AddPicturesModel(Uri.EMPTY))
        }
        binding!!.addPicture!!.root.visibility = View.GONE
    }
}

fun HomeFragment.removeEntrance(position : Int,resulttList: ArrayList<EntranceModel>)
{

    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.delete_entrance_popup)
    dialog.show()
    dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
  //  dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    dialog.window!!.setGravity(Gravity.CENTER)

    dialog.findViewById<TextView>(R.id.tvDelete).setOnClickListener {
        dialog.dismiss()
        resulttList.removeAt(position)
        mapBoxMap?.removeMarker( entranceMarker.get(position))
        entranceMarker.removeAt(position)
        entranceList = resulttList
        binding!!.addEntrances.rvEntrances.adapter = EntrancesAdapter(entranceList, this)
        binding!!.addEntrances.rvEntrances.adapter!!.notifyDataSetChanged()
    }

    dialog.findViewById<TextView>(R.id.tvSkip).setOnClickListener {
        dialog.dismiss()
    }

}



fun HomeFragment.editeEntrance(position : Int,resulttList: ArrayList<EntranceModel>)
{

    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(R.layout.edit_entrance_popup)
    dialog.show()
    dialog.window!!.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

   var  edtEntranceName = dialog.findViewById<EditText>(R.id.edtEntranceName)
   var  edtEntranceCategory = dialog.findViewById<EditText>(R.id.edtEntranceCategory)
   var  edtEntranceInfo = dialog.findViewById<EditText>(R.id.edtEntranceInfo)
    //  dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
    dialog.window!!.setGravity(Gravity.BOTTOM)
    dialog.findViewById<TextView>(R.id.tvSave).setOnClickListener {
        dialog.dismiss()
        resulttList.get(position).entranceCategory = edtEntranceCategory.text.toString()
        resulttList.get(position).entranceName = edtEntranceName.text.toString()
        resulttList.get(position).entranceInfo = edtEntranceInfo.text.toString()
        entranceList = resulttList
        binding!!.addEntrances.rvEntrances.adapter = EntrancesAdapter(entranceList, this)
        binding!!.addEntrances.rvEntrances.adapter!!.notifyDataSetChanged()
    }

    dialog.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
        dialog.dismiss()
    }

}