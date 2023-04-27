package com.unl.addressvalidator.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.unl.addressvalidator.R
import com.unl.map.sdk.UnlMap
import com.unl.map.sdk.data.EnvironmentType

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UnlMap(applicationContext,getString(R.string.api_key),
            getString(R.string.vpm_id), EnvironmentType.SANDBOX)
        setContentView(R.layout.activity_home)
    }
}