package com.locapin.mobile

import android.app.Application
import com.locapin.mobile.data.local.FirebasePopulator
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class LocaPinApp : Application() {
    @Inject
    lateinit var populator: FirebasePopulator

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            // Populate Firebase with initial data
            populator.populate()
        }
    }
}
