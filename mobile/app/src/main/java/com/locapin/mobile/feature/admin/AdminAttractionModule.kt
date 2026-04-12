package com.locapin.mobile.feature.admin

import com.locapin.mobile.core.network.AppDataMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminAttractionModule {
    @Provides
    @Singleton
    fun bindAdminAttractionRepository(
        mode: AppDataMode,
        mockRepository: InMemoryAdminAttractionRepository,
        remoteRepository: RemoteAdminAttractionRepository,
        firebaseRepository: FirebaseAdminAttractionRepository
    ): AdminAttractionRepository = when (mode) {
        AppDataMode.MOCK -> mockRepository
        AppDataMode.REMOTE -> remoteRepository
        AppDataMode.FIREBASE -> firebaseRepository
    }
}
