package com.unl.addressvalidator.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabasewithmodelclassess.model.DaySelectionModel
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.ui.interfaces.OperationHoursClickListner
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner
import com.unl.addressvalidator.util.Constant


class OperationalDayAdapter(
     val daysList: ArrayList<DaySelectionModel>,
    private val itemClickListner: OperationHoursClickListner,
) : RecyclerView.Adapter<OperationalDayAdapter.ViewHolder>() {

   // var selectedIndex = -1
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
            tvDays.text = daysList.get(position).days
            if (daysList.get(position).isSelected) {
                tvDays.setBackgroundResource(R.drawable.theme_round_btn)
                tvDays.setTextColor( Color.parseColor("#ffffff"))
            } else {
                tvDays.setBackgroundResource(R.drawable.bg_button_disable)
                tvDays.setTextColor( Color.parseColor("#444444"))
            }

            tvDays.setOnClickListener {
               // selectedIndex = position
                if(daysList.get(position).days.equals(Constant.ALL_DAYS) && daysList.get(position).isSelected == false)
                {
                    daysList.get(position).isSelected = true
                    for (i in 1.. daysList.size-1)
                    {
                        daysList.get(i).isSelected = false
                    }
                }else if(daysList.get(position).days.equals(Constant.ALL_DAYS) && daysList.get(position).isSelected == true)
                {
                    daysList.get(position).isSelected = false

                }else
                {
                    daysList.get(0).isSelected = false
                    if(daysList.get(position).isSelected)
                    {
                        daysList.get(position).isSelected = false
                    }else
                    {
                        daysList.get(position).isSelected = true
                    }
                }
                itemClickListner.dayClick(daysList.get(position).days)
                notifyDataSetChanged()
            }
        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = daysList.size

    // ---------------------------------------------------------------------------------------------

}