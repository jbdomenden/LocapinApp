package com.locapin.mobile.feature.admin

data class AdminMapArea(
    val id: String,
    val name: String,
    val description: String,
    val districtLabel: String,
    val centerLatitude: Double,
    val centerLongitude: Double,
    val polygonPoints: String = "",
    val hexColor: String = "#F0F4A4",
    val isPremium: Boolean = false
)
