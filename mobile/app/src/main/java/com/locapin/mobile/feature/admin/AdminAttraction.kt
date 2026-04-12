package com.locapin.mobile.feature.admin

data class AdminAttraction(
    val id: String,
    val name: String,
    val knownFor: String,
    val description: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val area: String,
    val isVisible: Boolean,
    val imageUrl: String? = null,
    val distance: String? = null
)
