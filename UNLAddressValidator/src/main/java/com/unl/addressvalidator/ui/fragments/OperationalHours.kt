package com.unl.addressvalidator.ui.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasewithmodelclassess.model.*
import com.unl.addressvalidator.R
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.ui.adapters.OperationalDayAdapter
import com.unl.addressvalidator.ui.adapters.OperationalHoursAdapter
import com.unl.addressvalidator.util.Constant
import com.unl.addressvalidator.util.Constant.ALLDAY
import com.unl.addressvalidator.util.Constant.ALL_DAYS
import com.unl.addressvalidator.util.Constant.CLOSE
import com.unl.addressvalidator.util.Constant.FRI
import com.unl.addressvalidator.util.Constant.MONDAY
import com.unl.addressvalidator.util.Constant.TUESDAY
import com.unl.addressvalidator.util.Constant.WEDNESDAY
import com.unl.addressvalidator.util.Constant.THURSDAY
import com.unl.addressvalidator.util.Constant.FRIDAY
import com.unl.addressvalidator.util.Constant.FROM_HOURS
import com.unl.addressvalidator.util.Constant.FROM_MINS
import com.unl.addressvalidator.util.Constant.MON
import com.unl.addressvalidator.util.Constant.OPEN
import com.unl.addressvalidator.util.Constant.SAT
import com.unl.addressvalidator.util.Constant.SATURDAY
import com.unl.addressvalidator.util.Constant.SUN
import com.unl.addressvalidator.util.Constant.SUNDAY
import com.unl.addressvalidator.util.Constant.THU
import com.unl.addressvalidator.util.Constant.TO_HOURS
import com.unl.addressvalidator.util.Constant.TO_MINS
import com.unl.addressvalidator.util.Constant.TUES
import com.unl.addressvalidator.util.Constant.WED
import com.unl.addressvalidator.util.Utility
import com.unl.map.sdk.data.VISIBILITY


fun HomeFragment.setAdapters() {
    var daysList = ArrayList<DaySelectionModel>()

    daysList.add(DaySelectionModel(Constant.ALL_DAYS,false))
    daysList.add(DaySelectionModel(Constant.MONDAY,false))
    daysList.add(DaySelectionModel(Constant.TUESDAY,false))
    daysList.add(DaySelectionModel(Constant.WEDNESDAY,false))
    daysList.add(DaySelectionModel(Constant.THURSDAY,false))
    daysList.add(DaySelectionModel(Constant.FRIDAY,false))
    daysList.add(DaySelectionModel(Constant.SATURDAY,false))
    daysList.add(DaySelectionModel(Constant.SUNDAY,false))

    binding!!.operationalHours.rvDays.adapter = OperationalDayAdapter(daysList, this)
    binding!!.operationalHours.rvDays.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    binding!!.operationalHours.rvDays.setBackgroundResource(R.color.white)
    binding!!.operationalHours.rvDays.adapter!!.notifyDataSetChanged()
}

fun HomeFragment.setOperationalHoursClick() {

    binding!!.cvPintHint.visibility = View.GONE
    openCloseTimeList.clear()
    for (i in 0..7) {
        openCloseTimeList.add(
            OpeningHoursSpecificationModel(
                "00:00:AM",
                weekDays.get(i),
                true,
                "00:00:AM"
            )
        )
    }

    binding!!.operationalHours.tvConfirm.setOnClickListener {
        binding!!.operationalHours.root.visibility = View.GONE
        updateOperationalHours()
        mapBoxMap!!.clear()
    }

    binding!!.operationalHours.tvSkip.setOnClickListener {
        binding!!.operationalHours.root.visibility = View.GONE
        updateOperationalHours()
        mapBoxMap!!.clear()
    }
    setAdapters()
    binding!!.operationalHours.rlWheelChair.setOnClickListener(View.OnClickListener {
        selectedAccessibility(
            0
        )
    })
    binding!!.operationalHours.rlAccessibility.setOnClickListener(View.OnClickListener {
        selectedAccessibility(
            1
        )
    })
    binding!!.operationalHours.rlOpeningHours.setOnClickListener {
        binding!!.operationalHours.llFromHours.visibility = View.VISIBLE
        binding!!.operationalHours.llToHours.visibility = View.GONE
        setFromHoursAdapters()
    }

    binding!!.operationalHours.rlClosingHours.setOnClickListener {
        binding!!.operationalHours.llFromHours.visibility = View.GONE
        binding!!.operationalHours.llToHours.visibility = View.VISIBLE
        setToHoursAdapters()
    }
    binding!!.operationalHours.tvfromAM.setOnClickListener {
        fromAMPM = "AM"
        binding!!.operationalHours.tvfromAM.setBackgroundResource(R.drawable.select_hour_bg)
        binding!!.operationalHours.tvfromAM.setTextColor(Color.parseColor("#ffffff"))
        binding!!.operationalHours.tvfromPM.setBackgroundResource(R.color.white)
        binding!!.operationalHours.tvfromPM.setTextColor(Color.parseColor("#444444"))
    }

    binding!!.operationalHours.tvfromPM.setOnClickListener {
        fromAMPM = "PM"
        binding!!.operationalHours.tvfromPM.setBackgroundResource(R.drawable.select_hour_bg)
        binding!!.operationalHours.tvfromPM.setTextColor(Color.parseColor("#ffffff"))
        binding!!.operationalHours.tvfromAM.setBackgroundResource(R.color.white)
        binding!!.operationalHours.tvfromAM.setTextColor(Color.parseColor("#444444"))
    }

    binding!!.operationalHours.tvAM.setOnClickListener {
        toAMPM = "AM"
        binding!!.operationalHours.tvAM.setBackgroundResource(R.drawable.select_hour_bg)
        binding!!.operationalHours.tvAM.setTextColor(Color.parseColor("#ffffff"))
        binding!!.operationalHours.tvPM.setBackgroundResource(R.color.white)
        binding!!.operationalHours.tvPM.setTextColor(Color.parseColor("#444444"))
    }

    binding!!.operationalHours.tvPM.setOnClickListener {
        toAMPM = "PM"
        binding!!.operationalHours.tvPM.setBackgroundResource(R.drawable.select_hour_bg)
        binding!!.operationalHours.tvPM.setTextColor(Color.parseColor("#ffffff"))
        binding!!.operationalHours.tvAM.setBackgroundResource(R.color.white)
        binding!!.operationalHours.tvAM.setTextColor(Color.parseColor("#444444"))
    }

    binding!!.operationalHours.tvsetFromHours.setOnClickListener {

        binding!!.operationalHours.llFromHours.visibility = View.GONE
        binding!!.operationalHours.llToHours.visibility = View.GONE
        binding!!.operationalHours.tvFromHoursText.setText("" + fromHours)
        binding!!.operationalHours.tvFromMinText.setText("" + fromMins)
        binding!!.operationalHours.tvFromAMPMText.setText("" + fromAMPM)
        updateOpeningClosinghours(OPEN)
    }

    binding!!.operationalHours.tvsetToHours.setOnClickListener {
        binding!!.operationalHours.llFromHours.visibility = View.GONE
        binding!!.operationalHours.llToHours.visibility = View.GONE
        binding!!.operationalHours.tvToHoursText.setText("" + toHours)
        binding!!.operationalHours.tvToMinsText.setText("" + toMins)
        binding!!.operationalHours.tvAMPMText.setText("" + toAMPM)
        updateOpeningClosinghours(CLOSE)
    }

    binding!!.operationalHours!!.addImage.setOnClickListener {
        showAddPictureDialog()
    }

    setAddressData()
}

fun HomeFragment.setAddressData() {

  /*  var houseNumber =  createAddressModel!!.address
    var streetAddress =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(0).street_address
    var city =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(0).city_district
    var pincode =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(0).postal_code
    var state =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(0).state_district
    var countryCode =  reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(0).country_code*/

/*    binding!!.addNewAdd!!.edtUnit.setText(houseNumber)
    binding!!.addNewAdd!!.edtStreet.setText(streetAddress)
    binding!!.addNewAdd!!.edtCity.setText(city)
    binding!!.addNewAdd!!.edtPincode.setText(pincode )
    binding!!.addNewAdd!!.edtState.setText(state)
    binding!!.addNewAdd!!.edtCountry.setText(countryCode)
    binding!!.addNewAdd.addressView.visibility = View.VISIBLE*/
    binding!!.operationalHours.tvAddressText.text  = createAddressModel!!.address


}


fun HomeFragment.updateOpeningClosinghours(openCloseType: String) {
    if (openCloseType.equals(OPEN)) {
        if (daysType.equals(ALL_DAYS)) {
            for (i in 0..openCloseTimeList.size - 1) {
                openCloseTimeList.set(
                    i,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins + ":" + fromAMPM,
                        weekDays.get(i),
                        true,
                        openCloseTimeList.get(i).closes
                    )
                )
            }
        } else if (daysType.equals(MONDAY)) {
            openCloseTimeList.set(
                MON,
                OpeningHoursSpecificationModel(
                    fromHours + ":" + fromMins + ":" + fromAMPM,
                    weekDays.get(MON),
                    true,
                    openCloseTimeList.get(MON).closes
                )
            )
        } else if (daysType.equals(TUESDAY)) {
            openCloseTimeList.set(
                TUES,
                OpeningHoursSpecificationModel(
                    fromHours + ":" + fromMins + ":" + fromAMPM,
                    weekDays.get(TUES),
                    true,
                    openCloseTimeList.get(TUES).closes
                )
            )
        } else if (daysType.equals(WEDNESDAY)) {
            openCloseTimeList.set(
                WED,
                OpeningHoursSpecificationModel(
                    fromHours + ":" + fromMins + ":" + fromAMPM,
                    weekDays.get(WED),
                    true,
                    openCloseTimeList.get(WED).closes
                )
            )
        } else if (daysType.equals(THURSDAY)) {
            openCloseTimeList.set(
                THU,
                OpeningHoursSpecificationModel(
                    fromHours + ":" + fromMins + ":" + fromAMPM,
                    weekDays.get(THU),
                    true,
                    openCloseTimeList.get(THU).closes
                )
            )
        } else if (daysType.equals(FRIDAY)) {
            openCloseTimeList.set(
                FRI,
                OpeningHoursSpecificationModel(
                    fromHours + ":" + fromMins + ":" + fromAMPM,
                    weekDays.get(FRI),
                    true,
                    openCloseTimeList.get(FRI).closes
                )
            )
        } else if (daysType.equals(SATURDAY)) {
            openCloseTimeList.set(
                SAT,
                OpeningHoursSpecificationModel(
                    fromHours + ":" + fromMins + ":" + fromAMPM,
                    weekDays.get(SAT),
                    true,
                    openCloseTimeList.get(SAT).closes
                )
            )
        } else if (daysType.equals(SUNDAY)) {
            openCloseTimeList.set(
                SUN,
                OpeningHoursSpecificationModel(
                    fromHours + ":" + fromMins + ":" + fromAMPM,
                    weekDays.get(SUN),
                    true,
                    openCloseTimeList.get(SUN).closes
                )
            )
        }
    } else {
        if (daysType.equals(ALL_DAYS)) {
            for (i in 0..openCloseTimeList.size - 1) {
                openCloseTimeList.set(
                    i,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(i).opens,
                        weekDays.get(i),
                        true,
                        toHours + ":" + toMins + ":" + toAMPM
                    )
                )
            }
        } else if (daysType.equals(MONDAY)) {
            openCloseTimeList.set(
                MON,
                OpeningHoursSpecificationModel(
                    openCloseTimeList.get(MON).opens,
                    weekDays.get(MON),
                    true,
                    toHours + ":" + toMins + ":" + toAMPM
                )
            )
        } else if (daysType.equals(TUESDAY)) {
            openCloseTimeList.set(
                TUES,
                OpeningHoursSpecificationModel(
                    openCloseTimeList.get(TUES).opens,
                    weekDays.get(TUES),
                    true,
                    toHours + ":" + toMins + ":" + toAMPM
                )
            )
        } else if (daysType.equals(WEDNESDAY)) {
            openCloseTimeList.set(
                WED,
                OpeningHoursSpecificationModel(
                    openCloseTimeList.get(WED).opens,
                    weekDays.get(WED),
                    true,
                    toHours + ":" + toMins + ":" + toAMPM
                )
            )
        } else if (daysType.equals(THURSDAY)) {
            openCloseTimeList.set(
                THU,
                OpeningHoursSpecificationModel(
                    openCloseTimeList.get(THU).opens,
                    weekDays.get(THU),
                    true,
                    toHours + ":" + toMins + ":" + toAMPM
                )
            )
        } else if (daysType.equals(FRIDAY)) {
            openCloseTimeList.set(
                FRI,
                OpeningHoursSpecificationModel(
                    openCloseTimeList.get(FRI).opens,
                    weekDays.get(FRI),
                    true,
                    toHours + ":" + toMins + ":" + toAMPM
                )
            )
        } else if (daysType.equals(SATURDAY)) {
            openCloseTimeList.set(
                SAT,
                OpeningHoursSpecificationModel(
                    openCloseTimeList.get(SAT).opens,
                    weekDays.get(SAT),
                    true,
                    toHours + ":" + toMins + ":" + toAMPM
                )
            )
        } else if (daysType.equals(SUNDAY)) {
            openCloseTimeList.set(
                SUN,
                OpeningHoursSpecificationModel(
                    openCloseTimeList.get(SUN).opens,
                    weekDays.get(SUN),
                    true,
                    toHours + ":" + toMins + ":" + toAMPM
                )
            )
        }
    }

}

fun HomeFragment.setFromHoursAdapters() {
    var daysList = ArrayList<String>()
    for (i in 0..12) {
        if (i < 10)
            daysList.add("0" + i)
        else
            daysList.add("" + i)
    }

    var oprationalHoursAdapter: OperationalHoursAdapter = OperationalHoursAdapter(
        daysList, this,
        FROM_HOURS
    )
    binding!!.operationalHours.rvFromHour.adapter = oprationalHoursAdapter
    binding!!.operationalHours.rvFromHour.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    binding!!.operationalHours.rvFromHour.setBackgroundResource(R.color.white)
    oprationalHoursAdapter.selectedIndex = returnHoursIndex(true).toInt()
    binding!!.operationalHours.rvFromHour.adapter!!.notifyDataSetChanged()
    setFromMinsAdapters()
}

fun HomeFragment.setToHoursAdapters() {
    var daysList = ArrayList<String>()
    for (i in 0..12) {
        if (i < 10)
            daysList.add("0" + i)
        else
            daysList.add("" + i)
    }
   var operationalHoursAdapter : OperationalHoursAdapter = OperationalHoursAdapter(daysList, this, TO_HOURS)
    binding!!.operationalHours.rvToHour.adapter = operationalHoursAdapter
    binding!!.operationalHours.rvToHour.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    binding!!.operationalHours.rvToHour.setBackgroundResource(R.color.white)
    operationalHoursAdapter.selectedIndex = returnHoursIndex(false).toInt()
    binding!!.operationalHours.rvToHour.adapter!!.notifyDataSetChanged()
    setToMinsAdapters()
}


fun HomeFragment.setFromMinsAdapters() {
    var daysList = ArrayList<String>()
    for (i in 0..59) {
        if (i < 10)
            daysList.add("0" + i)
        else
            daysList.add("" + i)
    }
  var  operationalHoursAdapter : OperationalHoursAdapter = OperationalHoursAdapter(daysList, this, FROM_MINS)
    binding!!.operationalHours.rvFromMin.adapter = operationalHoursAdapter

    binding!!.operationalHours.rvFromMin.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    binding!!.operationalHours.rvFromMin.setBackgroundResource(R.color.white)
    operationalHoursAdapter.selectedIndex = returnMinsIndex(true).toInt()
    binding!!.operationalHours.rvFromMin.adapter!!.notifyDataSetChanged()
}


fun HomeFragment.setToMinsAdapters() {
    var daysList = ArrayList<String>()

    for (i in 0..59) {
        if (i < 10)
            daysList.add("0" + i)
        else
            daysList.add("" + i)
    }
   var operationalHoursAdapter : OperationalHoursAdapter = OperationalHoursAdapter(daysList, this, TO_MINS)
    binding!!.operationalHours.rvToMin.adapter = operationalHoursAdapter
    binding!!.operationalHours.rvToMin.layoutManager =
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    binding!!.operationalHours.rvToMin.setBackgroundResource(R.color.white)
    operationalHoursAdapter.selectedIndex = returnMinsIndex(false).toInt()
    binding!!.operationalHours.rvToMin.adapter!!.notifyDataSetChanged()
}

fun HomeFragment.selectedAccessibility(position: Int) {
    binding!!.operationalHours!!.rlWheelChair.setBackgroundResource(R.drawable.address_label_shape)
    binding!!.operationalHours!!.rlAccessibility.setBackgroundResource(R.drawable.address_label_shape)
    binding!!.operationalHours!!.ivWheelChair.setImageTintList(
        ColorStateList.valueOf(
            resources.getColor(
                R.color.black2
            )
        )
    )
    binding!!.operationalHours!!.ivElevator.setImageTintList(
        ColorStateList.valueOf(
            resources.getColor(
                R.color.black2
            )
        )
    )
    binding!!.operationalHours!!.tvElevator.setTextColor(resources.getColor(R.color.black2))
    binding!!.operationalHours!!.tvAccessibility.setTextColor(resources.getColor(R.color.black2))
    binding!!.operationalHours!!.ivCheckElevator.setImageResource(R.drawable.labelunchecked)
    binding!!.operationalHours!!.ivCheckWheelchair.setImageResource(R.drawable.labelunchecked)

    var img: Drawable? = null

    if (position == 0) {
        accessibilityType = "wheelchair"
        binding!!.operationalHours!!.rlWheelChair.setBackgroundResource(R.drawable.label_selected_shape)
        binding!!.operationalHours!!.ivWheelChair.setImageTintList(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.purple1
                )
            )
        )
        binding!!.operationalHours!!.tvAccessibility.setTextColor(resources.getColor(R.color.purple1))
        binding!!.operationalHours!!.ivCheckWheelchair.setImageResource(R.drawable.labelcheck)
        img = getContext().getResources().getDrawable(R.drawable.home)

    } else if (position == 1) {
        accessibilityType = "elevator"
        binding!!.operationalHours!!.rlAccessibility.setBackgroundResource(R.drawable.label_selected_shape)
        binding!!.operationalHours!!.ivElevator.setImageTintList(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.purple1
                )
            )
        )
        binding!!.operationalHours!!.tvElevator.setTextColor(resources.getColor(R.color.purple1))
        binding!!.operationalHours!!.ivCheckElevator.setImageResource(R.drawable.labelcheck)

    }


}


fun HomeFragment.returnHoursIndex( opening : Boolean): String {
    var position = "-1"
   try {
       if(opening)
       {
           if(daysType.equals(ALL_DAYS))
           {
               var splitArray = openCloseTimeList.get(ALLDAY).opens.split(":")
               position = splitArray[0]
           }else if(daysType.equals(MONDAY))
           {
               var splitArray = openCloseTimeList.get(MON).opens.split(":")
               position = splitArray[0]
           }else if(daysType.equals(TUESDAY))
           {
               var splitArray = openCloseTimeList.get(TUES).opens.split(":")
               position = splitArray[0]
           }else if(daysType.equals(WEDNESDAY))
           {
               var splitArray = openCloseTimeList.get(WED).opens.split(":")
               position = splitArray[0]
           }else if(daysType.equals(THURSDAY))
           {
               var splitArray = openCloseTimeList.get(THU).opens.split(":")
               position = splitArray[0]
           }else if(daysType.equals(FRIDAY))
           {
               var splitArray = openCloseTimeList.get(FRI).opens.split(":")
               position = splitArray[0]
           }else if(daysType.equals(SATURDAY))
           {
               var splitArray = openCloseTimeList.get(SAT).opens.split(":")
               position = splitArray[0]
           }else if(daysType.equals(SUNDAY))
           {
               var splitArray = openCloseTimeList.get(SUN).opens.split(":")
               position = splitArray[0]
           }
       }else
       {
           if(daysType.equals(ALL_DAYS))
           {
               var splitArray = openCloseTimeList.get(ALLDAY).closes.split(":")
               position = splitArray[0]
           }else if(daysType.equals(MONDAY))
           {
               var splitArray = openCloseTimeList.get(MON).closes.split(":")
               position = splitArray[0]
           }else if(daysType.equals(TUESDAY))
           {
               var splitArray = openCloseTimeList.get(TUES).closes.split(":")
               position = splitArray[0]
           }else if(daysType.equals(WEDNESDAY))
           {
               var splitArray = openCloseTimeList.get(WED).closes.split(":")
               position = splitArray[0]
           }else if(daysType.equals(THURSDAY))
           {
               var splitArray = openCloseTimeList.get(THU).closes.split(":")
               position = splitArray[0]
           }else if(daysType.equals(FRIDAY))
           {
               var splitArray = openCloseTimeList.get(FRI).closes.split(":")
               position = splitArray[0]
           }else if(daysType.equals(SATURDAY))
           {
               var splitArray = openCloseTimeList.get(SAT).closes.split(":")
               position = splitArray[0]
           }else if(daysType.equals(SUNDAY))
           {
               var splitArray = openCloseTimeList.get(SUN).closes.split(":")
               position = splitArray[0]
           }
       }

   }
   catch (e:java.lang.Exception)
   {
       e.printStackTrace()
   }

    return position
}


fun HomeFragment.returnMinsIndex( opening : Boolean): String {
    var position = "-1"
   try {
       if(opening)
       {
           if(daysType.equals(ALL_DAYS))
           {
               var splitArray = openCloseTimeList.get(ALLDAY).opens.split(":")
               position = splitArray[1]
           }else if(daysType.equals(MONDAY))
           {
               var splitArray = openCloseTimeList.get(MON).opens.split(":")
               position = splitArray[1]
           }else if(daysType.equals(TUESDAY))
           {
               var splitArray = openCloseTimeList.get(TUES).opens.split(":")
               position = splitArray[1]
           }else if(daysType.equals(WEDNESDAY))
           {
               var splitArray = openCloseTimeList.get(WED).opens.split(":")
               position = splitArray[1]
           }else if(daysType.equals(THURSDAY))
           {
               var splitArray = openCloseTimeList.get(THU).opens.split(":")
               position = splitArray[1]
           }else if(daysType.equals(FRIDAY))
           {
               var splitArray = openCloseTimeList.get(FRI).opens.split(":")
               position = splitArray[1]
           }else if(daysType.equals(SATURDAY))
           {
               var splitArray = openCloseTimeList.get(SAT).opens.split(":")
               position = splitArray[1]
           }else if(daysType.equals(SUNDAY))
           {
               var splitArray = openCloseTimeList.get(SUN).opens.split(":")
               position = splitArray[1]
           }
       }else
       {
           if(daysType.equals(ALL_DAYS))
           {
               var splitArray = openCloseTimeList.get(ALLDAY).closes.split(":")
               position = splitArray[1]
           }else if(daysType.equals(MONDAY))
           {
               var splitArray = openCloseTimeList.get(MON).closes.split(":")
               position = splitArray[1]
           }else if(daysType.equals(TUESDAY))
           {
               var splitArray = openCloseTimeList.get(TUES).closes.split(":")
               position = splitArray[1]
           }else if(daysType.equals(WEDNESDAY))
           {
               var splitArray = openCloseTimeList.get(WED).closes.split(":")
               position = splitArray[1]
           }else if(daysType.equals(THURSDAY))
           {
               var splitArray = openCloseTimeList.get(THU).closes.split(":")
               position = splitArray[1]
           }else if(daysType.equals(FRIDAY))
           {
               var splitArray = openCloseTimeList.get(FRI).closes.split(":")
               position = splitArray[1]
           }else if(daysType.equals(SATURDAY))
           {
               var splitArray = openCloseTimeList.get(SAT).closes.split(":")
               position = splitArray[1]
           }else if(daysType.equals(SUNDAY))
           {
               var splitArray = openCloseTimeList.get(SUN).closes.split(":")
               position = splitArray[1]
           }
       }

   }
   catch (e:java.lang.Exception)
   {
       e.printStackTrace()
   }

    return position
}


fun HomeFragment.returnAmPm( opening : Boolean): String {
    var position = "AM"
    try {
        if(opening)
        {
            if(daysType.equals(ALL_DAYS))
            {
                var splitArray = openCloseTimeList.get(ALLDAY).opens.split(":")
                position = splitArray[2]
            }else if(daysType.equals(MONDAY))
            {
                var splitArray = openCloseTimeList.get(MON).opens.split(":")
                position = splitArray[2]
            }else if(daysType.equals(TUESDAY))
            {
                var splitArray = openCloseTimeList.get(TUES).opens.split(":")
                position = splitArray[2]
            }else if(daysType.equals(WEDNESDAY))
            {
                var splitArray = openCloseTimeList.get(WED).opens.split(":")
                position = splitArray[2]
            }else if(daysType.equals(THURSDAY))
            {
                var splitArray = openCloseTimeList.get(THU).opens.split(":")
                position = splitArray[2]
            }else if(daysType.equals(FRIDAY))
            {
                var splitArray = openCloseTimeList.get(FRI).opens.split(":")
                position = splitArray[2]
            }else if(daysType.equals(SATURDAY))
            {
                var splitArray = openCloseTimeList.get(SAT).opens.split(":")
                position = splitArray[2]
            }else if(daysType.equals(SUNDAY))
            {
                var splitArray = openCloseTimeList.get(SUN).opens.split(":")
                position = splitArray[2]
            }
        }else
        {
            if(daysType.equals(ALL_DAYS))
            {
                var splitArray = openCloseTimeList.get(ALLDAY).closes.split(":")
                position = splitArray[2]
            }else if(daysType.equals(MONDAY))
            {
                var splitArray = openCloseTimeList.get(MON).closes.split(":")
                position = splitArray[2]
            }else if(daysType.equals(TUESDAY))
            {
                var splitArray = openCloseTimeList.get(TUES).closes.split(":")
                position = splitArray[2]
            }else if(daysType.equals(WEDNESDAY))
            {
                var splitArray = openCloseTimeList.get(WED).closes.split(":")
                position = splitArray[2]
            }else if(daysType.equals(THURSDAY))
            {
                var splitArray = openCloseTimeList.get(THU).closes.split(":")
                position = splitArray[2]
            }else if(daysType.equals(FRIDAY))
            {
                var splitArray = openCloseTimeList.get(FRI).closes.split(":")
                position = splitArray[2]
            }else if(daysType.equals(SATURDAY))
            {
                var splitArray = openCloseTimeList.get(SAT).closes.split(":")
                position = splitArray[2]
            }else if(daysType.equals(SUNDAY))
            {
                var splitArray = openCloseTimeList.get(SUN).closes.split(":")
                position = splitArray[2]
            }
        }

    }
    catch (e:java.lang.Exception)
    {
        e.printStackTrace()
    }

    return position
}



fun HomeFragment.setFromAndToTime()
{

    binding!!.operationalHours.tvFromHoursText.setText("" + returnHoursIndex(true))
    binding!!.operationalHours.tvFromMinText.setText("" + returnMinsIndex(true))

    fromAMPM =  returnAmPm(true)
    binding!!.operationalHours.tvFromAMPMText.setText("" +fromAMPM)

    toAMPM =  returnAmPm(false)

    binding!!.operationalHours.tvToHoursText.setText("" +  returnHoursIndex(false))
    binding!!.operationalHours.tvToMinsText.setText("" + returnMinsIndex(false))
    binding!!.operationalHours.tvAMPMText.setText("" + toAMPM)

}



fun HomeFragment.updateOperationalHours() {

    openCloseTimeList.removeAt(0)

    for(i in 0..6)
    {
        if(openCloseTimeList.get(i).opens.equals(openCloseTimeList.get(i).closes))
        {
            openCloseTimeList.get(i).isHoliday = true
        }
    }
    createAddressModel!!.openingHoursSpecificationModel = openCloseTimeList
    viewModel.insertAddress(database, createAddressModel!!)
}


