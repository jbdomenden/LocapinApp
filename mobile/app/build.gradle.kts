plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.locapin.mobile"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.locapin.mobile"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }

        val apiBaseUrl = (
            project.findProperty("LOCAPIN_API_BASE_URL") as? String
                ?: System.getenv("LOCAPIN_API_BASE_URL")
                ?: "https://example.com/"
            ).ensureTrailingSlash()
        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")

        val useMockData = (
            project.findProperty("LOCAPIN_USE_MOCK_DATA") as? String
                ?: System.getenv("LOCAPIN_USE_MOCK_DATA")
                ?: "true"
            ).toBoolean()
        buildConfigField("boolean", "USE_MOCK_DATA", useMockData.toString())

        val enableRemoteTouristAttractionsRead = (
            project.findProperty("LOCAPIN_ENABLE_REMOTE_TOURIST_ATTRACTIONS_READ") as? String
                ?: System.getenv("LOCAPIN_ENABLE_REMOTE_TOURIST_ATTRACTIONS_READ")
                ?: "false"
            ).toBoolean()
        buildConfigField(
            "boolean",
            "ENABLE_REMOTE_TOURIST_ATTRACTIONS_READ",
            enableRemoteTouristAttractionsRead.toString()
        )

        val mapsKey = (project.findProperty("MAPS_API_KEY") as? String) ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsKey

        val googleServerClientId = (project.findProperty("GOOGLE_SERVER_CLIENT_ID") as? String)
            ?: System.getenv("GOOGLE_SERVER_CLIENT_ID")
            ?: ""
        buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID", "\"$googleServerClientId\"")

        val facebookAppId = (project.findProperty("FACEBOOK_APP_ID") as? String)
            ?: System.getenv("FACEBOOK_APP_ID")
            ?: ""
        val facebookClientToken = (project.findProperty("FACEBOOK_CLIENT_TOKEN") as? String)
            ?: System.getenv("FACEBOOK_CLIENT_TOKEN")
            ?: ""
        manifestPlaceholders["FACEBOOK_APP_ID"] = facebookAppId
        manifestPlaceholders["FACEBOOK_CLIENT_TOKEN"] = facebookClientToken
        resValue("string", "facebook_app_id", facebookAppId.ifBlank { "0" })
        resValue("string", "facebook_client_token", facebookClientToken)
        resValue("string", "fb_login_protocol_scheme", "fb${facebookAppId.ifBlank { "0" }}")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
        resValues = true
    }
    packaging { resources.excludes += "/META-INF/{AL2.0,LGPL2.1}" }
}

fun String.ensureTrailingSlash(): String = if (endsWith("/")) this else "$this/"

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.material)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    implementation(libs.androidx.datastore.preferences)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    implementation(libs.play.services.location)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.auth)
    implementation(libs.facebook.login)

    implementation(libs.androidx.core.splashscreen)

    implementation(libs.firebase.bom)
    implementation(libs.google.firebase.analytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
