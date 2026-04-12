package com.locapin.mobile.data.repository

import com.locapin.mobile.core.common.LocaPinResult
import com.locapin.mobile.data.remote.LocaPinApi
import com.locapin.mobile.domain.model.Category
import com.locapin.mobile.domain.model.Destination
import com.locapin.mobile.domain.model.User
import com.locapin.mobile.domain.repository.DestinationRepository
import com.locapin.mobile.domain.repository.ProfileRepository
import com.locapin.mobile.domain.repository.TouristFavoritesRepository
import com.locapin.mobile.feature.admin.AdminAttraction
import com.locapin.mobile.feature.admin.AdminAttractionRepository
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class DestinationRepositoryImpl @Inject constructor(
    private val api: LocaPinApi,
    private val adminAttractionRepository: AdminAttractionRepository,
    private val favoritesRepository: TouristFavoritesRepository
) : DestinationRepository {

    override suspend fun getDestinations(
        query: String?,
        categoryId: String?,
        sort: String?,
        page: Int,
        lat: Double?,
        lng: Double?
    ): LocaPinResult<List<Destination>> {
        val favoriteIds = favoritesRepository.favoriteIds.first()
        val categoryNameFilter = categoryId?.let { resolveCategoryName(it) }
        val normalizedQuery = query?.trim()?.lowercase(Locale.getDefault()).orEmpty()

        val items = adminAttractionRepository.attractions.value
            .asSequence()
            .filter { it.isVisible }
            .filter { item ->
                categoryNameFilter == null || item.category.equals(categoryNameFilter, ignoreCase = true)
            }
            .filter { item ->
                if (normalizedQuery.isBlank()) true
                else {
                    item.name.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                        item.knownFor.lowercase(Locale.getDefault()).contains(normalizedQuery) ||
                        item.area.lowercase(Locale.getDefault()).contains(normalizedQuery)
                }
            }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
            .map { it.toDestination(favoriteIds.contains(it.id)) }
            .toList()

        return LocaPinResult.Success(items)
    }

    override suspend fun getDestinationDetail(id: String): LocaPinResult<Destination> {
        val favoriteIds = favoritesRepository.favoriteIds.first()
        val attraction = adminAttractionRepository.getAttractionById(id)
            ?: return LocaPinResult.Error("Destination not found")
        return LocaPinResult.Success(attraction.toDestination(favoriteIds.contains(id)))
    }

    override suspend fun getCategories(): LocaPinResult<List<Category>> {
        val categories = adminAttractionRepository.attractions.value
            .asSequence()
            .map { it.category.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase(Locale.getDefault()) }
            .sortedBy { it.lowercase(Locale.getDefault()) }
            .map { categoryName ->
                Category(
                    id = categoryName.lowercase(Locale.getDefault()).replace(" ", "-"),
                    name = categoryName
                )
            }
            .toList()

        return LocaPinResult.Success(categories)
    }

    override suspend fun getFavorites(): LocaPinResult<List<Destination>> {
        val favoriteIds = favoritesRepository.favoriteIds.first()
        val favorites = adminAttractionRepository.attractions.value
            .filter { it.id in favoriteIds && it.isVisible }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }
            .map { it.toDestination(isFavorite = true) }
        return LocaPinResult.Success(favorites)
    }

    override suspend fun setFavorite(id: String, save: Boolean): LocaPinResult<Unit> {
        favoritesRepository.setFavorite(id = id, save = save)
        return LocaPinResult.Success(Unit)
    }

    private fun resolveCategoryName(categoryId: String): String =
        adminAttractionRepository.attractions.value
            .map { it.category }
            .firstOrNull { it.lowercase(Locale.getDefault()).replace(" ", "-") == categoryId }
            ?: categoryId

    private fun AdminAttraction.toDestination(isFavorite: Boolean): Destination = Destination(
        id = id,
        name = name,
        description = description,
        knownFor = knownFor,
        categoryName = category,
        area = area,
        address = "$area, San Juan City",
        lat = latitude,
        lng = longitude,
        heroImageUrl = imageUrl,
        galleryImages = imageUrl?.let { listOf(it) } ?: emptyList(),
        rating = null,
        distanceKm = distance?.removeSuffix(" km")?.toDoubleOrNull(),
        openingHours = null,
        contactInfo = null,
        isFavorite = isFavorite
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
