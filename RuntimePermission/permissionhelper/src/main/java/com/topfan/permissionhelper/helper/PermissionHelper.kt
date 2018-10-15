package com.topfan.permissionhelper.helper

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.FragmentActivity
import com.topfan.permissionhelper.ui.fragments.PermissionFragment

class PermissionHelper {

    companion object {
        private val TAG = PermissionHelper.javaClass.name

        /**
         * This method can be called to check if requested permissions are already granted or not.
         * @param context: context of the calling activity
         * @param permissions: permissions to check in the format "Manifest.permission.<PERMISSION>"
         * @return true is all the requested permission are already granted for this app otherwise false.
         */
        fun isPermissionAvailable(context: FragmentActivity, permissions: Array<String>): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionFragment.Companion.needPermission(context, permissions).isEmpty()
            } else {
                true
            }
        }

        /**
         * This method can be called to request permissions at run time. The method would show a prompt to user if requested permission are not already granted.
         * @param context: context of the calling activity
         * @param permissions: permissions to check in the format "Manifest.permission.<PERMISSION>"
         * @param callback: A listener who want to listen callback on success.
         */
        fun askPermission(context: FragmentActivity, permissions: Array<String>, callback: Callback) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val permissionNeeded = PermissionFragment.Companion.needPermission(context, permissions)
                if (permissionNeeded.isNotEmpty()) {
                    val fragment = PermissionFragment.getInstance(permissionNeeded)
                    fragment.setCallbackListener(callback)
                    context.supportFragmentManager.beginTransaction().add(fragment, TAG).commitNowAllowingStateLoss()
                }
            }
        }

        /**
         * This method will take the user device setting screen, generally would be used in permanent denial case in this module.
         * @param activity : reference of calling activity
         */
        fun goToSettings(activity: FragmentActivity?) {
            activity?.startActivity(Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", activity.packageName, null)))
        }
    }

    interface Callback {
        /**
         * This callback would be called on caller only if all the requested permissions are allowed by user.
         */
        fun onPermissionGranted()
    }

}
