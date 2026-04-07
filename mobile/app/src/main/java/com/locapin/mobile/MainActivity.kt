package com.locapin.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.locapin.mobile.core.designsystem.theme.LocaPinTheme
import com.locapin.mobile.feature.auth.FacebookAuthBridge
import com.locapin.mobile.feature.map.LocationPermissionUiState
import com.locapin.mobile.ui.AppNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var locationPermissionUiState by mutableStateOf(LocationPermissionUiState.UNKNOWN)
    private var hasRequestedLocationPermission by mutableStateOf(false)

    private val locationPermissionRequester =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasRequestedLocationPermission = true
            refreshLocationPermissionState()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        refreshLocationPermissionState()

        setContent {
            LocaPinTheme {
                AppNavHost(
                    locationPermissionUiState = locationPermissionUiState,
                    requestLocationPermission = {
                        locationPermissionRequester.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    openAppSettings = {
                        startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", packageName, null)
                            }
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshLocationPermissionState()
    }

    private fun refreshLocationPermissionState() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        locationPermissionUiState = when {
            hasPermission -> LocationPermissionUiState.GRANTED
            hasRequestedLocationPermission &&
                !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                LocationPermissionUiState.PERMANENTLY_DENIED
            }

            !hasRequestedLocationPermission -> LocationPermissionUiState.UNKNOWN
            else -> LocationPermissionUiState.DENIED
        }
    }

    @Deprecated("Uses legacy callback dispatch required by Facebook SDK")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val handled = FacebookAuthBridge.handleActivityResult(requestCode, resultCode, data)
        if (!handled) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
