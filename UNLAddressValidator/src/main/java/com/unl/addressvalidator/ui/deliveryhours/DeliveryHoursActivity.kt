package com.unl.addressvalidator.ui.deliveryhours

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.roomdatabasewithmodelclassess.model.OpeningHoursSpecificationModel
import com.unl.addressvalidator.R
import com.unl.addressvalidator.database.UnlAddressDatabase
import com.unl.addressvalidator.databinding.ActivityDeliveryHoursBinding
import com.unl.addressvalidator.databinding.AddPicturesPopupBinding
import com.unl.addressvalidator.ui.adapters.OperationalDayAdapter
import com.unl.addressvalidator.ui.adapters.OperationalHoursAdapter
import com.unl.addressvalidator.ui.fragments.HomeFragment
import com.unl.addressvalidator.ui.fragments.showAddPictureDialog
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.createAddressModel
import com.unl.addressvalidator.ui.homescreen.ValidatorViewModel
import com.unl.addressvalidator.ui.homescreen.showAddPictureDialog
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.builder.MultiImagePicker
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.ui.interfaces.AddressImageClickListner
import com.unl.addressvalidator.ui.interfaces.OperationHoursClickListner
import com.unl.addressvalidator.util.Constant
import com.unl.addressvalidator.util.Utility
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


class DeliveryHoursActivity : AppCompatActivity(), OperationHoursClickListner,
    AddressImageClickListner {
    var binding: ActivityDeliveryHoursBinding? = null
    lateinit var viewModel: DeliveryHoursViewModel
    lateinit var database: UnlAddressDatabase
    lateinit var operationalDayAdapter : OperationalDayAdapter
    var isReplace: Boolean = false

    var openCloseTimeList: ArrayList<OpeningHoursSpecificationModel> = ArrayList()
    var weekDays = arrayOf(
        Constant.ALL_DAYS,
        Constant.MONDAY,
        Constant.TUESDAY,
        Constant.WEDNESDAY,
        Constant.THURSDAY,
        Constant.FRIDAY,
        Constant.SATURDAY,
        Constant.SUNDAY
    )
    var daysType = ""
    var fromHours = "09"
    var fromMins = "00"
    var fromAMPM = "AM"
    var toHours = "18"
    var toMins = "00"
    var toAMPM = "AM"
    var accessibilityType = ""
    lateinit var adapter: AddPicturesAdapter
    val dataListSize = 9
    var replaceIndex: Int = 0
    var isWheelchairSelected = false
    var isElevatorSelected = false
    lateinit  var bind : AddPicturesPopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryHoursBinding.inflate(getLayoutInflater())
        setContentView(binding!!.root)
        binding!!.backBtn.setOnClickListener {
            finish()
        }
        database = UnlAddressDatabase.getInstance(this)
        initiateViewModel()
        setOperationalHoursClick()
    }

    private fun initiateViewModel() {

        viewModel = ViewModelProvider(this)[DeliveryHoursViewModel::class.java]


    }


    fun setAdapters() {
        var daysList = ArrayList<String>()

        daysList.add(Constant.ALL_DAYS)
        daysList.add(Constant.MONDAY)
        daysList.add(Constant.TUESDAY)
        daysList.add(Constant.WEDNESDAY)
        daysList.add(Constant.THURSDAY)
        daysList.add(Constant.FRIDAY)
        daysList.add(Constant.SATURDAY)
        daysList.add(Constant.SUNDAY)

        operationalDayAdapter = OperationalDayAdapter(daysList, this)
        binding!!.operationalHours.rvDays.adapter = operationalDayAdapter
        binding!!.operationalHours.rvDays.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding!!.operationalHours.rvDays.setBackgroundResource(R.color.white)
        binding!!.operationalHours.rvDays.adapter!!.notifyDataSetChanged()
    }

    fun setOperationalHoursClick() {


        openCloseTimeList.clear()
        for (i in 0..7) {
            openCloseTimeList.add(
                OpeningHoursSpecificationModel(
                    "09:00",
                    weekDays.get(i),
                    true,
                    "18:00"
                )
            )
        }

        binding!!.operationalHours.tvConfirm.setOnClickListener {
            //  binding!!.operationalHours.root.visibility = View.GONE
            submitData()

        }

        binding!!.operationalHours.tvSkip.setOnClickListener {
            // binding!!.operationalHours.root.visibility = View.GONE
            submitData()

        }
        binding!!.operationalHours.editAddress.setOnClickListener {

        }
        setAdapters()
        binding!!.operationalHours.rlWheelChair.setOnClickListener(View.OnClickListener {
            if(isWheelchairSelected)
                isWheelchairSelected = false
            else
                isWheelchairSelected = true
            selectedAccessibility()
        })
        binding!!.operationalHours.rlAccessibility.setOnClickListener(View.OnClickListener {
            if(isElevatorSelected)
                isElevatorSelected = false
            else
                isElevatorSelected = true
            selectedAccessibility()
        })
        binding!!.operationalHours.rlOpeningHours.setOnClickListener {

            if(operationalDayAdapter.selectedIndex >= 0)
            {
                binding!!.operationalHours.llFromHours.visibility = View.VISIBLE
                binding!!.operationalHours.llToHours.visibility = View.GONE
                setFromHoursAdapters()
            }else
            {
                Toast.makeText(this,"Please select day",Toast.LENGTH_SHORT).show()
            }

        }

        binding!!.operationalHours.rlClosingHours.setOnClickListener {

            if(operationalDayAdapter.selectedIndex >= 0)
            {
                binding!!.operationalHours.llFromHours.visibility = View.GONE
                binding!!.operationalHours.llToHours.visibility = View.VISIBLE
                setToHoursAdapters()
            }else
            {
                Toast.makeText(this,"Please select day",Toast.LENGTH_SHORT).show()
            }

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
            updateOpeningClosinghours(Constant.OPEN)
        }

        binding!!.operationalHours.tvsetToHours.setOnClickListener {

            if(toHours> fromHours)
            {
                binding!!.operationalHours.llFromHours.visibility = View.GONE
                binding!!.operationalHours.llToHours.visibility = View.GONE

                binding!!.operationalHours.tvToHoursText.setText("" + toHours)
                binding!!.operationalHours.tvToMinsText.setText("" + toMins)

                // binding!!.operationalHours.tvAMPMText.setText("" + toAMPM)
                updateOpeningClosinghours(Constant.CLOSE)
            }else if(toHours == fromHours && toMins > fromMins)
            {
                binding!!.operationalHours.llFromHours.visibility = View.GONE
                binding!!.operationalHours.llToHours.visibility = View.GONE

                binding!!.operationalHours.tvToHoursText.setText("" + toHours)
                binding!!.operationalHours.tvToMinsText.setText("" + toMins)

                // binding!!.operationalHours.tvAMPMText.setText("" + toAMPM)
                updateOpeningClosinghours(Constant.CLOSE)
            }else
            {
                Toast.makeText(this, "closing time should be greater then opening time",Toast.LENGTH_SHORT).show()
            }

        }

        binding!!.operationalHours!!.addImage.setOnClickListener {
            showAddPictureDialog()
        }
        setAddressData()
    }

    fun submitData() {
        updateOperationalHours()
        Handler().postDelayed(Runnable {
            UnlValidatorActivity.addressImageList.clear()
            createAddressModel = null
            createAddressModel
            UnlValidatorActivity.pinLat = 0.0
            UnlValidatorActivity.pinLong = 0.0
            startActivity(Intent(this@DeliveryHoursActivity, UnlValidatorActivity::class.java))
            finishAffinity()
        }, 500)
    }

    fun setAddressData() {
        binding!!.operationalHours.tvAddressText.text = createAddressModel!!.address

        if (UnlValidatorActivity.addressImageList != null && UnlValidatorActivity.addressImageList.size > 0) {

            try {
                var str: String = UnlValidatorActivity.addressImageList.get(0).ivPhotos.toString()
                if (str != null && !str.equals(""))
                {
                    Glide.with(this)
                        .load(UnlValidatorActivity.addressImageList.get(0).ivPhotos)
                        .placeholder(R.drawable.photos) // Set a placeholder image if needed
                        .error(R.drawable.photos) // Set an error image if loading fails
                        .into(binding!!.operationalHours!!.addImage)
                }

                var count = 0
                UnlValidatorActivity.addressImageList.forEach() {
                    var str: String = it.ivPhotos.toString()

                    if (str != null && !str.equals(""))
                    {
                        count++
                    }

                }

                binding!!.operationalHours!!.imageCount.text = "" + count + " of 9"

            }
            catch (e:java.lang.Exception)
            {
                e.printStackTrace()
            }
        }
    }

    fun updateOpeningClosinghours(openCloseType: String) {
        if (openCloseType.equals(Constant.OPEN)) {
            if (daysType.equals(Constant.ALL_DAYS)) {
                for (i in 0..openCloseTimeList.size - 1) {
                    openCloseTimeList.set(
                        i,
                        OpeningHoursSpecificationModel(
                            fromHours + ":" + fromMins ,
                            weekDays.get(i),
                            true,
                            openCloseTimeList.get(i).closes
                        )
                    )
                }
            } else if (daysType.equals(Constant.MONDAY)) {
                openCloseTimeList.set(
                    Constant.MON,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins ,
                        weekDays.get(Constant.MON),
                        true,
                        openCloseTimeList.get(Constant.MON).closes
                    )
                )
            } else if (daysType.equals(Constant.TUESDAY)) {
                openCloseTimeList.set(
                    Constant.TUES,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins ,
                        weekDays.get(Constant.TUES),
                        true,
                        openCloseTimeList.get(Constant.TUES).closes
                    )
                )
            } else if (daysType.equals(Constant.WEDNESDAY)) {
                openCloseTimeList.set(
                    Constant.WED,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins ,
                        weekDays.get(Constant.WED),
                        true,
                        openCloseTimeList.get(Constant.WED).closes
                    )
                )
            } else if (daysType.equals(Constant.THURSDAY)) {
                openCloseTimeList.set(
                    Constant.THU,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins ,
                        weekDays.get(Constant.THU),
                        true,
                        openCloseTimeList.get(Constant.THU).closes
                    )
                )
            } else if (daysType.equals(Constant.FRIDAY)) {
                openCloseTimeList.set(
                    Constant.FRI,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins ,
                        weekDays.get(Constant.FRI),
                        true,
                        openCloseTimeList.get(Constant.FRI).closes
                    )
                )
            } else if (daysType.equals(Constant.SATURDAY)) {
                openCloseTimeList.set(
                    Constant.SAT,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins ,
                        weekDays.get(Constant.SAT),
                        true,
                        openCloseTimeList.get(Constant.SAT).closes
                    )
                )
            } else if (daysType.equals(Constant.SUNDAY)) {
                openCloseTimeList.set(
                    Constant.SUN,
                    OpeningHoursSpecificationModel(
                        fromHours + ":" + fromMins ,
                        weekDays.get(Constant.SUN),
                        true,
                        openCloseTimeList.get(Constant.SUN).closes
                    )
                )
            }
        } else {
            if (daysType.equals(Constant.ALL_DAYS)) {
                for (i in 0..openCloseTimeList.size - 1) {
                    openCloseTimeList.set(
                        i,
                        OpeningHoursSpecificationModel(
                            openCloseTimeList.get(i).opens,
                            weekDays.get(i),
                            true,
                            toHours + ":" + toMins
                        )
                    )
                }
            } else if (daysType.equals(Constant.MONDAY)) {
                openCloseTimeList.set(
                    Constant.MON,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(Constant.MON).opens,
                        weekDays.get(Constant.MON),
                        true,
                        toHours + ":" + toMins
                    )
                )
            } else if (daysType.equals(Constant.TUESDAY)) {
                openCloseTimeList.set(
                    Constant.TUES,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(Constant.TUES).opens,
                        weekDays.get(Constant.TUES),
                        true,
                        toHours + ":" + toMins
                    )
                )
            } else if (daysType.equals(Constant.WEDNESDAY)) {
                openCloseTimeList.set(
                    Constant.WED,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(Constant.WED).opens,
                        weekDays.get(Constant.WED),
                        true,
                        toHours + ":" + toMins
                    )
                )
            } else if (daysType.equals(Constant.THURSDAY)) {
                openCloseTimeList.set(
                    Constant.THU,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(Constant.THU).opens,
                        weekDays.get(Constant.THU),
                        true,
                        toHours + ":" + toMins
                    )
                )
            } else if (daysType.equals(Constant.FRIDAY)) {
                openCloseTimeList.set(
                    Constant.FRI,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(Constant.FRI).opens,
                        weekDays.get(Constant.FRI),
                        true,
                        toHours + ":" + toMins
                    )
                )
            } else if (daysType.equals(Constant.SATURDAY)) {
                openCloseTimeList.set(
                    Constant.SAT,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(Constant.SAT).opens,
                        weekDays.get(Constant.SAT),
                        true,
                        toHours + ":" + toMins
                    )
                )
            } else if (daysType.equals(Constant.SUNDAY)) {
                openCloseTimeList.set(
                    Constant.SUN,
                    OpeningHoursSpecificationModel(
                        openCloseTimeList.get(Constant.SUN).opens,
                        weekDays.get(Constant.SUN),
                        true,
                        toHours + ":" + toMins
                    )
                )
            }
        }

    }

    fun setFromHoursAdapters() {
        var daysList = ArrayList<String>()
        for (i in 0..23) {
            if (i < 10)
                daysList.add("0" + i)
            else
                daysList.add("" + i)
        }

        var oprationalHoursAdapter: OperationalHoursAdapter = OperationalHoursAdapter(
            daysList, this,
            Constant.FROM_HOURS
        )
        binding!!.operationalHours.rvFromHour.adapter = oprationalHoursAdapter
        binding!!.operationalHours.rvFromHour.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.operationalHours.rvFromHour.setBackgroundResource(R.color.white)
        oprationalHoursAdapter.selectedIndex = returnHoursIndex(true).toInt()
        binding!!.operationalHours.rvFromHour.adapter!!.notifyDataSetChanged()
        setFromMinsAdapters()
    }

    fun setToHoursAdapters() {
        var daysList = ArrayList<String>()
        for (i in 0..23) {
            if (i < 10)
                daysList.add("0" + i)
            else
                daysList.add("" + i)
        }
        var operationalHoursAdapter: OperationalHoursAdapter =
            OperationalHoursAdapter(daysList, this, Constant.TO_HOURS)
        binding!!.operationalHours.rvToHour.adapter = operationalHoursAdapter
        binding!!.operationalHours.rvToHour.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.operationalHours.rvToHour.setBackgroundResource(R.color.white)
        operationalHoursAdapter.selectedIndex = returnHoursIndex(false).toInt()
        binding!!.operationalHours.rvToHour.adapter!!.notifyDataSetChanged()
        setToMinsAdapters()
    }


    fun setFromMinsAdapters() {
        var daysList = ArrayList<String>()
        for (i in 0..59) {
            if (i < 10)
                daysList.add("0" + i)
            else
                daysList.add("" + i)
        }
        var operationalHoursAdapter: OperationalHoursAdapter =
            OperationalHoursAdapter(daysList, this, Constant.FROM_MINS)
        binding!!.operationalHours.rvFromMin.adapter = operationalHoursAdapter

        binding!!.operationalHours.rvFromMin.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.operationalHours.rvFromMin.setBackgroundResource(R.color.white)
        operationalHoursAdapter.selectedIndex = returnMinsIndex(true).toInt()
        binding!!.operationalHours.rvFromMin.adapter!!.notifyDataSetChanged()
    }


    fun setToMinsAdapters() {
        var daysList = ArrayList<String>()

        for (i in 0..59) {
            if (i < 10)
                daysList.add("0" + i)
            else
                daysList.add("" + i)
        }
        var operationalHoursAdapter: OperationalHoursAdapter =
            OperationalHoursAdapter(daysList, this, Constant.TO_MINS)
        binding!!.operationalHours.rvToMin.adapter = operationalHoursAdapter
        binding!!.operationalHours.rvToMin.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding!!.operationalHours.rvToMin.setBackgroundResource(R.color.white)
        operationalHoursAdapter.selectedIndex = returnMinsIndex(false).toInt()
        binding!!.operationalHours.rvToMin.adapter!!.notifyDataSetChanged()
    }

    fun selectedAccessibility() {
        updateButtonStatus()
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

        if (isWheelchairSelected) {
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
            // img = getResources().getDrawable(R.drawable.home)

        }
        if (isElevatorSelected) {
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


    fun returnHoursIndex(opening: Boolean): String {
        var position = "-1"
        try {
            if (opening) {
                if (daysType.equals(Constant.ALL_DAYS)) {
                    var splitArray = openCloseTimeList.get(Constant.ALLDAY).opens.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.MONDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.MON).opens.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.TUESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.TUES).opens.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.WEDNESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.WED).opens.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.THURSDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.THU).opens.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.FRIDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.FRI).opens.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.SATURDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SAT).opens.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.SUNDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SUN).opens.split(":")
                    position = splitArray[0]
                }
            } else {
                if (daysType.equals(Constant.ALL_DAYS)) {
                    var splitArray = openCloseTimeList.get(Constant.ALLDAY).closes.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.MONDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.MON).closes.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.TUESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.TUES).closes.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.WEDNESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.WED).closes.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.THURSDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.THU).closes.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.FRIDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.FRI).closes.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.SATURDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SAT).closes.split(":")
                    position = splitArray[0]
                } else if (daysType.equals(Constant.SUNDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SUN).closes.split(":")
                    position = splitArray[0]
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return position
    }


    fun returnMinsIndex(opening: Boolean): String {
        var position = "-1"
        try {
            if (opening) {
                if (daysType.equals(Constant.ALL_DAYS)) {
                    var splitArray = openCloseTimeList.get(Constant.ALLDAY).opens.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.MONDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.MON).opens.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.TUESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.TUES).opens.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.WEDNESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.WED).opens.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.THURSDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.THU).opens.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.FRIDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.FRI).opens.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.SATURDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SAT).opens.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.SUNDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SUN).opens.split(":")
                    position = splitArray[1]
                }
            } else {
                if (daysType.equals(Constant.ALL_DAYS)) {
                    var splitArray = openCloseTimeList.get(Constant.ALLDAY).closes.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.MONDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.MON).closes.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.TUESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.TUES).closes.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.WEDNESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.WED).closes.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.THURSDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.THU).closes.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.FRIDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.FRI).closes.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.SATURDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SAT).closes.split(":")
                    position = splitArray[1]
                } else if (daysType.equals(Constant.SUNDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SUN).closes.split(":")
                    position = splitArray[1]
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return position
    }


    fun returnAmPm(opening: Boolean): String {
        var position = "AM"
        try {
            if (opening) {
                if (daysType.equals(Constant.ALL_DAYS)) {
                    var splitArray = openCloseTimeList.get(Constant.ALLDAY).opens.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.MONDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.MON).opens.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.TUESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.TUES).opens.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.WEDNESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.WED).opens.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.THURSDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.THU).opens.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.FRIDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.FRI).opens.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.SATURDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SAT).opens.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.SUNDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SUN).opens.split(":")
                    position = splitArray[2]
                }
            } else {
                if (daysType.equals(Constant.ALL_DAYS)) {
                    var splitArray = openCloseTimeList.get(Constant.ALLDAY).closes.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.MONDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.MON).closes.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.TUESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.TUES).closes.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.WEDNESDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.WED).closes.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.THURSDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.THU).closes.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.FRIDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.FRI).closes.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.SATURDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SAT).closes.split(":")
                    position = splitArray[2]
                } else if (daysType.equals(Constant.SUNDAY)) {
                    var splitArray = openCloseTimeList.get(Constant.SUN).closes.split(":")
                    position = splitArray[2]
                }
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }

        return position
    }


    fun setFromAndToTime() {

        binding!!.operationalHours.tvFromHoursText.setText("" + returnHoursIndex(true))
        binding!!.operationalHours.tvFromMinText.setText("" + returnMinsIndex(true))



        binding!!.operationalHours.tvToHoursText.setText("" + returnHoursIndex(false))
        binding!!.operationalHours.tvToMinsText.setText("" + returnMinsIndex(false))

 /*       fromAMPM = returnAmPm(true)
        binding!!.operationalHours.tvFromAMPMText.setText("" + fromAMPM)
        toAMPM = returnAmPm(false)
        binding!!.operationalHours.tvAMPMText.setText("" + toAMPM)*/

    }


    fun updateOperationalHours() {

        openCloseTimeList.removeAt(0)

        for (i in 0..6) {
            if (openCloseTimeList.get(i).opens.equals(openCloseTimeList.get(i).closes)) {
                openCloseTimeList.get(i).isHoliday = true
            }
        }
        createAddressModel!!.openingHoursSpecificationModel = openCloseTimeList

        var accessbilityArray = ArrayList<String>()
        if(isElevatorSelected)
            accessbilityArray.add(Constant.ELEVATOR_ACCESSIBILITY)
        if(isWheelchairSelected)
            accessbilityArray.add(Constant.WHEELCHAIR_ACCESSIBILITY)

        createAddressModel!!.accessibility = accessbilityArray
        viewModel.insertAddress(database, createAddressModel!!)
    }

    var lastIndex = -1
    override fun dayClick(day: String) {
        if(lastIndex!= operationalDayAdapter.selectedIndex)
        {
            lastIndex = operationalDayAdapter.selectedIndex
            daysType = day
            setFromAndToTime()
            updateButtonStatus()
        }else
        {
            daysType = ""
            lastIndex = -1
            operationalDayAdapter.selectedIndex = -2
            operationalDayAdapter.notifyDataSetChanged()
            updateButtonStatus()
        }

    }

    fun updateButtonStatus()
    {
        if(operationalDayAdapter.selectedIndex>=0 || isWheelchairSelected || isElevatorSelected)
        {
            binding!!.operationalHours.tvConfirm.setBackgroundResource(R.drawable.theme_round_btn)
            binding!!.operationalHours.tvConfirm.isClickable = true
            binding!!.operationalHours.tvConfirm.isEnabled = true
        }else
        {
            binding!!.operationalHours.tvConfirm.setBackgroundResource(R.drawable.bg_button_disable)
            binding!!.operationalHours.tvConfirm.isClickable = false
            binding!!.operationalHours.tvConfirm.isEnabled = false
        }


    }

    override fun itemClick(hour: String, type: String) {
        if (type.equals(Constant.FROM_HOURS)) {
            fromHours = hour
        } else if (type.equals(Constant.FROM_MINS)) {
            fromMins = hour
        } else if (type.equals(Constant.TO_HOURS)) {
            toHours = hour
        } else if (type.equals(Constant.TO_MINS)) {
            toMins = hour
        }
    }



    override fun addressImageClick(index : Int, isReplaceImage : Boolean) {
        isReplace = isReplaceImage
        if(isReplace) {
            replaceIndex = index
            openImagePicker(1)
        }
        else {
            replaceIndex = UnlValidatorActivity.addressImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
            openImagePicker(dataListSize -replaceIndex)
        }
    }

    fun openImagePicker(imageLimit : Int) {
        MultiImagePicker.with(this)
            .setSelectionLimit(imageLimit)
            .open()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MultiImagePicker.REQUEST_PICK_MULTI_IMAGES && resultCode == AppCompatActivity.RESULT_OK) {
            val result = MultiImagePicker.Result(data)
            if (result.isSuccess()) {
                val imageList = result.getImageList()
                val uriList = ArrayList<AddPicturesModel>()
                uriList.clear()
                for (uri in imageList) {
                    uriList.add(AddPicturesModel(uri))
                }
                val uriListSize = uriList.size
                try {
                    if (isReplace) {
                        UnlValidatorActivity.addressImageList[replaceIndex] = AddPicturesModel(uriList[0].ivPhotos)
                    } else {
                        for (i in replaceIndex until dataListSize) {
                            if (i - replaceIndex < uriListSize) {
                                UnlValidatorActivity.addressImageList[i] =
                                    AddPicturesModel(uriList[i - replaceIndex].ivPhotos)
                            } else {
                                UnlValidatorActivity.addressImageList[i] = AddPicturesModel(Uri.EMPTY)
                            }
                        }
                    }
                    if(uriListSize >0)
                     updateAddPictureSavebtn(true)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                adapter.notifyDataSetChanged()
            }
        }
    }

}