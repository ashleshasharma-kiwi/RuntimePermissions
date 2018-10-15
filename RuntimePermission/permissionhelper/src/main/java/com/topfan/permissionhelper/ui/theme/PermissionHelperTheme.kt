package com.topfan.permissionhelper.ui.theme

class PermissionHelperTheme private constructor(val themeProperties: ThemeProperties) {

    companion object {
        private var appTheme: PermissionHelperTheme? = null

        fun initAppTheme(themeProperties: ThemeProperties):PermissionHelperTheme{
            if(appTheme ==null){
                appTheme = PermissionHelperTheme(themeProperties)
            }
            return appTheme as PermissionHelperTheme
        }

        fun getInstance(): PermissionHelperTheme? {
            return appTheme
        }

    }

    class ThemeProperties(val appTheme: Int)

}