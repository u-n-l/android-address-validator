package com.unl.addressvalidator.ui.entrances

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasewithmodelclassess.model.EntranceModel
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.databinding.ActivityEntrancesBinding
import com.unl.addressvalidator.databinding.ActivityLandmarkBinding
import com.unl.addressvalidator.ui.adapters.EntrancesAdapter
import com.unl.addressvalidator.ui.deliveryhours.DeliveryHoursActivity
import com.unl.addressvalidator.ui.fragments.HomeFragment
import com.unl.addressvalidator.ui.fragments.addEntrancePoint
import com.unl.addressvalidator.ui.fragments.setOperationalHoursClick
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.createAddressModel
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.builder.MultiImagePicker
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.ui.interfaces.AddressImageClickListner
import com.unl.addressvalidator.ui.interfaces.EntranceClickListner
import com.unl.addressvalidator.util.Utility
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.EnvironmentType
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import com.unl.map.sdk.prefs.DataManager

class EntrancesActivity : AppCompatActivity(), EntranceClickListner, AddressImageClickListner {

    var binding: ActivityEntrancesBinding? = null
    var mapBoxMap: MapboxMap? = null
    val entranceImageList = ArrayList<AddPicturesModel>()
    var moveCounter: Int = 0
    var replaceIndex: Int = 0
    lateinit var adapter: AddPicturesAdapter
    var entranceList: ArrayList<EntranceModel> = ArrayList()
    var entranceMarker: ArrayList<Marker> = ArrayList()
    val dataListSize = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UnlMap(this, DataManager.getApiKey()!!, DataManager.getVpmId()!!, EnvironmentType.SANDBOX)
        binding = ActivityEntrancesBinding.inflate(getLayoutInflater())
        setContentView(binding!!.root)
        binding!!.mapView.gridCellClickable = false
        binding!!.mapView.getMapAsync {
            mapBoxMap = it
            binding!!.mapView.fm = supportFragmentManager
            binding!!.mapView.activity = this
            binding!!.mapView.lifeCycleOwner = this
            binding!!.mapView.enableIndoorMap = false
            binding!!.mapView.viewLifecycle = this
            Utility.configureMap(binding!!.mapView, this)
            setMapData()
        }
        binding!!.mapView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {

                val action = event.actionMasked
                if (action == MotionEvent.ACTION_MOVE) {
                    moveCounter++

                    Log.v("TOUCHPINT", "MOVE" + MotionEvent.ACTION_MOVE)
                } else if (action == MotionEvent.ACTION_UP) {

                    val currentZoomLevel =  binding!!.mapView.mapbox!!.getCameraPosition().zoom
                    Log.v("TOUCHPINT", "Counter" + moveCounter)
                    if ( moveCounter < 4 && currentZoomLevel>15.00)
                    {
                            val new_position: LatLng = binding!!.mapView.mapbox!!.getProjection().fromScreenLocation(
                                PointF(event.x, event.y - 50 )
                            )
                            showMarker(new_position, "Entrance No 1")
                            addEntrancePoint(new_position.latitude,new_position.longitude)
                             updateButtonStatus()
                    }
                    moveCounter = 0
                }
                return false
            }
        })

        binding!!.backBtn.setOnClickListener {
            finish()
        }
        binding!!.addEntrances!!.root.visibility = View.VISIBLE
        binding!!.pinHint!!.text = "Move the pin to the building entrance"
        val layoutManager = LinearLayoutManager(this)
        binding!!.addEntrances!!.rvEntrances.layoutManager = layoutManager
        binding!!.addEntrances!!.rvEntrances.setBackgroundResource(R.color.white)
        binding!!.addEntrances!!.tvConfirm.setOnClickListener {

            updateEntrance()
            startActivity(Intent(this, DeliveryHoursActivity::class.java))
        }

        binding!!.addEntrances!!.tvSkip.setOnClickListener {
            startActivity(Intent(this, DeliveryHoursActivity::class.java))

        }

        binding!!.hidePinHint.setOnClickListener {
            binding!!.cvPintHint.visibility = View.GONE
        }

    }


    fun updateButtonStatus()
    {
        if(entranceList.size > 0)
        {
            binding!!.addEntrances.tvConfirm.setBackgroundResource(R.drawable.theme_round_btn)
            binding!!.addEntrances.tvConfirm.isClickable = true
            binding!!.addEntrances.tvConfirm.isEnabled = true
        }else
        {
            binding!!.addEntrances.tvConfirm.setBackgroundResource(R.drawable.bg_button_disable)
            binding!!.addEntrances.tvConfirm.isClickable = false
            binding!!.addEntrances.tvConfirm.isEnabled = false
        }


    }

 fun addEntrancePoint(lattitude : Double, longitude : Double)
    {
        val entranceModel = EntranceModel("Entrance", ""+(entranceList.size+1).toString(),"","","0","",lattitude,longitude,"",ArrayList<String>())
        entranceList.add(entranceModel)
        binding!!.addEntrances.rvEntrances.visibility = View.VISIBLE
        binding!!.addEntrances.rvEntrances.adapter = EntrancesAdapter(entranceList, this)
        binding!!.addEntrances.rvEntrances.adapter!!.notifyDataSetChanged()
    }


    fun setMapData()
    {
        var latlng = LatLng(UnlValidatorActivity.pinLat, UnlValidatorActivity.pinLong)
        Utility.changeCameraPosition(latlng, mapBoxMap!!)

    }

    //Create marker for Address
    fun showMarker(latLng: LatLng, address: String) {
        try {

            val iconFactory = IconFactory.getInstance(this)
            val btmap: Bitmap = (ResourcesCompat.getDrawable(
               resources,
                R.drawable.entrance_marker,
                null
            ) as BitmapDrawable).getBitmap()
            val icon: com.mapbox.mapboxsdk.annotations.Icon = iconFactory.fromBitmap(btmap)

            var marker  = MarkerOptions()
                .position(latLng)
                .icon(icon)

            entranceMarker.add(mapBoxMap?.addMarker(marker)!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun entranceEditClick(position: Int, entranceModel: ArrayList<EntranceModel>) {
        editeEntrance(position,entranceModel)
    }

    override fun entranceDeleteClick(position: Int,entranceModel: ArrayList<EntranceModel>) {
        removeEntrance(position,entranceModel)
    }

    override fun entranceImageClick(position: Int, entranceModel: ArrayList<EntranceModel>) {

        showEntrancePictureDialog(position,entranceModel)
    }

    fun openImagePicker() {
        MultiImagePicker.with(this)
            .setSelectionLimit(dataListSize)
            .open()
    }


    fun editeEntrance(position : Int, resulttList: ArrayList<EntranceModel>)
    {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.edit_entrance_popup)
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var  edtEntranceName = dialog.findViewById<EditText>(R.id.edtEntranceName)
        var  edtEntranceCategory = dialog.findViewById<EditText>(R.id.edtEntranceCategory)
        var  edtEntranceInfo = dialog.findViewById<EditText>(R.id.edtEntranceInfo)
        //  dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        dialog.window!!.setGravity(Gravity.BOTTOM)
        dialog.findViewById<TextView>(R.id.tvSave).setOnClickListener {
            dialog.dismiss()
            resulttList.get(position).entranceCategory = edtEntranceCategory.text.toString()
            resulttList.get(position).entranceName = edtEntranceName.text.toString()
            resulttList.get(position).entranceInfo = edtEntranceInfo.text.toString()
            entranceList = resulttList
            binding!!.addEntrances.rvEntrances.adapter = EntrancesAdapter(entranceList, this)
            binding!!.addEntrances.rvEntrances.adapter!!.notifyDataSetChanged()
        }

        dialog.findViewById<TextView>(R.id.tvCancel).setOnClickListener {
            dialog.dismiss()
        }

    }


    fun removeEntrance(position : Int,resulttList: ArrayList<EntranceModel>)
    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_entrance_popup)
        dialog.show()
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setGravity(Gravity.CENTER)

        dialog.findViewById<TextView>(R.id.tvDelete).setOnClickListener {
            dialog.dismiss()
            resulttList.removeAt(position)
            mapBoxMap?.removeMarker( entranceMarker.get(position))
            entranceMarker.removeAt(position)
            entranceList = resulttList
            binding!!.addEntrances.rvEntrances.adapter = EntrancesAdapter(entranceList, this)
            binding!!.addEntrances.rvEntrances.adapter!!.notifyDataSetChanged()
            updateButtonStatus()
        }
        dialog.findViewById<TextView>(R.id.tvSkip).setOnClickListener {
            dialog.dismiss()
        }
    }
    override fun addressImageClick() {
        replaceIndex = entranceImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
        openImagePicker()
    }

    fun updateEntrance()
    {
        createAddressModel!!.entranceModel = entranceList
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
                for (i in replaceIndex until dataListSize) {
                    if (i - replaceIndex < uriListSize) {
                        entranceImageList[i] = AddPicturesModel(uriList[i - replaceIndex].ivPhotos)
                    } else {
                        entranceImageList[i] = AddPicturesModel(Uri.EMPTY)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

}
