package com.unl.addressvalidator.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.ui.interfaces.OperationHoursClickListner
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner


class OperationalHoursAdapter(
    private val DaysList: ArrayList<String>,
    private val itemClickListner: OperationHoursClickListner,
    private val type : String
) : RecyclerView.Adapter<OperationalHoursAdapter.ViewHolder>() {

    var selectedIndex = -1
    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDays: TextView = view.findViewById(R.id.tvDays)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.hours_item, viewGroup, false)

        return ViewHolder(view)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))
            tvDays.text = DaysList.get(position)
            if (selectedIndex == position) {
                tvDays.setBackgroundResource(R.drawable.select_hour_bg)
                tvDays.setTextColor( Color.parseColor("#ffffff"))
            } else {
                tvDays.setBackgroundResource(R.color.white)
                tvDays.setTextColor( Color.parseColor("#444444"))
            }

            tvDays.setOnClickListener {
                itemClickListner.itemClick(DaysList.get(position),type)
                selectedIndex = position
                notifyDataSetChanged()
            }
        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = DaysList.size

    // ---------------------------------------------------------------------------------------------

}