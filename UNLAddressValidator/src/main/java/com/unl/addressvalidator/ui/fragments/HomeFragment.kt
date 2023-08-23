package com.unl.addressvalidator.ui.fragments


import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.contentValuesOf
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabasewithmodelclassess.model.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
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
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.ReverseGeoCodeResponse
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.adapters.AddressListAdapter
import com.unl.addressvalidator.ui.adapters.LandMarkResultAdapter
import com.unl.addressvalidator.ui.adapters.SearchResultAdapter
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.builder.MultiImagePicker
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.ui.interfaces.*
import com.unl.addressvalidator.ui.viewmodel.HomeViewModel
import com.unl.addressvalidator.util.Constant
import com.unl.addressvalidator.util.Constant.ALL_DAYS
import com.unl.addressvalidator.util.Constant.FRIDAY
import com.unl.addressvalidator.util.Constant.FROM_HOURS
import com.unl.addressvalidator.util.Constant.FROM_MINS
import com.unl.addressvalidator.util.Constant.MONDAY
import com.unl.addressvalidator.util.Constant.SATURDAY
import com.unl.addressvalidator.util.Constant.SUNDAY
import com.unl.addressvalidator.util.Constant.THURSDAY
import com.unl.addressvalidator.util.Constant.TO_HOURS
import com.unl.addressvalidator.util.Constant.TO_MINS
import com.unl.addressvalidator.util.Constant.TUESDAY
import com.unl.addressvalidator.util.Constant.WEDNESDAY
import com.unl.addressvalidator.util.Utility
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.EnvironmentType
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import com.unl.map.sdk.prefs.DataManager
import org.json.JSONArray
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), SearchItemClickListner, LandmarkClickListner,AddressItemClickListner, LifecycleOwner,
    OperationHoursClickListner, AddressImageClickListner, EntranceClickListner,
    LocationListener {

    var entranceList: ArrayList<EntranceModel> = ArrayList()
    var entranceMarker: ArrayList<Marker> = ArrayList()

    var openCloseTimeList: ArrayList<OpeningHoursSpecificationModel> = ArrayList()
    var weekDays = arrayOf(ALL_DAYS, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY)
    //Get live location from GPS
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null
    lateinit var locationManager: LocationManager

    //Loan Image from gallert
    var selectImageForEntrance = false
    var selectImageForLandmakr = false
    var entranceSelectionEnable = false
    val imageList = ArrayList<String>()
    val landMakrImageList = ArrayList<AddPicturesModel>()
    val dataList = ArrayList<AddPicturesModel>()
    val entranceImageList = ArrayList<AddPicturesModel>()
    val uriList = ArrayList<AddPicturesModel>()
    lateinit var adapter: AddPicturesAdapter
    val dataListSize = 9
    var replaceIndex: Int = 0

    private var apiKey: String? = null
    private var vpmId: String? = null
    lateinit var viewModel: HomeViewModel
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var viewModelStoreOwner: ViewModelStoreOwner
    lateinit var supportFragmentManager: FragmentManager
    var binding: FragmentHomeBinding? = null
     var mapBoxMap: MapboxMap? = null
    var reverseGeoCodeResponse: ReverseGeoCodeResponse? = null
    var reverseGeoCodeCurrentLocationResponse: ReverseGeoCodeResponse? = null
    var pinLat: Double = 0.0
    var pinLong: Double = 0.0
    var currLat: Double = 0.0
    var currLong: Double = 0.0
    var currentAddressText = ""
    var moveCounter: Int = 0
    var isChangeMarker = true
    var updateLocation = true
    var addressType = "home"
    var accessibilityType = ""
    var cityText = ""
    var stateText = ""
    var pincodeText = ""
    lateinit var database: UnlAddressDatabase
    var lifecycleRegistry: LifecycleRegistry? = null
    var createAddressModel: CreateAddressModel? = null

    private val permissionsRequestCode = 123

    //operational hours working details
    var daysType = ALL_DAYS
    var fromHours = "00"
    var fromMins = "00"
    var fromAMPM = "AM"
    var toHours = "00"
    var toMins = "00"
    var toAMPM = "AM"

    // list for add marker reference
    private var markerViewList: ArrayList<View> = ArrayList()

    var selectedLandmarkDataList  = ArrayList<LandmarkDataList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMetadataFromMenifest()
        UnlMap(context!!, apiKey!!, vpmId!!, EnvironmentType.SANDBOX)
        initiateViewModel()
        getAutocompleteResponse()
        getReversgeoCodeResponse()
        getAddressFromCurrLocatinResponse()
        getNearbyLandmarkResponse()
        getAddressImageUploadResponse()
        database = UnlAddressDatabase.getInstance(context = context!!)
        lifecycleRegistry = LifecycleRegistry(this);
        lifecycleRegistry!!.setCurrentState(Lifecycle.State.CREATED);
        requestPermissions()
        for (i in 0 until 9) {
            dataList.add(AddPicturesModel(Uri.EMPTY))
            landMakrImageList.add(AddPicturesModel(Uri.EMPTY))
            entranceImageList.add(AddPicturesModel(Uri.EMPTY))
        }
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
            binding!!.mapView.enableTileSelector(true)
            binding!!.mapView.setGridControls(context!!, true)
            binding!!.mapView.setTileSelectorGravity(Gravity.END)


        }

        binding!!.ivShowAddreess.setOnClickListener {
            getAddressCreated()
            viewModel.getAddress(database)
          // Handler().postDelayed(Runnable {  viewModel.getAddress(database)},1000)
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
                    if (isChangeMarker && moveCounter < 4)
                    {
                        if(entranceSelectionEnable )
                        {
                            val new_position: LatLng = binding!!.mapView.mapbox!!.getProjection().fromScreenLocation(PointF(event.x, event.y - 50 ))
                            showMarker(new_position, "Entrance No 1")
                            addEntrancePoint(new_position.latitude,new_position.longitude)
                        }else
                        {
                            val new_position: LatLng = binding!!.mapView.mapbox!!.getProjection().fromScreenLocation(PointF(event.x , event.y - 50))
                            clearMap()
                            pinLat = new_position.latitude
                            pinLong = new_position.longitude
                            showMarker(new_position, "home")
                            callReverseGeoCode(new_position)
                        }

                    }else if( entranceSelectionEnable && moveCounter < 4 )
                    {
                        isChangeMarker = true
                        clearMap()
                        val new_position: LatLng = binding!!.mapView.mapbox!!.getProjection().fromScreenLocation(PointF(event.x, event.y - 50))
                        showMarker(new_position, "Entrance No 1")
                        addEntrancePoint(new_position.latitude,new_position.longitude)
                    }
                    moveCounter = 0
                }
                return false
            }
        })

        binding!!.hidePinHint.setOnClickListener {
            binding!!.cvPintHint.visibility = View.GONE
        }

        binding!!.addNewAdd.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s!!.toString().length > 1 /*&& fromSelection == false*/) {
                        // binding!!.progressBar.visibility = View.VISIBLE
                        var searchNearbyMe : JsonObject? = JsonObject()
                        var jsonArray = JsonArray()
                        jsonArray.add(pinLong)
                        jsonArray.add(pinLat)
                        searchNearbyMe!!.add("Point", jsonArray)

                        viewModel.getAutocompleteData(s.toString(),searchNearbyMe!!)
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
        viewModel.getAddress(database)
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
    fun clearMap() {
        markerViewList.forEach {
            binding!!.mapView.removeView(it)
        }
        markerViewList.clear()
        mapBoxMap?.clear()
    }

    //Create marker for Address
    fun showMarker(latLng: LatLng, address: String) {
        try {

            if(entranceSelectionEnable)
            {
                val iconFactory = IconFactory.getInstance(context!!)
                val btmap: Bitmap = (ResourcesCompat.getDrawable(
                    context!!.resources,
                    R.drawable.entrance_marker,
                    null
                ) as BitmapDrawable).getBitmap()
                val icon: com.mapbox.mapboxsdk.annotations.Icon = iconFactory.fromBitmap(btmap)

                var marker  = MarkerOptions()
                    .position(latLng)
                    .icon(icon)

                entranceMarker.add(mapBoxMap?.addMarker(marker)!!)

            }else
            {
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
            }

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


    private fun getNearbyLandmarkResponse() {
        viewModel.nearbyLandmark.observe(lifecycleOwner, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    binding!!.progressBar.visibility = View.GONE
                    showNearbyLandmark(Utility.parseNearbyLandmarkJson(response.data!!))
                }

                is ApiCallBack.Error -> {
                    binding!!.progressBar.visibility = View.GONE

                }

                is ApiCallBack.Loading -> {
                    binding!!.progressBar.visibility = View.VISIBLE
                }

            }
        })

    }


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
    }

    fun getAddressCreated() {
        viewModel.addresslist.observe(this, { users ->
            try {

               if(users!= null && users.size>0)
                {
                     initAddressList(users)
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
                    showReverseGeoAddress()
                }

                is ApiCallBack.Error -> {
                        Toast.makeText(context , ""+response.message,Toast.LENGTH_SHORT).show()
                }

                is ApiCallBack.Loading -> {

                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MultiImagePicker.REQUEST_PICK_MULTI_IMAGES && resultCode == AppCompatActivity.RESULT_OK) {
            val result = MultiImagePicker.Result(data)
            if (result.isSuccess()) {
                val imageList = result.getImageList()

                uriList.clear()
                for (uri in imageList) {
                    uriList.add(AddPicturesModel(uri))
                }

                val uriListSize = uriList.size
                if (selectImageForLandmakr) {
                    for (i in replaceIndex until dataListSize) {
                        if (i - replaceIndex < uriListSize) {
                            landMakrImageList[i] =
                                AddPicturesModel(uriList[i - replaceIndex].ivPhotos)
                        } else {
                            landMakrImageList[i] = AddPicturesModel(Uri.EMPTY)
                        }
                    }
                } else if(selectImageForEntrance)
                {
                    for (i in replaceIndex until dataListSize) {
                        if (i - replaceIndex < uriListSize) {
                            entranceImageList[i] =
                                AddPicturesModel(uriList[i - replaceIndex].ivPhotos)
                        } else {
                            entranceImageList[i] = AddPicturesModel(Uri.EMPTY)
                        }
                    }
                }
                else {
                    for (i in replaceIndex until dataListSize) {
                        if (i - replaceIndex < uriListSize) {
                            dataList[i] = AddPicturesModel(uriList[i - replaceIndex].ivPhotos)
                        } else {
                            dataList[i] = AddPicturesModel(Uri.EMPTY)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }


        }
    }

    fun openImagePicker() {
        MultiImagePicker.with(activity)
            .setSelectionLimit(9)
            .open()
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

            lifecycleOwner1: Activity,
            supportFragmentManager1: FragmentManager
        ) =
            HomeFragment().apply {
                lifecycleOwner = lifecycleOwner1 as LifecycleOwner
                viewModelStoreOwner = lifecycleOwner1 as ViewModelStoreOwner
                supportFragmentManager = supportFragmentManager1

            }
    }


    private fun getMetadataFromMenifest() {
        val info: ApplicationInfo = context!!.getPackageManager().getApplicationInfo(
            activity.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = info.metaData
        vpmId = bundle.getString(Constant.META_VPM_ID_KEY)
        apiKey = bundle.getString(Constant.META_API_KEY)
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
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
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

    override fun landmarkItemClick(landmarkDataList: LandmarkDataList) {


        if(selectedLandmarkDataList.contains(landmarkDataList))
        {
            selectedLandmarkDataList.remove(landmarkDataList)
        }
        if(selectedLandmarkDataList.size >0)
        {
            binding!!.addLandmark!!.tvConfirm.setBackgroundResource(R.drawable.theme_round_btn)
            binding!!.addLandmark!!.tvConfirm.isEnabled = true
        }else
        {
            binding!!.addLandmark!!.tvConfirm.setBackgroundResource(R.drawable.bg_button_disable)
            binding!!.addLandmark!!.tvConfirm.isEnabled = false
        }


    }

    override fun uploadLandmarkPic(position: Int, resulttList: ArrayList<LandmarkDataList>) {
        selectImageForEntrance = false
        selectImageForLandmakr = true
        showLandmarkPictureDialog(position, resulttList)
    }


    override fun addressImageClick(index : Int, isReplace : Boolean) {
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
        MultiImagePicker.with(activity)
            .setSelectionLimit(imageLimit)
            .open()
    }


    override fun dayClick(day: String) {
        daysType = day
        setFromAndToTime()
    }

    override fun itemClick(hour: String, type: String) {
        if (type.equals(FROM_HOURS))
        {
            fromHours = hour
        }else if (type.equals(FROM_MINS))
        {
            fromMins = hour
        }else if (type.equals(TO_HOURS))
        {
            toHours = hour
        }else if (type.equals(TO_MINS))
        {
            toMins = hour
        }

    }

    override fun entranceEditClick(position: Int, entranceModel: ArrayList<EntranceModel>) {
        editeEntrance(position,entranceModel)
    }

    override fun entranceDeleteClick(position: Int,entranceModel: ArrayList<EntranceModel>) {
        removeEntrance(position,entranceModel)
    }

    override fun entranceImageClick(position: Int, entranceModel: ArrayList<EntranceModel>) {
        selectImageForEntrance = true
        selectImageForLandmakr = false
        showEntrancePictureDialog(position,entranceModel)
    }


    override fun addressItemClick(createAddressModel: CreateAddressModel) {

    }
}



