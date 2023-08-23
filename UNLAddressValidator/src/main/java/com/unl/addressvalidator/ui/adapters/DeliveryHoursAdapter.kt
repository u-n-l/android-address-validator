package com.unl.addressvalidator.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabasewithmodelclassess.model.OpeningHoursSpecificationModel
import com.google.gson.internal.LinkedTreeMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.ui.interfaces.OperationHoursClickListner
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner


class DeliveryHoursAdapter(
    private val DaysList: ArrayList<OpeningHoursSpecificationModel>
) : RecyclerView.Adapter<DeliveryHoursAdapter.ViewHolder>() {


    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOpenClosehours: TextView = view.findViewById(R.id.tvOpenClosehours)
        val tvDayName: TextView = view.findViewById(R.id.tvDayName)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.delivery_hours_item, viewGroup, false)

        return ViewHolder(view)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))

            val getrow: Any = DaysList.get(position)
            val t: LinkedTreeMap<Any, Any> = getrow as LinkedTreeMap<Any, Any>
            val opens = t["opens"].toString()
            val closes = t["closes"].toString()
            val day = t["dayOfWeek"].toString()
            tvDayName.text = day
            tvOpenClosehours.text = opens+"-"+closes



        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = DaysList.size

    // ---------------------------------------------------------------------------------------------

}