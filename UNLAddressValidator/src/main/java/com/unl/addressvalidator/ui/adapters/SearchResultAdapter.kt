package com.unl.addressvalidator.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner


class SearchResultAdapter(
    private val resulttList: ArrayList<AutocompleteData>,
    private val itemClickListner: SearchItemClickListner,
) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {


    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        val cvMain: LinearLayout = view.findViewById(R.id.cvMain)

    }

    // ---------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.search_item, viewGroup, false)

        return ViewHolder(view)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))
            tvAddress.text = resulttList.get(position).suggestion

            cvMain.setOnClickListener {
                itemClickListner.searchItemClick(resulttList.get(position))
            }
        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = resulttList.size

    // ---------------------------------------------------------------------------------------------

}