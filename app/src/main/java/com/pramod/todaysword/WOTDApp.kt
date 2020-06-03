package com.pramod.todaysword

import android.app.Application
import android.content.Context
import com.facebook.ads.AdSettings
import com.facebook.ads.AudienceNetworkAds
import com.pramod.todaysword.helper.PrefManager
import com.pramod.todaysword.helper.ThemeManager
import com.pramod.todaysword.util.CustomExceptionHandler

class WOTDApp : Application() {

    override fun onCreate() {
        super.onCreate()

        ThemeManager.applyTheme(ThemeManager.getDefaultThemeOption())

        if (Thread.getDefaultUncaughtExceptionHandler() !is CustomExceptionHandler) {
            Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler(this));
        }

        if (AudienceNetworkAds.isInAdsProcess(this)) {
            return;
        }

        AdSettings.addTestDevice("5a37c734-2349-4af8-a1ae-151b335e3243");
        AudienceNetworkAds.initialize(this);
    }

    companion object {
        @JvmStatic
        fun clearAppData(context: Context) {
            try {
                val packageName: String = context.packageName
                val runtime = Runtime.getRuntime()
                runtime.exec("pm clear $packageName")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}