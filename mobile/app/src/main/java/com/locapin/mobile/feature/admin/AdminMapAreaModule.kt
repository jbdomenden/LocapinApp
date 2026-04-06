package com.locapin.mobile.feature.admin

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminMapAreaModule {
    @Binds
    @Singleton
    abstract fun bindAdminMapAreaRepository(
        impl: InMemoryAdminMapAreaRepository
    ): AdminMapAreaRepository
}
