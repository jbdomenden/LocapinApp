package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.remote.AttractionApiService
import com.locapin.mobile.data.remote.CategoryApiService
import com.locapin.mobile.domain.model.Category
import com.locapin.mobile.domain.model.Destination
import com.locapin.mobile.domain.repository.DestinationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDestinationRepository @Inject constructor(
    private val attractionApiService: AttractionApiService,
    private val categoryApiService: CategoryApiService
) : DestinationRepository {
    override suspend fun getDestinations(
        query: String?,
        categoryId: String?,
        sort: String?,
        page: Int,
        lat: Double?,
        lng: Double?
    ): LocaPinResult<List<Destination>> = runCatching {
        val response = attractionApiService.getAttractions(
            query = query,
            categoryId = categoryId,
            sort = sort,
            page = page,
            lat = lat,
            lng = lng
        )
        LocaPinResult.Success(response.data.orEmpty().map { it.toDomain() })
    }.getOrElse { LocaPinResult.Error(it.message ?: "Unable to fetch attractions") }

    override suspend fun getDestinationDetail(id: String): LocaPinResult<Destination> = runCatching {
        val response = attractionApiService.getAttractionDetail(id)
        val destination = response.data ?: return LocaPinResult.Error(response.error ?: "Destination not found")
        LocaPinResult.Success(destination.toDomain())
    }.getOrElse { LocaPinResult.Error(it.message ?: "Unable to fetch destination") }

    override suspend fun getCategories(): LocaPinResult<List<Category>> = runCatching {
        val response = categoryApiService.getCategories()
        LocaPinResult.Success(response.data.orEmpty().map { Category(id = it.id, name = it.name) })
    }.getOrElse { LocaPinResult.Error(it.message ?: "Unable to fetch categories") }

    override suspend fun getFavorites(): LocaPinResult<List<Destination>> =
        LocaPinResult.Error("Remote favorites wiring is deferred to a later phase")

    override suspend fun setFavorite(id: String, save: Boolean): LocaPinResult<Unit> =
        LocaPinResult.Error("Remote favorites wiring is deferred to a later phase")
}
