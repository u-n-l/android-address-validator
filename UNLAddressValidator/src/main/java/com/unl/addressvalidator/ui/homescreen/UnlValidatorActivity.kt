package com.unl.addressvalidator.ui.homescreen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasewithmodelclassess.model.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.database.UnlAddressDatabase
import com.unl.addressvalidator.databinding.ActivityHomeBinding
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.adapters.AddressListAdapter
import com.unl.addressvalidator.ui.adapters.SearchResultAdapter
import com.unl.addressvalidator.ui.addressdetail.AddressListActivity
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.builder.MultiImagePicker
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.ui.interfaces.AddressImageClickListner
import com.unl.addressvalidator.ui.interfaces.AddressItemClickListner
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner
import com.unl.addressvalidator.ui.landmark.LandmarkActivity
import com.unl.addressvalidator.util.Constant
import com.unl.addressvalidator.util.Utility
import com.unl.addressvalidator.util.Utility.changeCameraPosition
import com.unl.addressvalidator.util.Utility.configureMap
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.CellPrecision
import com.unl.map.sdk.data.EnvironmentType
import com.unl.map.sdk.helpers.grid_controls.loadGrids
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import java.util.*
import kotlin.collections.ArrayList

class UnlValidatorActivity : AppCompatActivity(), AddressImageClickListner, LocationListener,
    AddressItemClickListner,
    SearchItemClickListner {

    private var apiKey: String? = null
    private var vpmId: String? = null

    var binding: ActivityHomeBinding? = null

    var mapBoxMap: MapboxMap? = null

    lateinit var viewModel: ValidatorViewModel
    var reverseGeoCodeResponse: ReverseGeoCodeResponse? = null
    var reverseGeoCodeCurrentLocationResponse: ReverseGeoCodeResponse? = null
    var addressType = "home"

    lateinit var adapter: AddPicturesAdapter
    val imageList = ArrayList<String>()

    val dataListSize = 9
    var replaceIndex: Int = 0

    var cityText = ""
    var stateText = ""
    var pincodeText = ""


    var currLat: Double = 0.0
    var currLong: Double = 0.0

    var moveCounter: Int = 0
    var isChangeMarker = true
    var updateLocation = true

    private val permissionsRequestCode = 123

    //Get live location from GPS
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    lateinit var locationManager: LocationManager

    var entranceList: ArrayList<EntranceModel> = ArrayList()
    var currentAddressText = ""
    lateinit var database: UnlAddressDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMetadataFromMenifest()
        UnlMap(this, apiKey!!, vpmId!!, EnvironmentType.SANDBOX)
        binding = ActivityHomeBinding.inflate(getLayoutInflater())
        setContentView(binding!!.root)
        binding!!.mapView.gridCellClickable = false
        binding!!.mapView.getMapAsync {
            mapBoxMap = it
            binding!!.mapView.fm = supportFragmentManager
            binding!!.mapView.activity = this
            binding!!.mapView.lifeCycleOwner = this
            binding!!.mapView.enableIndoorMap = false
            binding!!.mapView.viewLifecycle = this
            configureMap(binding!!.mapView,this)
           /* binding!!.mapView.enableTileSelector(true)
            binding!!.mapView.setGridControls(this, true)
            binding!!.mapView.setTileSelectorGravity(Gravity.END)
            binding!!.mapView.ivTile.setImageResource(R.drawable.ic_tile)
            binding!!.mapView.ivTile.setBackgroundColor(Color.parseColor("#00000000"))
            binding!!.mapView.mapbox!!.uiSettings.setCompassFadeFacingNorth(true)
            binding!!.mapView.mapbox!!.uiSettings.setCompassImage(resources.getDrawable(R.drawable.transparent_bg))
            binding!!.mapView.isVisibleGrids = true
            binding!!.mapView.cellPrecision = CellPrecision.GEOHASH_LENGTH_9
            binding!!.mapView.mapbox?.loadGrids(true, binding!!.mapView, CellPrecision.GEOHASH_LENGTH_9)
            binding!!.mapView.ivGrid.visibility = View.GONE*/

/*          Handler().postDelayed(Runnable {
              binding!!.mapView.cellPrecision = CellPrecision.GEOHASH_LENGTH_9
              binding!!.mapView.mapbox?.loadGrids(true, binding!!.mapView, CellPrecision.GEOHASH_LENGTH_9)
                                         },1000)*/


        }
        for (i in 0 until 9) {
            addressImageList.add(AddPicturesModel(Uri.EMPTY))
        }
        database = UnlAddressDatabase.getInstance(this)

        binding!!.mapView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                updateLocation = false




                val action = event.actionMasked
                if (action == MotionEvent.ACTION_MOVE) {
                    moveCounter++

                    Log.v("TOUCHPINT", "MOVE" + MotionEvent.ACTION_MOVE)
                } else if (action == MotionEvent.ACTION_UP) {
                    Log.v("TOUCHPINT", "Counter" + moveCounter)
                    if (isChangeMarker && moveCounter < 4) {

                        val currentZoomLevel =  binding!!.mapView.mapbox!!.getCameraPosition().zoom
                        if(currentZoomLevel>18.00)
                        {
                            val new_position: LatLng =
                                binding!!.mapView.mapbox!!.getProjection().fromScreenLocation(
                                    PointF(event.x, event.y - 50)
                                )
                            binding!!.cvPintHint.visibility = View.GONE
                            clearMap()
                            pinLat = new_position.latitude
                            pinLong = new_position.longitude
                            showMarker(new_position, "home")
                            callReverseGeoCode(new_position)
                        }

                    }
                    moveCounter = 0
                }
                return false
            }
        })

        binding!!.hidePinHint.setOnClickListener {
            binding!!.cvPintHint.visibility = View.GONE
        }

        binding!!.confirmAddress.ivClose.setOnClickListener {
            binding!!.confirmAddress.root.visibility = View.GONE
        }

        binding!!.ivShowAddreess.setOnClickListener {
           // getAddressCreated()
            //viewModel.getAddress(database)

            startActivity(Intent(this, AddressListActivity::class.java))
            // Handler().postDelayed(Runnable {  viewModel.getAddress(database)},1000)
        }

        binding!!.addNewAdd.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s!!.toString().length > 1 /*&& fromSelection == false*/) {
                        // binding!!.progressBar.visibility = View.VISIBLE
                        var searchNearbyMe: JsonObject? = JsonObject()
                        var jsonArray = JsonArray()
                        jsonArray.add(pinLong)
                        jsonArray.add(pinLat)
                        searchNearbyMe!!.add("Point", jsonArray)

                        viewModel.getAutocompleteData(s.toString(), searchNearbyMe!!)
                    } else {
                        //  fromSelection = false
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?, start: Int,
                before: Int, count: Int
            ) {

            }
        })

        requestPermissions()
        initiateViewModel()
        setSearchListView()
        getReversgeoCodeResponse()
        getAddressFromCurrLocatinResponse()
        getAutocompleteResponse()
        getSearchAddressResponse()
        selectedLabel(0)
        setNewAddressClick()

    }


    fun getAddressCreated() {
        viewModel.addresslist.observe(this, { users ->
            try {
                if (users != null && users.size > 0) {
                    Collections.reverse(users)
                    initAddressList(users)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun setSearchListView() {
        val layoutManager = LinearLayoutManager(this)
        binding!!.addNewAdd!!.rvSearchResult.layoutManager = layoutManager
        val separator = DividerItemDecoration(this, layoutManager.orientation)
        binding!!.addNewAdd!!.rvSearchResult.addItemDecoration(separator)
        binding!!.addNewAdd!!.rvSearchResult.setBackgroundResource(R.color.white)
        val lateralPadding = resources.getDimension(R.dimen.big_padding).toInt()
        binding!!.addNewAdd!!.rvSearchResult.setPadding(lateralPadding, 0, lateralPadding, 0)
    }


    private fun getAutocompleteResponse() {
        viewModel.autoCompleteData.observe(this, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    response.data
                    if (response.data == null || response.data.size == 0) {
                        viewModel.getAutocompleteData(
                            binding!!.addNewAdd!!.edtSearch.text.toString()
                        )
                    } else {
                        binding!!.addNewAdd.rvSearchResult.adapter =
                            SearchResultAdapter(response.data!!, this)
                        binding!!.addNewAdd.rvSearchResult.visibility = View.VISIBLE
                    }

                    //   Toast.makeText(context,""+response.data!!.size,Toast.LENGTH_SHORT).show()
                }

                is ApiCallBack.Error -> {

                }

                is ApiCallBack.Loading -> {

                }

            }
        })

    }


    //Clear Map before adding new Marker
    fun clearMap() {
        /* markerViewList.forEach {
             binding!!.mapView.removeView(it)
         }
         markerViewList.clear()*/
        mapBoxMap?.clear()
    }


    private fun callReverseGeoCode(latlng: LatLng) {
        var jsonObject = JsonObject()
        var jsonArray = JsonArray()
        jsonArray.add(latlng.latitude)
        jsonArray.add(latlng.longitude)
        jsonObject.add("Point", jsonArray)
        viewModel.getAddfromLocation(jsonObject)
    }

    private fun initiateViewModel() {
        viewModel = ViewModelProvider(this)[ValidatorViewModel::class.java]
    }

    private fun getReversgeoCodeResponse() {
        viewModel?.reverseGeocodeData?.observe(this, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    response.data
                    reverseGeoCodeResponse = response.data
                    showReverseGeoAddress()
                }

                is ApiCallBack.Error -> {
                    Toast.makeText(this, "" + response.message, Toast.LENGTH_SHORT).show()
                }

                is ApiCallBack.Loading -> {

                }
            }
        })
    }

    fun showReverseGeoAddress() {
        try {
            binding!!.confirmAddress!!.root.visibility = View.VISIBLE
            binding!!.addNewAdd!!.root.visibility = View.GONE

            var houseNo =
                reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).house_number
            var streetName =
                reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).street_address
            var city =
                reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).city_district
            var pincode =
                reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).postal_code
            var state =
                reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).state_district
            var country =
                reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).country_code

            var address =
                Utility.returnFullAddress(houseNo, "", "", streetName, city, state, pincode)
            binding!!.addNewAdd.tvAddressText.text = address
          //  binding!!.addNewAdd.edtBuildingName.setText(reverseGeoCodeResponse!!.features!!.get(0).properties.place.name)

            binding!!.confirmAddress!!.tvAddressText!!.text = address
            //   reverseGeoCodeResponse!!.features!!.get(0).properties.place.name
            binding!!.confirmAddress!!.tvConfirm.setOnClickListener {
                //binding!!.confirmAddress!!.root.visibility = View.GONE
                //  openLandmarkPopup()
                setAddressObject()
                startActivity(Intent(this, LandmarkActivity::class.java))
            }
            binding!!.confirmAddress!!.mainLayout.setOnClickListener {
            }
            binding!!.confirmAddress!!.addNew.setOnClickListener {
                binding!!.confirmAddress!!.root.visibility = View.GONE
                binding!!.addNewAdd!!.root.visibility = View.VISIBLE
                binding!!.headerTitle.text = Constant.ADD_ADDRESS
                binding!!.backBtn.visibility = View.VISIBLE
            }
            binding!!.confirmAddress!!.editAddress.setOnClickListener {
                binding!!.confirmAddress!!.root.visibility = View.GONE
                binding!!.addNewAdd!!.root.visibility = View.VISIBLE
                binding!!.headerTitle.text = Constant.EDIT_ADDRESS
                binding!!.backBtn.visibility = View.VISIBLE
                setMapPointAddress()
                setAddressObject()
            }
            binding!!.confirmAddress!!.addImage.setOnClickListener {
                showAddPictureDialog()
            }
            binding!!.backBtn.setOnClickListener {
                binding!!.confirmAddress!!.root.visibility = View.VISIBLE
                binding!!.addNewAdd!!.root.visibility = View.GONE
                binding!!.backBtn!!.visibility = View.GONE
                clearAddressFields()
            }

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun setAddressObject() {

        var houseNo =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).house_number
        var streetName =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).street_address
        var city =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).city_district
        var pincode =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).postal_code
        var state =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).state_district
        var country =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).country_code

        var address = Utility.returnFullAddress(houseNo, "", "", streetName, city, state, pincode)
        binding!!.addNewAdd.tvAddressText.text = address

        var floor = ""
        var buildingNum = ""
        var buildingName = ""

        var neighbour = ""

        if (addressType.equals("other")) {
            addressType = binding!!.addNewAdd!!.edtLabelName.text!!.toString()
        }
        val addressModel = AddressModel(
            houseNo,
            floor,
            buildingNum,
            buildingName,
            streetName,
            neighbour,
            cityText,
            stateText,
            country,
            pincodeText
        )

        val locationModel = LocationModel(pinLat!!, pinLong!!)
        val landmarkModel = LandmarkModel(
            "",
            "",
            "00.00",
            "00.00",
            "", ArrayList<String>()
        )

        var entranceList: ArrayList<EntranceModel> = ArrayList()

        var openCloseTimeList: ArrayList<OpeningHoursSpecificationModel> = ArrayList()
        val openingHoursSpecificationModel =
            OpeningHoursSpecificationModel("11:00:00", "Monday", false, "08:00:00")
        if (addressImageList != null && addressImageList.size > 0) {
            imageList.clear()
            addressImageList.forEach() {
                var str: String = it.ivPhotos.toString()

                if (str != null && !str.equals(""))
                    imageList.add(str)
            }
        } else {
            imageList.clear()
        }

        openCloseTimeList.add(openingHoursSpecificationModel)
        createAddressModel = CreateAddressModel(
            addressModel,
            addressType,
            address,
            locationModel,
            landmarkModel,
            entranceList,
            imageList,
            openCloseTimeList
        )
    }

    fun setMapPointAddress() {
        var houseNumber =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).house_number
        var streetAddress =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).street_address
        var city =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).city_district
        var pincode =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).postal_code
        var state =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).state_district
        var countryCode =
            reverseGeoCodeResponse!!.features!!.get(0).properties!!.postal_address.get(0).country_code

        binding!!.addNewAdd!!.edtUnit.setText(houseNumber)
        binding!!.addNewAdd!!.edtStreet.setText(streetAddress)
        binding!!.addNewAdd!!.edtCity.setText(city)
        binding!!.addNewAdd!!.edtPincode.setText(pincode)
        binding!!.addNewAdd!!.edtState.setText(state)
        binding!!.addNewAdd!!.edtCountry.setText(countryCode)
        binding!!.addNewAdd.addressView.visibility = View.VISIBLE
        binding!!.addNewAdd.tvAddressText.text =
            Utility.returnFullAddress(houseNumber, "", "", streetAddress, city, state, pincode)
    }

    fun openImagePicker() {
        MultiImagePicker.with(this)
            .setSelectionLimit(dataListSize)
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
                for (i in replaceIndex until dataListSize) {
                    if (i - replaceIndex < uriListSize) {
                        addressImageList[i] = AddPicturesModel(uriList[i - replaceIndex].ivPhotos)
                    } else {
                        addressImageList[i] = AddPicturesModel(Uri.EMPTY)
                    }
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * For get the VPM_ID and API_KEY from third party App
     */
    private fun getMetadataFromMenifest() {
        val info: ApplicationInfo = getPackageManager().getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = info.metaData
        vpmId = bundle.getString(Constant.META_VPM_ID_KEY)
        apiKey = bundle.getString(Constant.META_API_KEY)
    }

    companion object {
        @JvmStatic
        var createAddressModel: CreateAddressModel? = null
        val addressImageList = ArrayList<AddPicturesModel>()
        var pinLat: Double = 0.0
        var pinLong: Double = 0.0
    }

    override fun addressImageClick() {
        replaceIndex = addressImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
        openImagePicker()
    }

    override fun onLocationChanged(location: Location) {
        updateCurrentLocation(location)
    }

    override fun onResume() {
        super.onResume()
        iniitLocatinoManager()
    }

    private fun iniitLocatinoManager() {
        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            val gpsLocationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationByGps = location
                    updateCurrentLocation(location!!)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            val networkLocationListener: LocationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    locationByNetwork = location
                    updateCurrentLocation(location!!)
                }

                override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }


            if (hasGps) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0F,
                    gpsLocationListener
                )
            }

            if (hasNetwork) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000,
                    0F,
                    networkLocationListener
                )
            }

            val lastKnownLocationByGps =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lastKnownLocationByGps?.let {
                locationByGps = lastKnownLocationByGps
            }

            val lastKnownLocationByNetwork =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork?.let {
                locationByNetwork = lastKnownLocationByNetwork
            }

            if (locationByGps != null && locationByNetwork != null) {
                if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {
                    updateCurrentLocation(locationByGps!!)
                } else {
                    updateCurrentLocation(locationByNetwork!!)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun updateCurrentLocation(location: Location) {
        if (updateLocation && mapBoxMap != null) {
            currLat = location.latitude
            currLong = location.longitude
            clearMap()
            showMarker(LatLng(location.latitude, location.longitude), "Current Location")
            changeCameraPosition(LatLng(location.latitude, location.longitude), mapBoxMap!!)
            callAddressFromCurrLocation(LatLng(location.latitude, location.longitude))
        }
    }


    private fun callAddressFromCurrLocation(latlng: LatLng) {
        var jsonObject = JsonObject()
        var jsonArray = JsonArray()
        jsonArray.add(latlng.latitude)
        jsonArray.add(latlng.longitude)
        jsonObject.add("Point", jsonArray)
        viewModel.getAddfromCurrentLocation(jsonObject)
    }

/*
    fun changeCameraPosition(latlng: LatLng) {
        val latLngBounds = LatLngBounds.Builder()
            .include(latlng)
            .include(latlng)
            .build()

        binding!!.mapView!!.mapbox!!.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                latLngBounds,
                50
            )
        )
        binding!!.mapView!!.mapbox!!.setMaxZoomPreference(21.00)
    }*/

    //Create marker for Address
    fun showMarker(latLng: LatLng, address: String) {
        try {
            pinLat = latLng.latitude
            pinLong = latLng.longitude
            val iconFactory = IconFactory.getInstance(this)
            val btmap: Bitmap = (ResourcesCompat.getDrawable(
                resources,
                R.drawable.home_marker,
                null
            ) as BitmapDrawable).getBitmap()
            val icon: com.mapbox.mapboxsdk.annotations.Icon = iconFactory.fromBitmap(btmap)
            mapBoxMap?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .icon(icon)
            )
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun requestPermissions() {

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            permissionsRequestCode
        )
    }

    override fun searchItemClick(searchResultDTO: AutocompleteData) {
        viewModel.getAddressFromItemId(searchResultDTO.id)
    }

    private fun getSearchAddressResponse() {
        viewModel.addressJson.observe(this, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    binding!!.addNewAdd.rvSearchResult.visibility = View.GONE
                    parseSearchResultJson(response.data!!)
                }
                is ApiCallBack.Error -> {

                }
                is ApiCallBack.Loading -> {

                }

            }
        })
    }

    fun updateAddressButton() {
        if (cityText.isNotEmpty() && stateText.isNotEmpty() && pincodeText.isNotEmpty()) {
            binding!!.addNewAdd!!.tvConfirm.setBackgroundResource(R.drawable.theme_round_btn)
            binding!!.addNewAdd!!.tvConfirm.isClickable = true
        } else {
            binding!!.addNewAdd!!.tvConfirm.setBackgroundResource(R.drawable.bg_button_disable)
            binding!!.addNewAdd!!.tvConfirm.isClickable = false
        }
    }

    private fun getAddressFromCurrLocatinResponse() {
        viewModel?.reverseGeocodeDataCurrentLocation?.observe(this, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    response.data
                    reverseGeoCodeCurrentLocationResponse = response.data
                    setCurrentAddress()
                }

                is ApiCallBack.Error -> {

                }

                is ApiCallBack.Loading -> {

                }

            }
        })

    }

    fun initAddressList(addresses: List<CreateAddressModel>) {
        binding!!.cvPintHint.visibility = View.GONE
        binding!!.ivShowAddreess.visibility = View.GONE
        binding!!.addressesView.root.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(this)
        binding!!.addressesView!!.rvAddress.layoutManager = layoutManager
        binding!!.addressesView!!.rvAddress.setBackgroundResource(R.color.white)
        binding!!.addressesView!!.rvAddress.adapter = AddressListAdapter(addresses!!, this)
        binding!!.addressesView!!.rvAddress.adapter!!.notifyDataSetChanged()
        binding!!.addressesView!!.ivClose.setOnClickListener {

            binding!!.ivShowAddreess.visibility = View.VISIBLE
            binding!!.addressesView.root.visibility = View.GONE
        }

    }

    override fun addressItemClick(createAddressModel: CreateAddressModel) {

    }

    override fun onBackPressed() {
        super.onBackPressed()
       finishAffinity()
    }
}