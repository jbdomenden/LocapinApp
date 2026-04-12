package com.locapin.mobile.core.network

import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.locapin.mobile.BuildConfig
import com.locapin.mobile.core.datastore.UserPreferencesDataStore
import com.locapin.mobile.data.remote.AttractionApiService
import com.locapin.mobile.data.remote.AuthApiService
import com.locapin.mobile.data.remote.CategoryApiService
import com.locapin.mobile.data.remote.LocaPinApi
import com.locapin.mobile.data.remote.MapAreaApiService
import com.locapin.mobile.data.repository.FakeAuthRepository
import com.locapin.mobile.data.repository.DestinationRepositoryImpl
import com.locapin.mobile.data.repository.FirebaseAuthRepository
import com.locapin.mobile.data.repository.FirebaseHistoryRepository
import com.locapin.mobile.data.repository.FirebaseProfileRepository
import com.locapin.mobile.data.repository.FirebaseTouristFavoritesRepository
import com.locapin.mobile.data.repository.HistoryRepositoryImpl
import com.locapin.mobile.data.repository.ProfileRepositoryImpl
import com.locapin.mobile.data.repository.RemoteAuthRepository
import com.locapin.mobile.data.repository.RemoteDestinationRepository
import com.locapin.mobile.data.repository.SegmentedMapRepositoryImpl
import com.locapin.mobile.data.repository.TouristFavoritesRepositoryImpl
import com.locapin.mobile.domain.repository.AuthRepository
import com.locapin.mobile.domain.repository.DestinationRepository
import com.locapin.mobile.domain.repository.HistoryRepository
import com.locapin.mobile.domain.repository.ProfileRepository
import com.locapin.mobile.domain.repository.SegmentedMapRepository
import com.locapin.mobile.domain.repository.TouristFavoritesRepository
import com.locapin.mobile.feature.admin.AdminAttractionRepository
import com.locapin.mobile.feature.admin.FirebaseAdminAttractionRepository
import com.locapin.mobile.feature.admin.InMemoryAdminAttractionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttp(prefs: UserPreferencesDataStore): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = runBlocking { prefs.authToken.first() }
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrBlank()) addHeader("Authorization", "Bearer $token")
                }.build()
                chain.proceed(request)
            }
            .addInterceptor(logger)
            .build()
    }

    @Provides
    @Singleton
    fun provideApi(client: OkHttpClient, json: Json): LocaPinApi =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(LocaPinApi::class.java)

    @Provides
    @Singleton
    fun provideAuthApiService(client: OkHttpClient, json: Json): AuthApiService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideAttractionApiService(client: OkHttpClient, json: Json): AttractionApiService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(AttractionApiService::class.java)

    @Provides
    @Singleton
    fun provideCategoryApiService(client: OkHttpClient, json: Json): CategoryApiService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(CategoryApiService::class.java)

    @Provides
    @Singleton
    fun provideMapAreaApiService(client: OkHttpClient, json: Json): MapAreaApiService =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MapAreaApiService::class.java)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): com.google.firebase.auth.FirebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): com.google.firebase.firestore.FirebaseFirestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): com.google.firebase.storage.FirebaseStorage = com.google.firebase.storage.FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideAppDataMode(): AppDataMode = AppDataMode.FIREBASE
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        mode: AppDataMode,
        mockRepository: FakeAuthRepository,
        remoteRepository: RemoteAuthRepository,
        firebaseRepository: FirebaseAuthRepository
    ): AuthRepository = when (mode) {
        AppDataMode.MOCK -> mockRepository
        AppDataMode.REMOTE -> remoteRepository
        AppDataMode.FIREBASE -> firebaseRepository
    }

    @Provides
    @Singleton
    fun provideDestinationRepository(
        mode: AppDataMode,
        mockRepository: DestinationRepositoryImpl,
        remoteRepository: RemoteDestinationRepository
    ): DestinationRepository = when (mode) {
        AppDataMode.MOCK -> mockRepository
        AppDataMode.REMOTE -> remoteRepository
        AppDataMode.FIREBASE -> mockRepository // Currently DestinationRepositoryImpl uses AdminAttractionRepository
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(
        mode: AppDataMode,
        localRepository: HistoryRepositoryImpl,
        firebaseRepository: FirebaseHistoryRepository
    ): HistoryRepository = when (mode) {
        AppDataMode.FIREBASE -> firebaseRepository
        else -> localRepository
    }

    @Provides
    @Singleton
    fun provideProfileRepository(
        mode: AppDataMode,
        localRepository: ProfileRepositoryImpl,
        firebaseRepository: FirebaseProfileRepository
    ): ProfileRepository = when (mode) {
        AppDataMode.FIREBASE -> firebaseRepository
        else -> localRepository
    }

    @Provides
    @Singleton
    fun provideSegmentedMapRepository(impl: SegmentedMapRepositoryImpl): SegmentedMapRepository = impl

    @Provides
    @Singleton
    fun provideTouristFavoritesRepository(
        mode: AppDataMode,
        localRepository: TouristFavoritesRepositoryImpl,
        firebaseRepository: FirebaseTouristFavoritesRepository
    ): TouristFavoritesRepository = when (mode) {
        AppDataMode.FIREBASE -> firebaseRepository
        else -> localRepository
    }
}
