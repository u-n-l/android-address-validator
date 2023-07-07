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


class OperationalDayAdapter(
    private val DaysList: ArrayList<String>,
    private val itemClickListner: OperationHoursClickListner,
) : RecyclerView.Adapter<OperationalDayAdapter.ViewHolder>() {

    var selectedIndex = -1
    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDays: TextView = view.findViewById(R.id.tvDays)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.day_item, viewGroup, false)

        return ViewHolder(view)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))
            tvDays.text = DaysList.get(position)
            if (selectedIndex == position) {
                tvDays.setBackgroundResource(R.drawable.theme_round_btn)
                tvDays.setTextColor( Color.parseColor("#ffffff"))
            } else {
                tvDays.setBackgroundResource(R.drawable.bg_button_disable)
                tvDays.setTextColor( Color.parseColor("#444444"))
            }

            tvDays . setOnClickListener {
                itemClickListner.dayClick(DaysList.get(position))
                selectedIndex = position
                notifyDataSetChanged()
            }
        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = DaysList.size

    // ---------------------------------------------------------------------------------------------

}