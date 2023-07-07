package com.unl.addressvalidator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.ui.interfaces.LandmarkClickListner


class LandMarkResultAdapter(
    private val resulttList: ArrayList<LandmarkDataList>,
    private val itemClickListner: LandmarkClickListner,
) : RecyclerView.Adapter<LandMarkResultAdapter.ViewHolder>() {

    var selectedIndex = -1
    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlaceName: TextView = view.findViewById(R.id.tvPlaceName)
        val llMain: LinearLayout = view.findViewById(R.id.llMain)
        val landmarkPic: ImageView = view.findViewById(R.id.landmarkPic)
        val imgCount: TextView = view.findViewById(R.id.imgCount)

    }

    // ---------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.landmark_item, viewGroup, false)

        return ViewHolder(view)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))
            tvPlaceName.text = resulttList.get(position).addressInfo.features.get(0).properties.category_name
          if(resulttList.get(position).imgCount!= null && !resulttList.get(position).imgCount.equals(""))
            imgCount.text = ""+resulttList.get(position).imgCount + " of 9"

            if (selectedIndex == position)
                llMain.setBackgroundResource(R.drawable.label_selected_shape)
            else
                llMain.setBackgroundResource(R.drawable.address_label_shape)

                llMain.setOnClickListener {
                    itemClickListner.landmarkItemClick(resulttList.get(position).addressInfo)
                    llMain.setBackgroundResource(R.drawable.label_selected_shape)
                    selectedIndex = position
                    notifyDataSetChanged()
                }
            if(resulttList.get(position).url!= null && !resulttList.get(position).url.equals(""))
            {
                Glide.with(itemView)
                    .load(resulttList.get(position).url)
                    .placeholder(R.drawable.add_photos) // Set a placeholder image if needed
                    .error(R.drawable.add_photos) // Set an error image if loading fails
                    .into(landmarkPic)
            }


            landmarkPic.setOnClickListener {
                    itemClickListner.uploadLandmarkPic(position,resulttList)
                }
        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = resulttList.size

    // ---------------------------------------------------------------------------------------------

}