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
import com.example.roomdatabasewithmodelclassess.model.EntranceModel
import com.google.gson.internal.LinkedTreeMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.ui.interfaces.EntranceClickListner
import com.unl.addressvalidator.ui.interfaces.LandmarkClickListner


class EntrancesListAdapter(
    private val resulttList: ArrayList<EntranceModel>,
    private val itemClickListner: EntranceClickListner,
) : RecyclerView.Adapter<EntrancesListAdapter.ViewHolder>() {

    var selectedIndex = -1
    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEntranceName: TextView = view.findViewById(R.id.tvEntranceName)
        val tvEntranceID: TextView = view.findViewById(R.id.tvEntranceID)
        val ivEdit: ImageView = view.findViewById(R.id.ivEdit)
        val entrancePic: ImageView = view.findViewById(R.id.entrancePic)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
        val imgCount: TextView = view.findViewById(R.id.imgCount)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.entrance_item, viewGroup, false)

        return ViewHolder(view)
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))
           try {

               ivEdit.visibility = View.INVISIBLE
               ivDelete.visibility = View.INVISIBLE
               val getrow: Any = resulttList.get(position)
               val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
               val entranceName = t["entranceName"].toString()
               val entranceNo = t["entranceNo"].toString()
               val entranceId = t["entranceId"].toString()
               val imgCountt = t["imgCount"].toString()
               val url = t["url"].toString()

               tvEntranceName.text = entranceName + " No. "+ entranceNo
               tvEntranceID.text = ""+entranceId

               if(imgCountt!= null && !imgCountt.equals(""))
                   imgCount.text = ""+imgCountt + " of 9"
               else
                   imgCount.text =  "0 of 9"

               entrancePic.setOnClickListener {
                   itemClickListner.entranceImageClick(position,resulttList)
               }

               if(url!= null && !url.equals(""))
               {
                   Glide.with(itemView)
                       .load(url)
                       .placeholder(R.drawable.photos) // Set a placeholder image if needed
                       .error(R.drawable.photos) // Set an error image if loading fails
                       .into(entrancePic)
               }else
               {
                   Glide.with(itemView)
                       .load(R.drawable.photos)
                       .placeholder(R.drawable.photos) // Set a placeholder image if needed
                       .error(R.drawable.photos) // Set an error image if loading fails
                       .into(entrancePic)
               }

           }
           catch (e:java.lang.Exception)
           {
               e.printStackTrace()
           }

        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = resulttList.size

    // ---------------------------------------------------------------------------------------------

}