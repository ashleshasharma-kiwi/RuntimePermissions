package com.kiwitech.runtimepermission

import android.app.Application
import android.graphics.Color
import com.topfan.permissionhelper.ui.theme.PermissionHelperTheme

class AppDelegate: Application() {

    override fun onCreate() {
        super.onCreate()
        PermissionHelperTheme.initAppTheme(PermissionHelperTheme.ThemeProperties(Color.BLACK))

    }
}