package com.locapin.mobile.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class AdminAttractionRequest(
    val name: String,
    val knownFor: String,
    val description: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val area: String,
    val isVisible: Boolean
)

@Serializable
data class AdminCategoryRequest(
    val name: String,
    val description: String
)

@Serializable
data class AdminMapAreaRequest(
    val name: String,
    val description: String,
    val districtLabel: String,
    val centerLatitude: Double,
    val centerLongitude: Double
)
