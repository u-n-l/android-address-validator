package com.unl.addressvalidator.ui.imagepicker.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.core.os.persistableBundleOf
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.unl.addressvalidator.R

import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.ui.interfaces.AddressImageClickListner
import com.unl.addressvalidator.ui.interfaces.LandmarkClickListner

class AddPicturesAdapter(
    private val dataList: ArrayList<AddPicturesModel>,
    private val addressImageClickLlistner: AddressImageClickListner,
) :
    RecyclerView.Adapter<AddPicturesAdapter.ViewHolder>() {

    var removedIndex = ArrayList<AddPicturesModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_pictures_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.ivPhotos)

        private val checkBox: CheckBox = itemView.findViewById(R.id.ivCheckbox)

        fun bind(data: AddPicturesModel) {
            val imageUri = data.ivPhotos

            Glide.with(itemView)
                .load(imageUri)
                .placeholder(R.drawable.add_photos) // Set a placeholder image if needed
                .error(R.drawable.add_photos) // Set an error image if loading fails
                .into(imageView)

            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setOnClickListener {
                addressImageClickLlistner.addressImageClick()
            }
            var str: String = imageUri.toString()

            if (str != null && !str.equals(""))
                checkBox.visibility = View.VISIBLE
            else
                checkBox.visibility = View.GONE

            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    removedIndex.add(data)
                    Log.v("CHECKBOX", "uri : " + data.ivPhotos)
                } else {
                    removedIndex.remove(data)
                }

            }
        }


    }
}
