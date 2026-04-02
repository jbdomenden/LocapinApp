package com.locapin.mobile.feature.auth

import android.content.Intent
import com.facebook.CallbackManager

object FacebookAuthBridge {
    private var callbackManager: CallbackManager? = null

    fun setCallbackManager(manager: CallbackManager) {
        callbackManager = manager
    }

    fun clearCallbackManager(manager: CallbackManager) {
        if (callbackManager === manager) callbackManager = null
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return callbackManager?.onActivityResult(requestCode, resultCode, data) ?: false
    }
}
