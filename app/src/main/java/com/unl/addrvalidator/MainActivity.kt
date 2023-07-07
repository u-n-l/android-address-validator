package com.unl.addrvalidator



import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.unl.addressvalidator.ui.fragments.HomeFragment
import com.unl.addressvalidator.ui.imagepicker.builder.MultiImagePicker.Companion.REQUEST_PICK_MULTI_IMAGES


class MainActivity : AppCompatActivity() {

    var fragment: HomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addFragment()
    }

    private fun addFragment()
    {
        try {
            val ft: FragmentTransaction = getFragmentManager().beginTransaction()
             fragment = HomeFragment.newInstance(getString(R.string.api_key),getString(R.string.vpm_id),this,this,supportFragmentManager)
            ft.replace(R.id.fragmentHolder, fragment)
            ft.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode.equals(REQUEST_PICK_MULTI_IMAGES))
        {
            fragment!!.onActivityResult(requestCode, resultCode, data)
        }
    }


}