package com.locapin.mobile.core.network

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
    fun provideAppDataMode(): AppDataMode =
        if (BuildConfig.USE_MOCK_DATA) AppDataMode.MOCK else AppDataMode.REMOTE
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        mode: AppDataMode,
        mockRepository: FakeAuthRepository,
        remoteRepository: RemoteAuthRepository
    ): AuthRepository = if (mode == AppDataMode.MOCK) mockRepository else remoteRepository

    @Provides
    @Singleton
    fun provideDestinationRepository(
        mode: AppDataMode,
        mockRepository: DestinationRepositoryImpl,
        remoteRepository: RemoteDestinationRepository
    ): DestinationRepository {
        val isRemoteTouristAttractionsReadEnabled =
            mode == AppDataMode.REMOTE || BuildConfig.ENABLE_REMOTE_TOURIST_ATTRACTIONS_READ
        return if (isRemoteTouristAttractionsReadEnabled) remoteRepository else mockRepository
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository = impl

    @Provides
    @Singleton
    fun provideProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository = impl

    @Provides
    @Singleton
    fun provideSegmentedMapRepository(impl: SegmentedMapRepositoryImpl): SegmentedMapRepository = impl

    @Provides
    @Singleton
    fun provideTouristFavoritesRepository(impl: TouristFavoritesRepositoryImpl): TouristFavoritesRepository = impl
}
