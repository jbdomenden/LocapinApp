package com.locapin.mobile.core.navigation

object AppDestinations {
    const val SessionCheck = "session_check"
    const val Auth = "auth/login"
    const val ForgotPassword = "auth/forgot_password"
    const val SignUp = "auth/sign_up"
    const val Eula = "auth/eula"
    const val TermsConditions = "auth/terms_conditions"
    const val PrivacyLocationConsent = "auth/privacy_location_consent"

    const val AdminEntry = "admin/dashboard"
    const val AdminAttractions = "admin/attractions"
    const val AdminAttractionCreate = "admin/attractions/create"
    const val AdminAttractionEdit = "admin/attractions/{attractionId}"
    const val AdminCategories = "admin/categories"
    const val AdminCategoryCreate = "admin/categories/create"
    const val AdminCategoryEdit = "admin/categories/{categoryId}"
    const val AdminMapAreas = "admin/map_areas"
    const val AdminMapAreaCreate = "admin/map_areas/create"
    const val AdminMapAreaEdit = "admin/map_areas/{mapAreaId}"
    const val AdminReports = "admin/reports"
    const val AdminProfile = "admin/profile"
    const val AdminSettings = "admin/settings"
    const val AdminChangePassword = "admin/change_password"

    const val TouristEntry = "tourist/entry"

    const val TouristDashboard = "tourist/dashboard"
    const val TouristMap = "tourist/map"
    const val TouristAttractions = "tourist/attractions"
    const val TouristFavorites = "tourist/favorites"
    const val TouristProfile = "tourist/profile"
    const val TouristAbout = "tourist/about"
    const val TouristSettings = "tourist/settings"
    const val TouristChangePassword = "tourist/change_password"

    fun adminAttractionEdit(attractionId: String): String = "admin/attractions/$attractionId"
    fun adminCategoryEdit(categoryId: String): String = "admin/categories/$categoryId"
    fun adminMapAreaEdit(mapAreaId: String): String = "admin/map_areas/$mapAreaId"
}
