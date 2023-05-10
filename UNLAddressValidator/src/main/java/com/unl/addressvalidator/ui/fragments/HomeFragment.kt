package com.unl.addressvalidator.ui.fragments

import android.app.Dialog
import android.app.Fragment
import android.content.res.ColorStateList
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.mapboxsdk.geometry.LatLng
import com.unl.addressvalidator.R
import com.unl.addressvalidator.databinding.FragmentHomeBinding
import com.unl.addressvalidator.model.AutocompleteData
import com.unl.addressvalidator.network.ApiCallBack
import com.unl.addressvalidator.ui.adapters.SearchResultAdapter
import com.unl.addressvalidator.ui.interfaces.SearchItemClickListner
import com.unl.addressvalidator.ui.viewmodel.HomeViewModel
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.EnvironmentType
import com.unl.map.sdk.helpers.grid_controls.setGridControls
import com.unl.map.sdk.helpers.tile_controls.enableTileSelector
import com.unl.map.sdk.helpers.tile_controls.setTileSelectorGravity
import com.unl.map.sdk.views.UnlMapView


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), SearchItemClickListner {
    // TODO: Rename and change types of parameters
    private var apiKey: String? = null
    private var vpmId: String? = null
    lateinit var viewModel: HomeViewModel
    lateinit var lifecycleOwner: LifecycleOwner
    lateinit var viewModelStoreOwner: ViewModelStoreOwner
    lateinit var supportFragmentManager: FragmentManager
    lateinit var unlMap : UnlMapView
    var dialog: Dialog? = null
    private var binding : FragmentHomeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UnlMap(context, apiKey!!, vpmId!!, EnvironmentType.SANDBOX)
        initiateViewModel()
        getAutocompleteResponse()

        //  setDialog()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding!!.mapView.getMapAsync {
            binding!!.mapView.fm = supportFragmentManager
            binding!!.mapView.activity = activity
            binding!!.mapView.lifeCycleOwner = viewModelStoreOwner
            binding!!.mapView.enableIndoorMap = false
            binding!!.mapView.viewLifecycle = lifecycleOwner
            binding!!.mapView.enableTileSelector(false)
            binding!!.mapView.setGridControls(activity, false)
            binding!!.mapView.setTileSelectorGravity(Gravity.END)
        }


        binding!!.addNewAdd.edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s!!.toString().length > 1 /*&& fromSelection == false*/) {
                       // binding!!.progressBar.visibility = View.VISIBLE

                        viewModel.getAutocompleteData(s.toString())
                    } else{
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

        binding!!.addNewAdd.rlHomeIcon.setOnClickListener(View.OnClickListener { selectedLabel(0) })
        binding!!.addNewAdd.rlOfficeIcon.setOnClickListener(View.OnClickListener { selectedLabel(1) })
        binding!!.addNewAdd.rlAddLableIcon.setOnClickListener(View.OnClickListener { selectedLabel(2) })


        setSearchListView()
        selectedLabel(0)

        // Inflate the layout for this fragment
        return binding!!.root
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

                    binding!!.addNewAdd.rvSearchResult.adapter =
                        SearchResultAdapter(response.data!!, this)
                    binding!!.addNewAdd.rvSearchResult.visibility = View.VISIBLE
                 //   Toast.makeText(context,""+response.data!!.size,Toast.LENGTH_SHORT).show()
                }

                is ApiCallBack.Error ->{

                }

                is ApiCallBack.Loading ->{

                }

            }
        })

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
        binding!!.addNewAdd!!.ivAddLabel.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.black2)))
        binding!!.addNewAdd!!.tvAddLabel.setTextColor(resources.getColor(R.color.black2))
        binding!!.addNewAdd!!.tvOffice.setTextColor(resources.getColor(R.color.black2))
        binding!!.addNewAdd!!.tvHome.setTextColor(resources.getColor(R.color.black2))
        binding!!.addNewAdd!!.ivCheckAddLabel.setImageResource(R.drawable.labelunchecked)
        binding!!.addNewAdd!!.ivCheckOffice.setImageResource(R.drawable.labelunchecked)
        binding!!.addNewAdd!!.ivCheckHome.setImageResource(R.drawable.labelunchecked)
        if (position == 0) {
            binding!!.addNewAdd!!.rlHomeIcon.setBackgroundResource(R.drawable.label_selected_shape)
            binding!!.addNewAdd!!.ivHome.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.purple1)))
            binding!!.addNewAdd!!.tvHome.setTextColor(resources.getColor(R.color.purple1))
            binding!!.addNewAdd!!.ivCheckHome.setImageResource(R.drawable.labelcheck)
        } else if (position == 1) {
            binding!!.addNewAdd!!.rlOfficeIcon.setBackgroundResource(R.drawable.label_selected_shape)
            binding!!.addNewAdd!!.ivOffice.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.purple1)))
            binding!!.addNewAdd!!.tvOffice.setTextColor(resources.getColor(R.color.purple1))
            binding!!.addNewAdd!!.ivCheckOffice.setImageResource(R.drawable.labelcheck)
        } else if (position == 2) {
            binding!!.addNewAdd!!.rlAddLableIcon.setBackgroundResource(R.drawable.label_selected_shape)
            binding!!.addNewAdd!!.ivAddLabel.setImageTintList(ColorStateList.valueOf(resources.getColor(R.color.purple1)))
            binding!!.addNewAdd!!.tvAddLabel.setTextColor(resources.getColor(R.color.purple1))
            binding!!.addNewAdd!!.ivCheckAddLabel.setImageResource(R.drawable.labelcheck)
        }
    }


    private fun getMetadataFromMenifest()
    {
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
      //  TODO("Not yet implemented")
    }
}