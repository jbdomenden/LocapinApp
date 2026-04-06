package com.locapin.mobile.app

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import dagger.hilt.android.HiltAndroidApp
import com.locapin.mobile.R

@HiltAndroidApp
class LocaPinApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Ensure Facebook SDK is initialized
        val appId = getString(R.string.facebook_app_id)
        if (appId != "0" && appId.isNotBlank()) {
            FacebookSdk.setApplicationId(appId)
            val clientToken = getString(R.string.facebook_client_token)
            if (clientToken.isNotBlank()) {
                FacebookSdk.setClientToken(clientToken)
            }
        }
        
        // Always call sdkInitialize to prevent "The SDK has not been initialized" crashes 
        // when LoginManager.getInstance() is called, even if the App ID is not yet configured.
        FacebookSdk.sdkInitialize(this)

        if (appId != "0" && appId.isNotBlank()) {
            AppEventsLogger.activateApp(this)
        }
    }
}
