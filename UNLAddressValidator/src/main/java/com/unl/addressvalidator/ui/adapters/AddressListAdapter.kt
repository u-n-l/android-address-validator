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
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.ui.interfaces.AddressItemClickListner


class AddressListAdapter(
    private val resulttList: List<CreateAddressModel>,
    private val itemClickListner: AddressItemClickListner,
) : RecyclerView.Adapter<AddressListAdapter.ViewHolder>() {

    var selectedIndex = -1
    // --
    // -------------------------------------------------------------------------------------------

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAddressDetails: TextView = view.findViewById(R.id.tvAddressDetails)
        val llMain: LinearLayout = view.findViewById(R.id.llMain)
        val ivAdressType: ImageView = view.findViewById(R.id.ivAdressType)
        val addressPic: ImageView = view.findViewById(R.id.addressPic)
        val imgCount: TextView = view.findViewById(R.id.imgCount)
        val tvAddress: TextView = view.findViewById(R.id.tvAddress)
    }

    // ---------------------------------------------------------------------------------------------

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.address_item, viewGroup, false)

        return ViewHolder(view)
    }

    // ---------------------------------------------------------------------------------------------

    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.run {
            // image.setImageBitmap(dataSet[position].imageAsBitmap(imageSize))
            tvAddress.text = resulttList.get(position).addressType
            tvAddressDetails.text = resulttList.get(position).address
            if (resulttList.get(position).images != null && resulttList.get(position)!!.images!!.size > 0) {
                imgCount.text = "" + resulttList.get(position)!!.images!!.size + " of 9"
                Glide.with(itemView)
                    .load(resulttList.get(position).images!!.get(0))
                    .placeholder(R.drawable.photos) // Set a placeholder image if needed
                    .error(R.drawable.photos) // Set an error image if loading fails
                    .into(addressPic)
            } else {
                Glide.with(itemView)
                    .load(R.drawable.photos)
                    .placeholder(R.drawable.photos) // Set a placeholder image if needed
                    .error(R.drawable.photos) // Set an error image if loading fails
                    .into(addressPic)
                imgCount.text = "0 of 9"
            }

            if (selectedIndex == position)
                llMain.setBackgroundResource(R.drawable.label_selected_shape)
            else
                llMain.setBackgroundResource(R.drawable.address_label_shape)

            llMain.setOnClickListener {
                itemClickListner.addressItemClick(resulttList.get(position))
                llMain.setBackgroundResource(R.drawable.label_selected_shape)
                selectedIndex = position
                notifyDataSetChanged()
            }

            if (resulttList.get(position).addressType.equals("home")) {
                ivAdressType.setImageResource(R.drawable.home)
            }else  if (resulttList.get(position).addressType.equals("office"))
            {
                ivAdressType.setImageResource(R.drawable.ic_office)
            }


        }
    }


    // ---------------------------------------------------------------------------------------------

    override fun getItemCount() = resulttList.size

    // ---------------------------------------------------------------------------------------------

}