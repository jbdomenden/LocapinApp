package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.local.InMemoryCache
import com.locapin.mobile.data.remote.LocaPinApi
import com.locapin.mobile.domain.model.Category
import com.locapin.mobile.domain.model.Destination
import com.locapin.mobile.domain.model.User
import com.locapin.mobile.domain.repository.DestinationRepository
import com.locapin.mobile.domain.repository.ProfileRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DestinationRepositoryImpl @Inject constructor(
    private val api: LocaPinApi,
    private val cache: InMemoryCache
) : DestinationRepository {
    override suspend fun getDestinations(
        query: String?,
        categoryId: String?,
        sort: String?,
        page: Int,
        lat: Double?,
        lng: Double?
    ): LocaPinResult<List<Destination>> = runCatching {
        api.destinations(query, categoryId, sort, page = page, lat = lat, lng = lng).data
            ?.map { it.toDomain() }
            ?: emptyList()
    }.fold(
        onSuccess = {
            cache.lastDestinations = it
            LocaPinResult.Success(it)
        },
        onFailure = {
            if (cache.lastDestinations.isNotEmpty()) LocaPinResult.Success(cache.lastDestinations)
            else LocaPinResult.Error(it.message ?: "Unable to load destinations")
        }
    )

    override suspend fun getDestinationDetail(id: String): LocaPinResult<Destination> = runCatching {
        api.destinationDetail(id).data?.toDomain()
    }.fold(
        onSuccess = { it?.let { data -> LocaPinResult.Success(data) } ?: LocaPinResult.Error("Destination not found") },
        onFailure = { LocaPinResult.Error(it.message ?: "Failed to load destination") }
    )

    override suspend fun getCategories(): LocaPinResult<List<Category>> = runCatching {
        api.categories().data?.map { Category(it.id, it.name) } ?: emptyList()
    }.fold(
        onSuccess = {
            cache.lastCategories = it
            LocaPinResult.Success(it)
        },
        onFailure = {
            if (cache.lastCategories.isNotEmpty()) LocaPinResult.Success(cache.lastCategories)
            else LocaPinResult.Error(it.message ?: "Unable to load categories")
        }
    )

    override suspend fun getFavorites(): LocaPinResult<List<Destination>> = runCatching {
        api.favorites().data?.map { it.toDomain() } ?: emptyList()
    }.fold(
        onSuccess = { LocaPinResult.Success(it) },
        onFailure = { LocaPinResult.Error(it.message ?: "Unable to load favorites") }
    )

    override suspend fun setFavorite(id: String, save: Boolean): LocaPinResult<Unit> = runCatching {
        if (save) api.addFavorite(id) else api.removeFavorite(id)
    }.fold(
        onSuccess = { LocaPinResult.Success(Unit) },
        onFailure = { LocaPinResult.Error(it.message ?: "Unable to update favorite") }
    )
}

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: LocaPinApi
) : ProfileRepository {
    override suspend fun getProfile(): LocaPinResult<User> = runCatching {
        api.profile().data?.toDomain()
    }.fold(
        onSuccess = { it?.let { data -> LocaPinResult.Success(data) } ?: LocaPinResult.Error("Missing profile") },
        onFailure = { LocaPinResult.Error(it.message ?: "Unable to load profile") }
    )
}
