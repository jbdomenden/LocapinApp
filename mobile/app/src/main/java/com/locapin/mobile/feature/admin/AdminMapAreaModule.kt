package com.locapin.mobile.feature.admin

import com.locapin.mobile.core.network.AppDataMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminMapAreaModule {
    @Provides
    @Singleton
    fun bindAdminMapAreaRepository(
        mode: AppDataMode,
        mockRepository: InMemoryAdminMapAreaRepository,
        remoteRepository: RemoteAdminMapAreaRepository
    ): AdminMapAreaRepository = if (mode == AppDataMode.MOCK) mockRepository else remoteRepository
}
