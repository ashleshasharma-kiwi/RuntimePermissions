package com.topfan.permissionhelper.ui.fragments

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import com.topfan.permissionhelper.helper.PermissionHelper
import java.lang.ref.WeakReference
import java.util.*

internal class PermissionFragment : Fragment() {


    private var callback: WeakReference<PermissionHelper.Callback>? = null
    private var neededPermissions: Array<String>? = null

    init {
        retainInstance = true
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION: Int = 100
        private const val KEY_PERMISSION: String = "key_permission"

        /**This method receive an array of permission & return permissions which require user attention.
         *@param activity: reference of calling activity
         * @param askedPermissions: array of permissions to check
         * @return array of permissions that needs user attention.
         */
        fun needPermission(activity: Activity, askedPermissions: Array<String>): Array<String> {
            val packageManager = activity.packageManager
            val packageName = activity.packageName
            val requiredPermission = ArrayList<String>()
            askedPermissions.forEach {
                if (packageManager.checkPermission(it, packageName) != PackageManager.PERMISSION_GRANTED) {
                    requiredPermission.add(it)
                }
            }
            return requiredPermission.toTypedArray()
        }

        fun getInstance(requiredPermission: Array<String>): PermissionFragment {
            val permissionFragment = PermissionFragment()
            val bundle = Bundle()
            bundle.putStringArray(KEY_PERMISSION, requiredPermission)
            permissionFragment.arguments = bundle
            return permissionFragment
        }
    }

    /**
     * Set a callback listener to listen if all the requested permissiond are granted by user.
     * @param callback: callback listener
     */
    fun setCallbackListener(callback: PermissionHelper.Callback) {
        this.callback = WeakReference(callback)
    }

    override fun onResume() {
        super.onResume()
        if (neededPermissions == null) {
            extractArguments()
        }
        if (neededPermissions!!.isNotEmpty()) {
            requestPermissions(neededPermissions!!, REQUEST_CODE_PERMISSION)
        } else {
            fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()
        }
    }

    /**
     * This method extracts arguments attached to this fragment
     */
    private fun extractArguments() {
        neededPermissions = arguments!!.getStringArray(KEY_PERMISSION)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSION && permissions.isNotEmpty()) {
            val callback = this.callback!!.get()
            if (callback != null) {
                val deniedPermissions = ArrayList<String>()
                var denied = false

                for (i in permissions.indices) {
                    val permission = permissions[i]
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        if (!shouldShowRequestPermissionRationale(permission)) {
                            deniedPermissions.add(permission)
                        }
                        denied = true
                    }
                }
                if (!denied) {
                    callback.onPermissionGranted()
                } else if (deniedPermissions.isNotEmpty()) {
                    launchPermanentDenialScreen(deniedPermissions)
                }
            }

            fragmentManager!!.beginTransaction().remove(this).commitAllowingStateLoss()

        }
    }

    /**
     * This method will launch a separate popup screen having explanation per permission why this app need such permissions.
     * @param deniedPermissions: array of permissions that require explanation.
     */

    private fun launchPermanentDenialScreen(deniedPermissions: ArrayList<String>) {
        val fm = activity!!.supportFragmentManager
        val alertDialog = PermanentDenialFragment.getInstance(deniedPermissions.toTypedArray())
        alertDialog.show(fm, PermanentDenialFragment.javaClass.name)
    }

}
