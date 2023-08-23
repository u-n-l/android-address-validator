package com.unl.addressvalidator.ui.landmark

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdatabasewithmodelclassess.model.LandmarkModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.unl.addressvalidator.R

import com.unl.addressvalidator.databinding.ActivityLandmarkBinding
import com.unl.addressvalidator.databinding.AddPicturesPopupBinding
import com.unl.addressvalidator.model.autocomplet.AutocompleteData
import com.unl.addressvalidator.model.landmark.LandmarkDataList
import com.unl.addressvalidator.model.reversegeocode.*
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.adapters.LandMarkResultAdapter
import com.unl.addressvalidator.ui.adapters.SearchResultAdapter
import com.unl.addressvalidator.ui.entrances.EntrancesActivity
import com.unl.addressvalidator.ui.fragments.*
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.createAddressModel
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.pinLat
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity.Companion.pinLong
import com.unl.addressvalidator.ui.homescreen.ValidatorViewModel
import com.unl.addressvalidator.ui.homescreen.clearAddressFields
import com.unl.addressvalidator.ui.homescreen.setAddressFromSearchResult
import com.unl.addressvalidator.ui.imagepicker.adapter.AddPicturesAdapter
import com.unl.addressvalidator.ui.imagepicker.builder.MultiImagePicker
import com.unl.addressvalidator.ui.imagepicker.data.AddPicturesModel
import com.unl.addressvalidator.ui.interfaces.AddressImageClickListner
import com.unl.addressvalidator.ui.interfaces.LandmarkClickListner
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner
import com.unl.addressvalidator.ui.viewmodel.HomeViewModel
import com.unl.addressvalidator.util.Utility
import com.unl.addressvalidator.util.Utility.changeCameraPosition
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.EnvironmentType
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import com.unl.map.sdk.prefs.DataManager
import com.unl.map.sdk.views.UnlMapView
import org.json.JSONArray
import org.json.JSONObject

/**
 * [LandmarkActivity] provide functionality to add Landmarks.
 * You can add landmark by search
 * You can select landmakr from default list
 * @constructor
 *
 */

class LandmarkActivity : AppCompatActivity(), LandmarkClickListner, AddressImageClickListner,
    SearchItemClickListner {
    var mapBoxMap: MapboxMap? = null
    var binding: ActivityLandmarkBinding? = null
    lateinit var viewModel: LandmarkViewModel

    val landmarkImageList = ArrayList<AddPicturesModel>()
    val dataListSize = 9
    var replaceIndex: Int = 0
    lateinit var adapter: AddPicturesAdapter
    var landmarkDataList = ArrayList<LandmarkDataList>()
    var isReplace: Boolean = false
    lateinit  var bind : AddPicturesPopupBinding
    var selectedLandmarkDataList  = ArrayList<LandmarkDataList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UnlMap(this, DataManager.getApiKey()!!, DataManager.getVpmId()!!, EnvironmentType.SANDBOX)
        binding = ActivityLandmarkBinding.inflate(getLayoutInflater())
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

        landmarkActivity = this

        binding!!.addLandmark.edtSearch.addTextChangedListener(object : TextWatcher {
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


        binding!!.backBtn.setOnClickListener {
            finish()
        }


        setSearchListView()
        initiateViewModel()
        initLandmarkList()
        getNearbyLandmarkResponse()
        getAutocompleteResponse()
        getSearchAddressResponse()
        callNearbyLandMark()

        binding!!.addLandmark!!.tvConfirm.isEnabled = false

        binding!!.addLandmark!!.tvConfirm.setOnClickListener {

            //   initEntrance()
            updateLandmark()
            startActivity(Intent(this, EntrancesActivity::class.java))
        }

        binding!!.addLandmark!!.tvSkip.setOnClickListener {
            binding!!.addLandmark!!.root.visibility = View.GONE
            startActivity(Intent(this, EntrancesActivity::class.java))
            //initEntrance()
        }

    }

    private fun setSearchListView() {
        val layoutManager = LinearLayoutManager(this)
        binding!!.addLandmark!!.rvSearchResult.layoutManager = layoutManager
        val separator = DividerItemDecoration(this, layoutManager.orientation)
        binding!!.addLandmark!!.rvSearchResult.addItemDecoration(separator)
        binding!!.addLandmark!!.rvSearchResult.setBackgroundResource(R.color.white)
        val lateralPadding = resources.getDimension(R.dimen.big_padding).toInt()
        binding!!.addLandmark!!.rvSearchResult.setPadding(lateralPadding, 0, lateralPadding, 0)
    }

    private fun initiateViewModel() {
        viewModel = ViewModelProvider(this)[LandmarkViewModel::class.java]
    }

    fun setMapData() {
        var latlng = LatLng(pinLat,pinLong)
        changeCameraPosition(latlng, mapBoxMap!!)
        showMarker(latlng, "home")
    }


    //Create marker for Address
    fun showMarker(latLng: LatLng, address: String) {
        try {

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

    private fun callNearbyLandMark() {
        var circleObject = JsonObject()
        var jsonObject = JsonObject()
        var jsonArray = JsonArray()
        jsonArray.add(pinLong)
        jsonArray.add(pinLat)
        jsonArray.add(1000)
        jsonObject.add("Point", jsonArray)
        circleObject.add("Circle", jsonObject)
        viewModel.getNearbyLandmark(circleObject)
    }

    fun showNearbyLandmark(landmarkList: ArrayList<ReverseGeoCodeResponse>) {
        try {
            if (landmarkList.size <= 0) {
                //binding!!.addLandmark!!.root.visibility = View.GONE

            } else {
                landmarkDataList.clear()

                landmarkList.forEach {
                    landmarkDataList.add(LandmarkDataList("", "", it!!, ArrayList<String>()))
                }

                binding!!.addLandmark.rvLandmark.adapter =
                    LandMarkResultAdapter(landmarkDataList, this)
                binding!!.addLandmark.rvLandmark.adapter!!.notifyDataSetChanged()
                landmarkList.forEach {
                    showLandmarkMarker(
                        LatLng(
                            it.features.get(0).geometry.coordinates.get(0),
                            it.features.get(0).geometry.coordinates.get(1)
                        ), it.features.get(0).properties!!.category_name
                    )
                }

            }
            //   Toast.makeText(context,"size : "+landmarkList.size,Toast.LENGTH_SHORT).show()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    fun initLandmarkList() {
        val layoutManager = LinearLayoutManager(this)
        binding!!.addLandmark!!.rvLandmark.layoutManager = layoutManager
        binding!!.addLandmark!!.rvLandmark.setBackgroundResource(R.color.white)
    }

    fun updateLandmark() {

        var landmarkModelList = ArrayList<LandmarkModel>()

        selectedLandmarkDataList.forEach {
            val landmarkModel = LandmarkModel(
                it!!.addressInfo!!.features.get(0).properties!!.postal_address.get(0).house_number,
                it!!.addressInfo!!.features.get(0).type,
                "" + it!!.addressInfo!!.features.get(0).geometry.coordinates.get(0),
                "" + it!!.addressInfo!!.features.get(0).geometry.coordinates.get(0),
                "", it!!.imageList
            )
            landmarkModelList.add(landmarkModel)
        }


        createAddressModel!!.landmarkModel = landmarkModelList
    }

    private fun getNearbyLandmarkResponse() {
        viewModel.nearbyLandmark.observe(this, { response ->
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

    override fun landmarkItemClick(landmarkModel: LandmarkDataList) {
        if(selectedLandmarkDataList.contains(landmarkModel))
        {
            selectedLandmarkDataList.remove(landmarkModel)
        }
        else
        {
            selectedLandmarkDataList.add(landmarkModel)
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

        landmarkImageList.clear()
        if (resulttList.get(position).imageList != null && resulttList.get(position).imageList.size > 0) {
            for (i in 0..resulttList.get(position).imageList.size - 1) {
                landmarkImageList.add(
                    AddPicturesModel(
                        Uri.parse(
                            resulttList.get(position).imageList.get(
                                i
                            )
                        )
                    )
                )
            }
            for (i in resulttList.get(position).imageList.size until 9) {
                landmarkImageList.add(AddPicturesModel(Uri.EMPTY))
            }
            showLandmarkPictureDialog(position, resulttList)
            updateAddPictureSavebtn(true)
        } else {
            for (i in 0 until 9) {
                landmarkImageList.add(AddPicturesModel(Uri.EMPTY))
            }
            showLandmarkPictureDialog(position, resulttList)
        }


    }

    override fun addressImageClick(index: Int, isReplaceImage: Boolean) {
        isReplace = isReplaceImage
        if (isReplace) {
            replaceIndex = index
            openImagePicker(1)
        } else {
            replaceIndex = landmarkImageList.indexOfFirst { it.ivPhotos == Uri.EMPTY }
            openImagePicker(dataListSize - replaceIndex)
        }
    }

    fun openImagePicker(imageLimit: Int) {
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
                        landmarkImageList[replaceIndex] = AddPicturesModel(uriList[0].ivPhotos)
                    } else {
                        for (i in replaceIndex until dataListSize) {
                            if (i - replaceIndex < uriListSize) {
                                landmarkImageList[i] =
                                    AddPicturesModel(uriList[i - replaceIndex].ivPhotos)
                            } else {
                                landmarkImageList[i] = AddPicturesModel(Uri.EMPTY)
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


    private fun getAutocompleteResponse() {
        viewModel.autoCompleteData.observe(this, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    response.data
                    binding!!.addLandmark.rvSearchResult.adapter =
                        SearchResultAdapter(response.data!!, this)
                    binding!!.addLandmark.rvSearchResult.visibility = View.VISIBLE
                }

                is ApiCallBack.Error -> {

                }

                is ApiCallBack.Loading -> {

                }

            }
        })

    }

    //Create marker for Address
    fun showLandmarkMarker(latLng: LatLng, address: String) {
        try {

            val iconFactory = IconFactory.getInstance(this)
            val btmap: Bitmap = (ResourcesCompat.getDrawable(
                resources,
                R.drawable.iv_restaurant,
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


    override fun searchItemClick(searchResultDTO: AutocompleteData) {
        viewModel.getAddressFromItemId(searchResultDTO.id)
    }


    private fun getSearchAddressResponse() {
        viewModel.addressJson.observe(this, { response ->
            when (response) {
                is ApiCallBack.Success -> {
                    binding!!.addLandmark.rvSearchResult.visibility = View.GONE
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

            val categoryType: String = propertiesData.getString("@type")
            val businessName: String = propertiesData.getString("vocabulary:name")

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


            val geoLocationArray: JSONArray = propertiesData.getJSONArray("vocabulary:geo")

            var geoLocObject: String = geoLocationArray.getString(0)

            val geoLocJson: JSONObject = JSONObject(geoLocObject)

            var lattitude: Double = geoLocJson.getDouble("vocabulary:latitude")
            var longitude: Double = geoLocJson.getDouble("vocabulary:longitude")

            val features: ArrayList<FeaturesData> = ArrayList<FeaturesData>()

            var coordinates: ArrayList<Double> = ArrayList()

            coordinates.add(lattitude)
            coordinates.add(longitude)
            val geomateryData: GeomateryData = GeomateryData(addressType, coordinates)

            var postalAddrArray: ArrayList<PostalAddressData> = ArrayList()

            val postalAddress: PostalAddressData = PostalAddressData(
                countryCode,
                stateDistrict,
                cityDistrict,
                addressRegion,
                streetAddress,
                postalCode,
                houseNumber
            )

            postalAddrArray.add(postalAddress)
            var properties: PropertiesData =
                PropertiesData("", categoryType, businessName, null, postalAddrArray, null, null)

            features.add(FeaturesData(addressType, geomateryData, properties))
            var reverseGeoCodeResponse: ReverseGeoCodeResponse =
                ReverseGeoCodeResponse(addressType, features)

            landmarkDataList.add(
                LandmarkDataList(
                    "",
                    "",
                    reverseGeoCodeResponse,
                    ArrayList<String>()
                )
            )

            binding!!.addLandmark.rvLandmark.adapter = LandMarkResultAdapter(landmarkDataList, this)
            binding!!.addLandmark.rvLandmark.adapter!!.notifyDataSetChanged()


            showLandmarkMarker(LatLng(lattitude, longitude), addressType)

//            clearAddressFields()
//            setAddressFromSearchResult(
//                houseNumber,
//                cityDistrict,
//                stateDistrict,
//                postalCode,
//                countryCode,
//                streetAddress
//            )
//
//
//
//
//            isChangeMarker = false
//            clearMap()
//            showMarker(LatLng(lattitude, longitude), "addressType")
//            changeCameraPosition(LatLng(lattitude, longitude),mapBoxMap)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    companion object
    {
        var landmarkActivity : LandmarkActivity? = null
    }

}