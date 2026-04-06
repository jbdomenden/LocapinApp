package com.locapin.mobile.feature.admin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminAttractionModule {
    @Binds
    @Singleton
    abstract fun bindAdminAttractionRepository(
        impl: InMemoryAdminAttractionRepository
    ): AdminAttractionRepository
}
