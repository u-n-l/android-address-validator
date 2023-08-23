package com.unl.addressvalidator.ui.deliveryhours


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

import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.addressImageList
import com.unl.addressvalidator.ui.homescreen.updateAddPictureSavebtn
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.util.Utility.getImagePathFromUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@SuppressLint("SuspiciousIndentation")
fun DeliveryHoursActivity.showAddPictureDialog() {

    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
     bind  = AddPicturesPopupBinding.inflate(inflater)


    val bottomSheetDialog = BottomSheetDialog(this)
    bottomSheetDialog.setContentView(bind.root)



   // binding!!.addPicture!!.root.visibility = View.VISIBLE
    bind!!.headerTitle.text = "Add Picture to the Address"
    bind!!.addressType.text = UnlValidatorActivity.createAddressModel!!.addressType
    adapter = AddPicturesAdapter(addressImageList, this)
    bind!!.rvAddPictures.adapter = adapter
    bind!!.rvAddPictures.layoutManager = GridLayoutManager(this, 4)

/*    bind!!.tvAddPhotos!!.setOnClickListener {
        replaceIndex = addressImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
      //  selectImageForLandmakr = false
        openImagePicker()
    }*/

    var count = 0
    addressImageList.forEach() {
        var str: String = it.ivPhotos.toString()

        if (str != null && !str.equals(""))
        {
            count++
        }

    }

    if(count >0)
    {
        updateAddPictureSavebtn(true)
    }else
        updateAddPictureSavebtn(false)

    bind!!.tvSave.setOnClickListener {
      // binding!!.addPicture!!.root.visibility = View.GONE
        bottomSheetDialog.dismiss()
        var count = 0
        addressImageList.forEach() {
            var str: String = it.ivPhotos.toString()

            if (str != null && !str.equals(""))
            {

              var path =   getImagePathFromUri(it.ivPhotos!!, this)
                var file =  File(path)


                val mFile: RequestBody =
                    RequestBody.create("image/png".toMediaTypeOrNull(), file)

                val fileName: String = "photo_" + System.currentTimeMillis() + ".png"
                val fileToUpload: MultipartBody.Part? = MultipartBody.Part.createFormData("file", fileName, mFile)

                    viewModel.uploadImage(fileToUpload!!)
                count++
            }

        }


        binding!!.operationalHours!!.imageCount.text = "" + count + " of 9"

        var str: String = addressImageList.get(0).ivPhotos.toString()

        if (str != null && !str.equals("")) {


            Glide.with(this)
                .load(addressImageList.get(0).ivPhotos)
                .placeholder(R.drawable.add_photos) // Set a placeholder image if needed
                .error(R.drawable.add_photos) // Set an error image if loading fails
                .into(binding!!.operationalHours!!.addImage)
        }

        if(count <= 0)
        {
            clearAddressImageList()
        }
    }

   bind!!.removePicture.setOnClickListener {

        Log.v("CHECKBOX", "size : " + adapter!!.removedIndex.size)
        if (adapter!!.removedIndex!!.size > 0) {
            adapter!!.removedIndex!!.forEach {
                addressImageList.remove(it)
            }
            for (i in addressImageList.size until 9) {
                addressImageList.add(AddPicturesModel(Uri.EMPTY))
            }

            bind!!.rvAddPictures.adapter!!.notifyDataSetChanged()
            adapter!!.removedIndex!!.clear()




        }

    }

    bind!!.tvSkip.setOnClickListener {
        addressImageList.clear()
        for (i in 0 until 9) {
            addressImageList.add(AddPicturesModel(Uri.EMPTY))
        }
        bottomSheetDialog.dismiss()
    }

    bottomSheetDialog.show()
}


fun DeliveryHoursActivity.updateAddPictureSavebtn(status : Boolean)
{
    if(status)
    {
        bind!!.tvSave!!.setBackgroundResource(R.drawable.theme_round_btn)
        bind!!.tvSave!!.isEnabled = true
    }else
    {
        bind!!.tvSave!!.setBackgroundResource(R.drawable.bg_button_disable)
        bind!!.tvSave!!.isEnabled = false
    }

}

fun UnlValidatorActivity.getAddressImageUploadResponse() {
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