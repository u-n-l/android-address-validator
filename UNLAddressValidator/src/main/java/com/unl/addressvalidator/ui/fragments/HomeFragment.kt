package com.unl.addressvalidator.ui.fragments


import android.Manifest
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasewithmodelclassess.model.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.unl.addressvalidator.R
import com.unl.addressvalidator.database.UnlAddressDatabase
import com.unl.addressvalidator.databinding.FragmentHomeBinding
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.model.dbmodel.CreateAddressModel
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.adapters.SearchResultAdapter
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner
import com.unl.addressvalidator.ui.viewmodel.HomeViewModel
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.EnvironmentType
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), SearchItemClickListner, LifecycleOwner, LocationListener {

//Get live location from GPS

    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    lateinit var locationManager: LocationManager


    private var apiKey: String? = null
    private var vpmId: String? = null
    lateinit var viewModel: HomeViewModel
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var viewModelStoreOwner: ViewModelStoreOwner
    lateinit var supportFragmentManager: FragmentManager
    private var binding: FragmentHomeBinding? = null
    private var mapBoxMap: MapboxMap? = null
    private var reverseGeoCodeResponse: ReverseGeoCodeResponse? = null
    private var reverseGeoCodeCurrentLocationResponse: ReverseGeoCodeResponse? = null

    var pinLat: Double = 0.0
    var pinLong: Double = 0.0

    var currLat: Double = 0.0
    var currLong: Double = 0.0

    var currentAddressText = ""
    var moveCounter: Int = 0
    var isChangeMarker = true
    var updateLocation = true
    var addressType = "home"
    var cityText = ""
    var stateText = ""
    var pincodeText = ""
    private lateinit var database: UnlAddressDatabase
    var lifecycleRegistry: LifecycleRegistry? = null

    private val permissionsRequestCode = 123

    // list for add marker reference
    private var markerViewList: ArrayList<View> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UnlMap(context!!, apiKey!!, vpmId!!, EnvironmentType.SANDBOX)
        initiateViewModel()
        getAutocompleteResponse()
        getReversgeoCodeResponse()
        getAddressFromCurrLocatinResponse()
        database = UnlAddressDatabase.getInstance(context = context!!)
        lifecycleRegistry = LifecycleRegistry(this);
        lifecycleRegistry!!.setCurrentState(Lifecycle.State.CREATED);
        requestPermissions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding!!.mapView.getMapAsync {
            mapBoxMap = it
            binding!!.mapView.fm = supportFragmentManager
            binding!!.mapView.activity = activity
            binding!!.mapView.lifeCycleOwner = viewModelStoreOwner
            binding!!.mapView.enableIndoorMap = false
            binding!!.mapView.viewLifecycle = lifecycleOwner
            binding!!.mapView.enableTileSelector(false)
            binding!!.mapView.setGridControls(context!!, true)
            binding!!.mapView.setTileSelectorGravity(Gravity.END)
        }

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
                        val new_position: LatLng =
                            binding!!.mapView.mapbox!!.getProjection()
                                .fromScreenLocation(PointF(event.x, event.y))

                        clearMap()
                        pinLat = new_position.latitude
                        pinLong = new_position.longitude
                        showMarker(new_position, "home")
                        callReverseGeoCode(new_position)
                    }
                    moveCounter = 0
                }


                return false
            }
        })

        binding!!.addNewAdd.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s!!.toString().length > 1 /*&& fromSelection == false*/) {
                        // binding!!.progressBar.visibility = View.VISIBLE

                        viewModel.getAutocompleteData(s.toString())
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


        setSearchListView()
        selectedLabel(0)
        setNewAddressClick()
        getSearchAddressResponse()
        return binding!!.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleRegistry!!.setCurrentState(Lifecycle.State.STARTED);
    }

    private fun callReverseGeoCode(latlng: LatLng) {
        var jsonObject = JsonObject()
        var jsonArray = JsonArray()
        jsonArray.add(latlng.latitude)
        jsonArray.add(latlng.longitude)
        jsonObject.add("Point", jsonArray)
        viewModel.getAddfromLocation(jsonObject)
    }

    //Clear Map before adding new Marker
    private fun clearMap() {
        markerViewList.forEach {
            binding!!.mapView.removeView(it)
        }
        markerViewList.clear()
        mapBoxMap?.clear()
    }

    //Create marker for Address
    private fun showMarker(latLng: LatLng, address: String) {
        try {

            val iconFactory = IconFactory.getInstance(context!!)
            val btmap: Bitmap = (ResourcesCompat.getDrawable(
                context!!.resources,
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
        /*  mapBoxMap?.addMarker(
              MarkerOptions()
                  .position(latLng)
                  .title(address)

          )*/
    }


    private fun setSearchListView() {
        val layoutManager = LinearLayoutManager(activity)
        binding!!.addNewAdd!!.rvSearchResult.layoutManager = layoutManager
        val separator = DividerItemDecoration(activity, layoutManager.orientation)
        binding!!.addNewAdd!!.rvSearchResult.addItemDecoration(separator)
        binding!!.addNewAdd!!.rvSearchResult.setBackgroundResource(R.color.white)
        val lateralPadding = resources.getDimension(R.dimen.big_padding).toInt()
        binding!!.addNewAdd!!.rvSearchResult.setPadding(lateralPadding, 0, lateralPadding, 0)
    }

    private fun initiateViewModel() {
        viewModel = ViewModelProvider(viewModelStoreOwner!!)[HomeViewModel::class.java]
    }

    private fun getAutocompleteResponse() {
        viewModel.autoCompleteData.observe(lifecycleOwner, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    response.data

                    binding!!.addNewAdd.rvSearchResult.adapter =
                        SearchResultAdapter(response.data!!, this)
                    binding!!.addNewAdd.rvSearchResult.visibility = View.VISIBLE
                    //   Toast.makeText(context,""+response.data!!.size,Toast.LENGTH_SHORT).show()
                }

                is ApiCallBack.Error -> {

                }

                is ApiCallBack.Loading -> {

                }

            }
        })

    }


    private fun getSearchAddressResponse() {
        viewModel.addressJson.observe(lifecycleOwner, { response ->
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

    fun parseSearchResultJson(rawData: JsonObject) {

        try {
            val parsed = JSONObject(rawData.toString())
            val featuresArray: JSONArray = parsed.getJSONArray("geojson:Features")
            var featureArrayJson: String = featuresArray.getString(0)
            val featureJson: JSONObject = JSONObject(featureArrayJson)
            var propertiesData: JSONObject = featureJson.getJSONObject("geojson:properties")
            val addressArray: JSONArray = propertiesData.getJSONArray("vocabulary:address")
            var AddressObject: String = addressArray.getString(0)
            val AddressJson: JSONObject = JSONObject(AddressObject)
            var addressType: String = AddressJson.getString("@type")
            var addressRegion: String = AddressJson.getString("vocabulary:addressRegion")
            var cityDistrict: String = AddressJson.getString("vocabulary:cityDistrict")
            var houseNumber: String = AddressJson.getString("vocabulary:houseNumber")
            var postalCode: String = AddressJson.getString("vocabulary:postalCode")
            var countryCode: String = AddressJson.getString("vocabulary:countryCode")
            var stateDistrict: String = AddressJson.getString("vocabulary:stateDistrict")
            var streetAddress: String = AddressJson.getString("vocabulary:streetAddress")

            clearAddressFields()
            setAddressFromSearchResult(
                houseNumber,
                cityDistrict,
                stateDistrict,
                postalCode,
                countryCode,
                streetAddress
            )


            val geoLocationArray: JSONArray = propertiesData.getJSONArray("vocabulary:geo")

            var geoLocObject: String = geoLocationArray.getString(0)

            val geoLocJson: JSONObject = JSONObject(geoLocObject)

            var lattitude: Double = geoLocJson.getDouble("vocabulary:latitude")
            var longitude: Double = geoLocJson.getDouble("vocabulary:longitude")

            isChangeMarker = false
            clearMap()
            showMarker(LatLng(lattitude, longitude), "addressType")
            changeCameraPosition(LatLng(lattitude, longitude))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun changeCameraPosition(latlng: LatLng) {
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
        binding!!.mapView!!.mapbox!!.setMaxZoomPreference(17.00)
    }

    private fun setAddressFromSearchResult(
        houseNumber: String,
        cityDistrict: String,
        stateDistrict: String,
        postalCode: String,
        countryCode: String,
        streetAddress: String
    ) {

        binding!!.addNewAdd!!.edtUnit.setText(houseNumber)
        binding!!.addNewAdd!!.edtStreet.setText(streetAddress)
        binding!!.addNewAdd!!.edtCity.setText(cityDistrict)
        binding!!.addNewAdd!!.edtPincode.setText(postalCode)
        binding!!.addNewAdd!!.edtState.setText(stateDistrict)
        binding!!.addNewAdd!!.edtCountry.setText(countryCode)


    }


    private fun getAddressCreated() {
        viewModel.addresslist.observe(this, { users ->
            try {
                for (user in users) {
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun getReversgeoCodeResponse() {
        viewModel?.reverseGeocodeData?.observe(lifecycleOwner, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    response.data
                    reverseGeoCodeResponse = response.data
                    binding!!.confirmAddress!!.root.visibility = View.VISIBLE
                    binding!!.addNewAdd!!.root.visibility = View.GONE
                    showReverseGeoAddress()
                }

                is ApiCallBack.Error -> {

                }

                is ApiCallBack.Loading -> {

                }

            }
        })

    }

    private fun showReverseGeoAddress() {
        try {
            binding!!.confirmAddress!!.tvAddressText.text =
                reverseGeoCodeResponse!!.features!!.get(0).properties.place.name
            binding!!.confirmAddress!!.tvConfirm.setOnClickListener {

            }
            binding!!.confirmAddress!!.mainLayout.setOnClickListener {

            }

            binding!!.confirmAddress!!.addNew.setOnClickListener {
                binding!!.confirmAddress!!.root.visibility = View.GONE
                binding!!.addNewAdd!!.root.visibility = View.VISIBLE
            }
            binding!!.confirmAddress!!.editAddress.setOnClickListener {
                binding!!.confirmAddress!!.root.visibility = View.GONE
                binding!!.addNewAdd!!.root.visibility = View.VISIBLE
                setAddressFromGeocodeAddress()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun setAddressFromGeocodeAddress() {

        binding!!.addNewAdd!!.edtUnit.setText(
            reverseGeoCodeResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).house_number
        )
        binding!!.addNewAdd!!.edtStreet.setText(
            reverseGeoCodeResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).street_address
        )
        binding!!.addNewAdd!!.edtCity.setText(
            reverseGeoCodeResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).city_district
        )
        binding!!.addNewAdd!!.edtPincode.setText(
            reverseGeoCodeResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).postal_code
        )
        binding!!.addNewAdd!!.edtState.setText(
            reverseGeoCodeResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).state_district
        )
        binding!!.addNewAdd!!.edtCountry.setText(
            reverseGeoCodeResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).country_code
        )
    }

    private fun setNewAddressClick() {
        binding!!.addNewAdd!!.tvConfirm.setOnClickListener {
            createAddress()
        }
        binding!!.addNewAdd!!.mainLayout.setOnClickListener {

        }

        binding!!.addNewAdd!!.currentAddressLayout.setOnClickListener {
            clearMap()
            clearAddressFields()
            setAddressFromCurrentLcation()
            showMarker(LatLng(currLat, currLong), "Current Location")
            changeCameraPosition(LatLng(currLat, currLong))
        }

        binding!!.addNewAdd.rlHomeIcon.setOnClickListener(View.OnClickListener { selectedLabel(0) })
        binding!!.addNewAdd.rlOfficeIcon.setOnClickListener(View.OnClickListener { selectedLabel(1) })
        binding!!.addNewAdd.rlAddLableIcon.setOnClickListener(View.OnClickListener { selectedLabel(2) })


        binding!!.addNewAdd!!.edtCity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s!!.toString().length > 1) {
                        cityText = s.toString()
                    } else
                        cityText = ""

                    updateButton()
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

        binding!!.addNewAdd!!.edtState.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s!!.toString().length > 1) {
                        stateText = s.toString()
                    } else
                        stateText = ""
                    updateButton()
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

        binding!!.addNewAdd!!.edtPincode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s!!.toString().length > 1) {
                        pincodeText = s.toString()
                    } else
                        pincodeText = ""

                    updateButton()
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
    }

    private fun updateButton() {

        if (cityText.isNotEmpty() && stateText.isNotEmpty() && pincodeText.isNotEmpty()) {
            binding!!.addNewAdd!!.tvConfirm.setBackgroundResource(R.drawable.theme_round_btn)
            binding!!.addNewAdd!!.tvConfirm.isClickable = true
        } else {
            binding!!.addNewAdd!!.tvConfirm.setBackgroundResource(R.drawable.bg_button_disable)
            binding!!.addNewAdd!!.tvConfirm.isClickable = false
        }

    }


    private fun createAddress() {

        var houseNo = binding!!.addNewAdd!!.edtUnit.text!!.toString()
        var floor = binding!!.addNewAdd!!.edtFloor.text!!.toString()
        var buildingNum = binding!!.addNewAdd!!.edtBuildingNum.text!!.toString()
        var buildingName = binding!!.addNewAdd!!.edtBuildingName.text!!.toString()
        var streetName = binding!!.addNewAdd!!.edtStreet.text!!.toString()
        var neighbour = binding!!.addNewAdd!!.edtNeighbour.text!!.toString()
        var country = binding!!.addNewAdd!!.edtCountry.text!!.toString()
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
            "Gyan Sagar school",
            "schood",
            "22.56765434",
            "75.324565432",
            "https://www.image.jpeg"
        )

        var entranceList: ArrayList<EntranceModel> = ArrayList()
        val entranceModel = EntranceModel("main gate", "1")
        entranceList.add(entranceModel)

        var openCloseTimeList: ArrayList<OpeningHoursSpecificationModel> = ArrayList()
        val openingHoursSpecificationModel =
            OpeningHoursSpecificationModel("11:00:00", "Monday", false, "08:00:00")
        openCloseTimeList.add(openingHoursSpecificationModel)
        val createAddress = CreateAddressModel(
            2,
            addressModel,
            addressType,
            locationModel,
            landmarkModel,
            entranceList,
            openCloseTimeList
        )
        viewModel.insertAddress(database, createAddress)

        getAddressCreated()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragment.
         */

        @JvmStatic
        fun newInstance(
            apiKey1: String,
            vpmId1: String,
            lifecycleOwner1: LifecycleOwner,
            viewModelStoreOwner1: ViewModelStoreOwner,
            supportFragmentManager1: FragmentManager
        ) =
            HomeFragment().apply {
                apiKey = apiKey1
                vpmId = vpmId1
                lifecycleOwner = lifecycleOwner1
                viewModelStoreOwner = viewModelStoreOwner1
                supportFragmentManager = supportFragmentManager1
                /*arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }*/
            }
    }


    fun selectedLabel(position: Int) {
        binding!!.addNewAdd!!.rlHomeIcon.setBackgroundResource(R.drawable.address_label_shape)
        binding!!.addNewAdd!!.rlOfficeIcon.setBackgroundResource(R.drawable.address_label_shape)
        binding!!.addNewAdd!!.rlAddLableIcon.setBackgroundResource(R.drawable.address_label_shape)
        binding!!.addNewAdd!!.ivHome.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.black2)))
        binding!!.addNewAdd!!.ivOffice.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.black2)))
        binding!!.addNewAdd!!.ivAddLabel.setImageTintList(
            ColorStateList.valueOf(
                resources.getColor(
                    R.color.black2
                )
            )
        )
        binding!!.addNewAdd!!.tvAddLabel.setTextColor(resources.getColor(R.color.black2))
        binding!!.addNewAdd!!.tvOffice.setTextColor(resources.getColor(R.color.black2))
        binding!!.addNewAdd!!.tvHome.setTextColor(resources.getColor(R.color.black2))
        binding!!.addNewAdd!!.ivCheckAddLabel.setImageResource(R.drawable.labelunchecked)
        binding!!.addNewAdd!!.ivCheckOffice.setImageResource(R.drawable.labelunchecked)
        binding!!.addNewAdd!!.ivCheckHome.setImageResource(R.drawable.labelunchecked)

        binding!!.addNewAdd.addLabelView.visibility = View.GONE

        if (position == 0) {
            addressType = "home"
            binding!!.addNewAdd!!.rlHomeIcon.setBackgroundResource(R.drawable.label_selected_shape)
            binding!!.addNewAdd!!.ivHome.setImageTintList(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.purple1
                    )
                )
            )
            binding!!.addNewAdd!!.tvHome.setTextColor(resources.getColor(R.color.purple1))
            binding!!.addNewAdd!!.ivCheckHome.setImageResource(R.drawable.labelcheck)
        } else if (position == 1) {
            addressType = "office"
            binding!!.addNewAdd!!.rlOfficeIcon.setBackgroundResource(R.drawable.label_selected_shape)
            binding!!.addNewAdd!!.ivOffice.setImageTintList(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.purple1
                    )
                )
            )
            binding!!.addNewAdd!!.tvOffice.setTextColor(resources.getColor(R.color.purple1))
            binding!!.addNewAdd!!.ivCheckOffice.setImageResource(R.drawable.labelcheck)
        } else if (position == 2) {

            binding!!.addNewAdd.addLabelView.visibility = View.VISIBLE
            addressType = "other"
            binding!!.addNewAdd!!.rlAddLableIcon.setBackgroundResource(R.drawable.label_selected_shape)
            binding!!.addNewAdd!!.ivAddLabel.setImageTintList(
                ColorStateList.valueOf(
                    resources.getColor(
                        R.color.purple1
                    )
                )
            )
            binding!!.addNewAdd!!.tvAddLabel.setTextColor(resources.getColor(R.color.purple1))
            binding!!.addNewAdd!!.ivCheckAddLabel.setImageResource(R.drawable.labelcheck)
        }
    }


    private fun getMetadataFromMenifest() {
        /*  val info: ApplicationInfo = context!!.getPackageManager().getApplicationInfo(
              activity.packageName,
              PackageManager.GET_META_DATA
          )
          val bundle = info.metaData
          val appId = bundle.getInt("com.unl.global.vpmid")
          Toast.makeText(context, "" + appId, Toast.LENGTH_LONG).show()
          Toast.makeText(context, "" + DataManager.getApiKey(), Toast.LENGTH_LONG).show()*/
    }

    override fun searchItemClick(searchResultDTO: AutocompleteData) {
        viewModel.getAddressFromItemId(searchResultDTO.id)
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry!!
    }

    override fun onLocationChanged(location: Location) {
        updateCurrentLocation(location)
    }

    private fun requestPermissions() {

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionsRequestCode
        )
    }

    override fun onResume() {
        super.onResume()
        iniitLocatinoManager()
    }

    private fun iniitLocatinoManager() {
        try {

            locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
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
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
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
            changeCameraPosition(LatLng(location.latitude, location.longitude))
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

    private fun getAddressFromCurrLocatinResponse() {
        viewModel?.reverseGeocodeDataCurrentLocation?.observe(lifecycleOwner, { response ->
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

    private fun setCurrentAddress() {
        currentAddressText =
            reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.place.name
        binding!!.addNewAdd!!.currentAddress.text = currentAddressText

    }


    private fun setAddressFromCurrentLcation() {

        binding!!.addNewAdd!!.edtUnit.setText(
            reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).house_number
        )
        binding!!.addNewAdd!!.edtStreet.setText(
            reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).street_address
        )
        binding!!.addNewAdd!!.edtCity.setText(
            reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).city_district
        )
        binding!!.addNewAdd!!.edtPincode.setText(
            reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).postal_code
        )
        binding!!.addNewAdd!!.edtState.setText(
            reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).state_district
        )
        binding!!.addNewAdd!!.edtCountry.setText(
            reverseGeoCodeCurrentLocationResponse!!.features!!.get(0).properties.postal_address.get(
                0
            ).country_code
        )
    }

    fun clearAddressFields() {
        binding!!.addNewAdd!!.edtUnit.setText("")
        binding!!.addNewAdd!!.edtStreet.setText("")
        binding!!.addNewAdd!!.edtCity.setText("")
        binding!!.addNewAdd!!.edtPincode.setText("")
        binding!!.addNewAdd!!.edtState.setText("")
        binding!!.addNewAdd!!.edtCountry.setText("")
    }


}