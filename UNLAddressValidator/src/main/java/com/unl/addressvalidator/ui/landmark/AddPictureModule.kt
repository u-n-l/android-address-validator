package com.unl.addressvalidator.ui.landmark


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.unl.addressvalidator.R
import com.unl.addressvalidator.databinding.AddPicturesPopupBinding
import com.unl.addressvalidator.model.landmark.LandmarkDataList

import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.adapters.LandMarkResultAdapter
import com.unl.addressvalidator.ui.fragments.showLandmarkPictureDialog
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity
import com.unl.addressvalidator.ui.homescreen.showAddPictureDialog
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.util.Utility.getImagePathFromUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@SuppressLint("SuspiciousIndentation")
fun LandmarkActivity.showLandmarkPictureDialog(
    position: Int,
    resulttList: ArrayList<LandmarkDataList>
) {

    landmarkImageList.clear()
    for (i in 0 until 9) {
        landmarkImageList.add(AddPicturesModel(Uri.EMPTY))
    }
    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val bind :AddPicturesPopupBinding = AddPicturesPopupBinding.inflate(inflater)
    val bottomSheetDialog = BottomSheetDialog(this)
    bottomSheetDialog.setContentView(bind.root)
    bind!!.headerTitle.text = "Add Picture to the Landmark"
    bind!!.addressType.text = UnlValidatorActivity.createAddressModel!!.addressType
    adapter = AddPicturesAdapter(landmarkImageList, this)
    bind!!.rvAddPictures.adapter = adapter
    bind!!.rvAddPictures.layoutManager = GridLayoutManager(this, 4)

    bind!!.tvAddPhotos!!.setOnClickListener {
        replaceIndex = landmarkImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
       // selectImageForLandmakr = true
        openImagePicker()
    }

    bind!!.tvSave.setOnClickListener {

        bottomSheetDialog.dismiss()
        var count = 0
        val imageList = ArrayList<String>()
        landmarkImageList.forEach() {
            var str: String = it.ivPhotos.toString()

            if (str != null && !str.equals(""))
            {
                imageList.add(str)
                count++
            }
        }


        var str: String = landmarkImageList.get(0).ivPhotos.toString()

        if (str != null && !str.equals("")) {
            resulttList.get(position).url = str
            resulttList.get(position).imgCount = "" + count
            resulttList.get(position).imageList = imageList
            binding!!.addLandmark.rvLandmark.adapter = LandMarkResultAdapter(resulttList, this)
            binding!!.addLandmark.rvLandmark.adapter!!.notifyDataSetChanged()

        }



    }

    bind!!.removePicture.setOnClickListener {

        Log.v("CHECKBOX", "size : " + adapter!!.removedIndex.size)
        if (adapter!!.removedIndex!!.size > 0) {
            adapter!!.removedIndex!!.forEach {
                landmarkImageList.remove(it)
            }
            for (i in landmarkImageList.size until 9) {
                landmarkImageList.add(AddPicturesModel(Uri.EMPTY))
            }
            showLandmarkPictureDialog( position
                ,  resulttList)
        }
    }

    bind!!.tvSkip.setOnClickListener {
        landmarkImageList.clear()
        for (i in 0 until 9) {
            landmarkImageList.add(AddPicturesModel(Uri.EMPTY))
        }
    }
    bottomSheetDialog.show()
}


fun LandmarkActivity.getAddressImageUploadResponse() {
    viewModel?.imageUploadResponseData?.observe(this, { response ->
        when (response) {
            is ApiCallBack.Success -> {
                response.data
                if(response.data!= null)
                {
                }
            }

            is ApiCallBack.Error -> {
            }

            is ApiCallBack.Loading -> {
            }

        }
    })
}