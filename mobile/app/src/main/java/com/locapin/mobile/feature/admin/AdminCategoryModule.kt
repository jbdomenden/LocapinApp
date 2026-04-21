package com.locapin.mobile.feature.admin

import com.locapin.mobile.core.network.AppDataMode
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminCategoryModule {
    @Provides
    @Singleton
    fun bindAdminCategoryRepository(
        mode: AppDataMode,
        mockRepository: InMemoryAdminCategoryRepository,
        firebaseRepository: FirebaseAdminCategoryRepository
    ): AdminCategoryRepository = if (mode == AppDataMode.MOCK) mockRepository else firebaseRepository
}
