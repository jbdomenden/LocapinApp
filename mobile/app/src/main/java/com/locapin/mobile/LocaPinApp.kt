package com.locapin.mobile

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LocaPinApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Ensure Facebook SDK is initialized only if valid credentials are provided
        val appId = getString(R.string.facebook_app_id)
        val clientToken = getString(R.string.facebook_client_token)
        
        if (appId != "0" && appId.isNotBlank() && clientToken.isNotBlank()) {
            FacebookSdk.setApplicationId(appId)
            FacebookSdk.setClientToken(clientToken)
            FacebookSdk.sdkInitialize(this)
            AppEventsLogger.activateApp(this)
        }
    }
}
