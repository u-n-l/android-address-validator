package com.unl.addressvalidator.ui.fragments

import android.app.Activity
import android.app.Fragment
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.mapbox.mapboxsdk.maps.SupportMapFragment

import com.unl.addressvalidator.R
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.fragments.viewmodel.HomeViewModel
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.EnvironmentType
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import com.unl.map.sdk.networks.UnlViewModel
import com.unl.map.sdk.prefs.DataManager
import com.unl.map.sdk.views.UnlMapView

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var apiKey: String? = null
    private var vpmId: String? = null
    lateinit var viewModel: HomeViewModel
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var viewModelStoreOwner: ViewModelStoreOwner
    lateinit var supportFragmentManager: FragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        UnlMap(
            context, apiKey!!,
            vpmId!!, EnvironmentType.SANDBOX
        )


        /* arguments?.let {
             param1 = it.getString(ARG_PARAM1)
             param2 = it.getString(ARG_PARAM2)
         }*/

        val info: ApplicationInfo = context!!.getPackageManager().getApplicationInfo(
            activity.packageName,
            PackageManager.GET_META_DATA
        )
        val bundle = info.metaData
        val appId = bundle.getInt("com.unl.global.vpmid")
      //  Toast.makeText(context, "" + appId, Toast.LENGTH_LONG).show()
        Toast.makeText(context, "" + DataManager.getApiKey(), Toast.LENGTH_LONG).show()
        initiateViewModel()
        getAutocompleteResponse()
        viewModel.getAutocompleteData("Indo")
    }

    private fun initiateViewModel()
    {
        viewModel = ViewModelProvider(viewModelStoreOwner!!)[HomeViewModel::class.java]
    }

    private fun getAutocompleteResponse()
    {
        viewModel.autoCompleteData.observe(lifecycleOwner,{response->
            when(response)
            {
                is ApiCallBack.Success ->{
                    response.data
                    Toast.makeText(context,""+response.data!!.size,Toast.LENGTH_SHORT).show()
                }

                is ApiCallBack.Error ->{

                }

                is ApiCallBack.Loading ->{

                }

            }
        })

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_blank, container, false)

        var unlMap = view.findViewById<UnlMapView>(R.id.mapView)
        unlMap.getMapAsync {
            unlMap.fm = supportFragmentManager
            unlMap.activity = activity
            unlMap.lifeCycleOwner = viewModelStoreOwner
            unlMap.enableIndoorMap = true
            unlMap.viewLifecycle = lifecycleOwner
            unlMap.enableTileSelector(true)
            unlMap.setGridControls(activity, true)
            unlMap.setTileSelectorGravity(Gravity.END)
        }

        // Inflate the layout for this fragment
        return view
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
}