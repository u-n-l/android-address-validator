package com.unl.addressvalidator.ui.homescreen


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.unl.addressvalidator.R
import com.unl.addressvalidator.databinding.AddPicturesPopupBinding
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.addressImageList
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.pinLat
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.pinLong
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.util.Utility.convert
import com.unl.addressvalidator.util.Utility.getImagePathFromUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@SuppressLint("SuspiciousIndentation")
fun UnlValidatorActivity.showAddPictureDialog() {

    val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    bind = AddPicturesPopupBinding.inflate(inflater)


    val bottomSheetDialog = BottomSheetDialog(this)
    bottomSheetDialog.setContentView(bind.root)


    // binding!!.addPicture!!.root.visibility = View.VISIBLE
    bind!!.headerTitle.text = "Add Picture to the Address"
    bind!!.addressType.text = addressType
    adapter = AddPicturesAdapter(addressImageList, this)
    bind!!.rvAddPictures.adapter = adapter
    bind!!.rvAddPictures.layoutManager = GridLayoutManager(this, 4)

    var counter = 0
    addressImageList.forEach() {
        var str: String = it.ivPhotos.toString()

        if (str != null && !str.equals("")) {
            counter++
        }

    }
    if (counter > 0)
        updateAddPictureSavebtn(true)
    else
        updateAddPictureSavebtn(false)

/*

    bind!!.tvAddPhotos!!.setOnClickListener {
        replaceIndex = addressImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
      //  selectImageForLandmakr = false
        openImagePicker()
    }
*/

    bind!!.tvSave.setOnClickListener {
        // binding!!.addPicture!!.root.visibility = View.GONE
        bottomSheetDialog.dismiss()
        var count = 0
        addressImageList.forEach() {
            var str: String = it.ivPhotos.toString()

            if (str != null && !str.equals("")) {
                // var path =   getImagePathFromUri(it.ivPhotos!!, this)

                /* val exifInterface = ExifInterface(path!!)
                 exifInterface.setAttribute(
                     ExifInterface.TAG_GPS_LATITUDE,
                     convert(pinLat)
                 )
                 exifInterface.setAttribute(
                     ExifInterface.TAG_GPS_LATITUDE_REF,
                     if (pinLat > 0) "N" else "S"
                 )
                 exifInterface.setAttribute(
                     ExifInterface.TAG_GPS_LONGITUDE,
                     convert(pinLong)
                 )
                 exifInterface.setAttribute(
                     ExifInterface.TAG_GPS_LONGITUDE_REF,
                     if (pinLong > 0) "E" else "W"
                 )
                 exifInterface.saveAttributes()*/

                /*   var file =  File(path)
                   val mFile: RequestBody =
                       RequestBody.create("image/png".toMediaTypeOrNull(), file)
                   val fileName: String = "photo_" + System.currentTimeMillis() + ".png"
                   val fileToUpload: MultipartBody.Part? = MultipartBody.Part.createFormData("file", fileName, mFile)
                   viewModel.uploadImage(fileToUpload!!)*/

                count++
            }

        }

        if(count<= 0)
        {
            clearAddressImageList()
        }

        binding!!.confirmAddress!!.imageCount.text = "" + count + " of 9"
        binding!!.addNewAdd!!.imageCount.text = "" + count + " of 9"

        var str: String = addressImageList.get(0).ivPhotos.toString()

        if (str != null && !str.equals("")) {
            Glide.with(this)
                .load(addressImageList.get(0).ivPhotos)
                .placeholder(R.drawable.photos) // Set a placeholder image if needed
                .error(R.drawable.photos) // Set an error image if loading fails
                .into(binding!!.confirmAddress!!.addImage)

            Glide.with(this)
                .load(addressImageList.get(0).ivPhotos)
                .placeholder(R.drawable.photos) // Set a placeholder image if needed
                .error(R.drawable.photos) // Set an error image if loading fails
                .into(binding!!.addNewAdd!!.addImage)

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

            var count = 0
            addressImageList.forEach() {
                var str: String = it.ivPhotos.toString()

                if (str != null && !str.equals("")) {
                    count++
                }

            }

          /*  if (count <= 0) {

                updateAddPictureSavebtn(false)
            }*/
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

fun UnlValidatorActivity.updateAddPictureSavebtn(status: Boolean) {
    if (status) {
        bind!!.tvSave!!.setBackgroundResource(R.drawable.theme_round_btn)
        bind!!.tvSave!!.isEnabled = true
    } else {
        bind!!.tvSave!!.setBackgroundResource(R.drawable.bg_button_disable)
        bind!!.tvSave!!.isEnabled = false
    }

}

fun UnlValidatorActivity.getAddressImageUploadResponse() {
    viewModel?.imageUploadResponseData?.observe(this, { response ->
        when (response) {
            is ApiCallBack.Success -> {
                response.data
                if (response.data != null) {

                }
            }

            is ApiCallBack.Error -> {

            }

            is ApiCallBack.Loading -> {

            }

        }
    })
}