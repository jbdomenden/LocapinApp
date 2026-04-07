package com.locapin.mobile.core.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri

sealed interface DirectionsLaunchResult {
    data object Launched : DirectionsLaunchResult
    data object InvalidCoordinates : DirectionsLaunchResult
    data object NoNavigationApp : DirectionsLaunchResult
}

object DirectionsLauncher {
    fun launch(
        context: Context,
        latitude: Double,
        longitude: Double,
        destinationLabel: String?
    ): DirectionsLaunchResult {
        if (!latitude.isValidLatitude() || !longitude.isValidLongitude()) {
            return DirectionsLaunchResult.InvalidCoordinates
        }

        val encodedLabel = destinationLabel?.takeIf { it.isNotBlank() }?.let(Uri::encode)
        val googleNavigationUri = Uri.parse("google.navigation:q=$latitude,$longitude")
        val googleMapsIntent = Intent(Intent.ACTION_VIEW, googleNavigationUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (googleMapsIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(googleMapsIntent)
            return DirectionsLaunchResult.Launched
        }

        val geoUri = Uri.parse(
            if (encodedLabel == null) {
                "geo:0,0?q=$latitude,$longitude"
            } else {
                "geo:0,0?q=$latitude,$longitude($encodedLabel)"
            }
        )
        val fallbackIntent = Intent(Intent.ACTION_VIEW, geoUri)
        if (fallbackIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(Intent.createChooser(fallbackIntent, "Open directions with"))
            return DirectionsLaunchResult.Launched
        }

        return DirectionsLaunchResult.NoNavigationApp
    }
}

private fun Double.isValidLatitude(): Boolean = isFinite() && this in -90.0..90.0

private fun Double.isValidLongitude(): Boolean = isFinite() && this in -180.0..180.0
