package com.kiwitech.runtimepermission

import android.Manifest
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.topfan.permissionhelper.helper.PermissionHelper

class MainActivity : AppCompatActivity() {

    fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askForLocationPermission();
    }

    private fun askForLocationPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (PermissionHelper.isPermissionAvailable(this, permissions)) {
            return
        }

        PermissionHelper.askPermission(this, permissions, object : PermissionHelper.Callback {
            override fun onPermissionGranted() {
                toast(getString(R.string.text_location_permission_granted))
            }
        })
    }

}
