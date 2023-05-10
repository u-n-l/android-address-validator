package com.unl.addrvalidator


import android.app.Activity
import android.app.FragmentTransaction
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.unl.addressvalidator.ui.fragments.HomeFragment

class MainActivity : AppCompatActivity() {
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
            val fragment: HomeFragment =
                HomeFragment.newInstance(getString(R.string.api_key),getString(R.string.vpm_id),this,this,supportFragmentManager)
            ft.replace(R.id.fragmentHolder, fragment)
            ft.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}