package com.unl.addrvalidator



import android.app.FragmentTransaction
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.unl.addressvalidator.data.EnvironmentType
import com.unl.addressvalidator.ui.fragments.HomeFragment
import com.unl.addressvalidator.ui.homescreen.UnlValidatorActivity
import com.unl.addressvalidator.ui.imagepicker.builder.MultiImagePicker.Companion.REQUEST_PICK_MULTI_IMAGES


class MainActivity : AppCompatActivity() {

    var fragment: HomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initValidator()
       // addFragment()
    }

    private fun initValidator()
    {
        try {
            UnlValidatorActivity.envType =  EnvironmentType.SANDBOX
            val intent = Intent(this, UnlValidatorActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun addFragment()
    {
        try {
            val ft: FragmentTransaction = getFragmentManager().beginTransaction()
             fragment = HomeFragment.newInstance(this,supportFragmentManager)
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