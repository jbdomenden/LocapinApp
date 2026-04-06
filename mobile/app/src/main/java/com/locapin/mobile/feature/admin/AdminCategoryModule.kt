package com.locapin.mobile.feature.admin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminCategoryModule {
    @Binds
    @Singleton
    abstract fun bindAdminCategoryRepository(
        impl: InMemoryAdminCategoryRepository
    ): AdminCategoryRepository
}
