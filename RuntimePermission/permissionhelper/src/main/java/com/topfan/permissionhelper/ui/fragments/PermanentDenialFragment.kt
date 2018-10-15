package com.topfan.permissionhelper.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.topfan.permissionhelper.R
import com.topfan.permissionhelper.helper.PermissionHelper
import com.topfan.permissionhelper.ui.theme.PermissionHelperTheme


internal class PermanentDenialFragment : DialogFragment() {

    private var neededPermission: Array<String>? = null
    private var popupText: TextView? = null

    companion object {
        private const val KEY_PERMISSION: String = "requiredPermissions"

        /**
         * Use this method to instantiate the class & to avoid runtime crashes.
         * @param requiredPermissions: array of permission to be requested to user.
         * @returns object of this class having requested permissions as arguments.
         */

        fun getInstance(requiredPermissions: Array<String>): PermanentDenialFragment {
            val permanentDenialFragment = PermanentDenialFragment()
            val bundle = Bundle()
            bundle.putStringArray(KEY_PERMISSION, requiredPermissions)
            permanentDenialFragment.arguments = bundle
            return permanentDenialFragment
        }
    }

    /**
     * This method is called by fragment lifecycle flow & used to inflate & initialize all the views.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_layout_permission_denial, container, false)
        neededPermission = arguments?.getStringArray(KEY_PERMISSION)
        iniViews(view)
        setExplainationalMessage()
        return view
    }


    /**
     *The method is used to initialize the basic views of this fragment.
     * @param view: parentView
     */
    private fun iniViews(view: View?) {
        popupText = view?.findViewById(R.id.tv_permission_text)
        view?.findViewById<TextView>(R.id.btn_app_setting)?.setOnClickListener {
            PermissionHelper.goToSettings(activity)
        }

        view?.findViewById<TextView>(R.id.btn_not_now)?.setOnClickListener {
            dismissAllowingStateLoss()
        }

        setAppTheme(view)
    }

    /**
     * This method helps in setting app theme on this popup.
     * PermissionHelperTheme should be initialized by parent module before using this feature by calling its PermissionHelperTheme.initAppTheme() on app launch .
     */
    private fun setAppTheme(view: View?) {
        val appTheme = PermissionHelperTheme.getInstance()
        if (appTheme?.themeProperties == null) {
            return
        }
        val appThemeColor = appTheme.themeProperties.appTheme
        view?.findViewById<TextView>(R.id.btn_app_setting)?.setBackgroundColor(appThemeColor)
        view?.findViewById<TextView>(R.id.tv_permission_title)?.setTextColor(appThemeColor)
    }

    /**This method id used to set a explanatory message in case of permanent denial of one or more permissions.
     * The message is constructed in two parts
     * a. Explanatory message passed by calling module as manifest metadata per permission.
     *    Keys: group_storage, group_location or group_camera. Dev can add extra key for other groups if needed.
     * b. Go TO Setting message, set by this module.
     * note: Dev can reset message b to other message or skip it by setting empty string on @string/permission_denial_text. Editing this message would change this message for other app part as well.
     */
    private fun setExplainationalMessage() {
        val applicationInfo = activity?.packageManager?.getApplicationInfo(activity?.packageName, PackageManager.GET_META_DATA)
        val bundle = applicationInfo?.metaData
        var text = ""
        val appName: String? = activity?.packageManager?.getApplicationLabel(applicationInfo) as String?
        for (i in neededPermission?.indices!!) {
            when (neededPermission?.get(i)) {
                Manifest.permission.WRITE_EXTERNAL_STORAGE -> text += formatString(bundle?.getString("group_storage"), appName)
                Manifest.permission.ACCESS_COARSE_LOCATION -> text += formatString(bundle?.getString("group_location"), appName)
                Manifest.permission.ACCESS_FINE_LOCATION -> text += formatString(bundle?.getString("group_location"), appName)
                Manifest.permission.CAMERA -> text += formatString(bundle?.getString("group_camera"), appName)
            }
        }
        text += "\n"+getString(R.string.permission_denial_text)
        popupText?.text = text
    }

    private fun formatString(message: String?, value: String?): Any? {
        if (message == null) {
            return message
        }
        return String.format(message, value)
    }

}
