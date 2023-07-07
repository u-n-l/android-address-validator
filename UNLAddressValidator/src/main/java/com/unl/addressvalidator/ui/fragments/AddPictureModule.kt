package com.unl.addressvalidator.ui.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.adapters.LandMarkResultAdapter
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.util.Utility.getImagePathFromUri
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


@SuppressLint("SuspiciousIndentation")
fun HomeFragment.showAddPictureDialog() {

    binding!!.addPicture!!.root.visibility = View.VISIBLE
    binding!!.addPicture!!.headerTitle.text = "Add Picture to the Address"
    binding!!.addPicture!!.addressType.text = addressType
    adapter = AddPicturesAdapter(dataList, this)
    binding!!.addPicture!!.rvAddPictures.adapter = adapter
    binding!!.addPicture!!.rvAddPictures.layoutManager = GridLayoutManager(context, 4)

    binding!!.addPicture!!.tvAddPhotos!!.setOnClickListener {
        replaceIndex = dataList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
        selectImageForLandmakr = false
        openImagePicker()
    }

    binding!!.addPicture!!.tvSave.setOnClickListener {
        binding!!.addPicture!!.root.visibility = View.GONE

        var count = 0
        dataList.forEach() {
            var str: String = it.ivPhotos.toString()

            if (str != null && !str.equals(""))
            {

              var path =   getImagePathFromUri(it.ivPhotos!!, context)
                var file =  File(path)


              /*  var path : File = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
                var  file  =  File(path, it.ivPhotos!!.getPath());

                try {
                    // Make sure the Pictures directory exists.
                    path.mkdirs()
                }
                catch (e:java.lang.Exception)
                {
                    e.printStackTrace()
                }
*/
                val mFile: RequestBody =
                    RequestBody.create("image/png".toMediaTypeOrNull(), file)

             //   val requestFile : RequestBody = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
                val fileName: String = "photo_" + System.currentTimeMillis() + ".png"
                val fileToUpload: MultipartBody.Part? = MultipartBody.Part.createFormData("file", fileName, mFile)

                    viewModel.uploadImage(fileToUpload!!)
                count++
            }

        }

        binding!!.confirmAddress!!.imageCount.text = "" + count + " of 9"
        binding!!.addNewAdd!!.imageCount.text = "" + count + " of 9"
        binding!!.operationalHours!!.imageCount.text = "" + count + " of 9"

        var str: String = dataList.get(0).ivPhotos.toString()

        if (str != null && !str.equals("")) {
            Glide.with(context)
                .load(dataList.get(0).ivPhotos)
                .placeholder(R.drawable.add_photos) // Set a placeholder image if needed
                .error(R.drawable.add_photos) // Set an error image if loading fails
                .into(binding!!.confirmAddress!!.addImage)

            Glide.with(context)
                .load(dataList.get(0).ivPhotos)
                .placeholder(R.drawable.add_photos) // Set a placeholder image if needed
                .error(R.drawable.add_photos) // Set an error image if loading fails
                .into(binding!!.addNewAdd!!.addImage)

            Glide.with(context)
                .load(dataList.get(0).ivPhotos)
                .placeholder(R.drawable.add_photos) // Set a placeholder image if needed
                .error(R.drawable.add_photos) // Set an error image if loading fails
                .into(binding!!.operationalHours!!.addImage)
        }
    }

    binding!!.addPicture!!.removePicture.setOnClickListener {

        Log.v("CHECKBOX", "size : " + adapter!!.removedIndex.size)
        if (adapter!!.removedIndex!!.size > 0) {
            adapter!!.removedIndex!!.forEach {
                dataList.remove(it)
            }
            for (i in dataList.size until 9) {
                dataList.add(AddPicturesModel(Uri.EMPTY))
            }
            showAddPictureDialog()
        }

    }

    binding!!.addPicture!!.tvSkip.setOnClickListener {
        dataList.clear()
        for (i in 0 until 9) {
            dataList.add(AddPicturesModel(Uri.EMPTY))
        }
        binding!!.addPicture!!.root.visibility = View.GONE
    }
}


fun HomeFragment.showLandmarkPictureDialog(
    position: Int,
    resulttList: ArrayList<LandmarkDataList>
) {

    binding!!.addPicture!!.root.visibility = View.VISIBLE
    binding!!.addPicture!!.headerTitle.text = "Add Picture to the Landmark"
    binding!!.addPicture!!.addressType.text = addressType
    adapter = AddPicturesAdapter(landMakrImageList, this)
    binding!!.addPicture!!.rvAddPictures.adapter = adapter
    binding!!.addPicture!!.rvAddPictures.layoutManager = GridLayoutManager(context, 4)

    binding!!.addPicture!!.tvAddPhotos!!.setOnClickListener {
        replaceIndex = landMakrImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
        selectImageForLandmakr = true
        openImagePicker()
    }

    binding!!.addPicture!!.tvSave.setOnClickListener {
        binding!!.addPicture!!.root.visibility = View.GONE


            var count = 0
            landMakrImageList.forEach() {
                var str: String = it.ivPhotos.toString()

                if (str != null && !str.equals(""))
                {
                    count++

                }

            }


            var str: String = landMakrImageList.get(0).ivPhotos.toString()

            if (str != null && !str.equals("")) {
                resulttList.get(position).url = str
                resulttList.get(position).imgCount = "" + count

                binding!!.addLandmark.rvLandmark.adapter = LandMarkResultAdapter(resulttList, this)
                binding!!.addLandmark.rvLandmark.adapter!!.notifyDataSetChanged()

            }



    }

    binding!!.addPicture!!.removePicture.setOnClickListener {

        Log.v("CHECKBOX", "size : " + adapter!!.removedIndex.size)
        if (adapter!!.removedIndex!!.size > 0) {
            adapter!!.removedIndex!!.forEach {
                dataList.remove(it)
            }
            for (i in dataList.size until 9) {
                dataList.add(AddPicturesModel(Uri.EMPTY))
            }
            showLandmarkPictureDialog( position
                  ,  resulttList)
        }

    }

    binding!!.addPicture!!.tvSkip.setOnClickListener {
        landMakrImageList.clear()
        for (i in 0 until 9) {
            landMakrImageList.add(AddPicturesModel(Uri.EMPTY))
        }
        binding!!.addPicture!!.root.visibility = View.GONE
    }

}

fun HomeFragment.getAddressImageUploadResponse() {
    viewModel?.imageUploadResponseData?.observe(lifecycleOwner, { response ->
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