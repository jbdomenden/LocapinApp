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
    private val categoryApiService: CategoryApiService,
    private val fallbackRepository: DestinationRepositoryImpl
) : DestinationRepository {
    override suspend fun getDestinations(
        query: String?,
        categoryId: String?,
        sort: String?,
        page: Int,
        lat: Double?,
        lng: Double?
    ): LocaPinResult<List<Destination>> {
        return runCatching {
            val response = attractionApiService.getAttractions(
                query = query,
                categoryId = categoryId,
                sort = sort,
                page = page,
                lat = lat,
                lng = lng
            )
            when {
                response.data != null -> LocaPinResult.Success(response.data.map { it.toDomain() })
                !response.error.isNullOrBlank() -> LocaPinResult.Error(response.error)
                else -> LocaPinResult.Error(response.message ?: "Unable to fetch attractions")
            }
        }.getOrElse {
            fallbackRepository.getDestinations(query, categoryId, sort, page, lat, lng)
        }
    }

    override suspend fun getDestinationDetail(id: String): LocaPinResult<Destination> {
        return runCatching {
            val response = attractionApiService.getAttractionDetail(id)
            val destination = response.data
            when {
                destination != null -> LocaPinResult.Success(destination.toDomain())
                !response.error.isNullOrBlank() -> LocaPinResult.Error(response.error)
                else -> LocaPinResult.Error(response.message ?: "Destination not found")
            }
        }.getOrElse {
            fallbackRepository.getDestinationDetail(id)
        }
    }

    override suspend fun getCategories(): LocaPinResult<List<Category>> {
        return runCatching {
            val response = categoryApiService.getCategories()
            when {
                response.data != null -> {
                    LocaPinResult.Success(response.data.map { Category(id = it.id, name = it.name) })
                }

                !response.error.isNullOrBlank() -> LocaPinResult.Error(response.error)
                else -> LocaPinResult.Error(response.message ?: "Unable to fetch categories")
            }
        }.getOrElse {
            fallbackRepository.getCategories()
        }
    }

    override suspend fun getFavorites(): LocaPinResult<List<Destination>> =
        fallbackRepository.getFavorites()

    override suspend fun setFavorite(id: String, save: Boolean): LocaPinResult<Unit> =
        fallbackRepository.setFavorite(id, save)
}
