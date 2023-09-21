package com.unl.addressvalidator.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.roomdatabasewithmodelclassess.model.LandmarkModel
import com.google.gson.internal.LinkedTreeMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.ui.interfaces.LandmarkClickListner


class LandMarksAdapter(
    private val resulttList: ArrayList<LandmarkModel>,
    private val itemClickListner: LandmarkClickListner,
) : RecyclerView.Adapter<LandMarksAdapter.ViewHolder>() {

    var selectedIndex = ArrayList<Int>()
    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
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

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))

            val getrow: Any = resulttList.get(position)
            val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
            val landmark_name = t["landmark_name"].toString()
            val list : ArrayList<String> = t["imageList"] as  ArrayList<String>


            try {
               tvPlaceName.text = landmark_name
           }
           catch (e :java.lang.Exception)
           {
               e.printStackTrace()
           }
          if(list!= null )
            imgCount.text = ""+list.size + " of 9"
            else
              imgCount.text =  "0 of 9"

            if (selectedIndex.contains(position))
                llMain.setBackgroundResource(R.drawable.label_selected_shape)
            else
                llMain.setBackgroundResource(R.drawable.address_label_shape)

                llMain.setOnClickListener {
                   /* itemClickListner.landmarkItemClick(resulttList.get(position))
                   if(selectedIndex.contains(position))
                   {
                       selectedIndex.remove(position)
                   }else
                   {
                       selectedIndex.add(position)
                   }*/

                  //  notifyDataSetChanged()
                }

            if(list!= null && list.size>0 && !list.get(0).equals(""))
            {
                Glide.with(itemView)
                    .load(list.get(0))
                    .placeholder(R.drawable.photos) // Set a placeholder image if needed
                    .error(R.drawable.photos) // Set an error image if loading fails
                    .into(landmarkPic)
            }else
            {
                Glide.with(itemView)
                    .load(R.drawable.photos)
                    .placeholder(R.drawable.photos) // Set a placeholder image if needed
                    .error(R.drawable.photos) // Set an error image if loading fails
                    .into(landmarkPic)
            }


            landmarkPic.setOnClickListener {
                    itemClickListner.viewLandMarkPic(position,resulttList)
                }
        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = resulttList.size

    // ---------------------------------------------------------------------------------------------

}