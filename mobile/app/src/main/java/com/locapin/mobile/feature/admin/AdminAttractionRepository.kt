package com.locapin.mobile.feature.admin

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

interface AdminAttractionRepository {
    val attractions: StateFlow<List<AdminAttraction>>
    val errorMessage: StateFlow<String?>
    fun clearError()
    fun getAttractionById(id: String): AdminAttraction?
    fun createAttraction(input: AdminAttractionInput): Boolean
    fun updateAttraction(id: String, input: AdminAttractionInput): Boolean
    fun deleteAttraction(id: String): Boolean
}

data class AdminAttractionInput(
    val name: String,
    val knownFor: String,
    val description: String,
    val category: String,
    val latitude: Double,
    val longitude: Double,
    val area: String,
    val isVisible: Boolean,
    val imageUrl: String? = null
)

@Singleton
class InMemoryAdminAttractionRepository @Inject constructor() : AdminAttractionRepository {
    private val _attractions = MutableStateFlow(seedAttractions())
    private val _errorMessage = MutableStateFlow<String?>(null)

    override val attractions: StateFlow<List<AdminAttraction>> = _attractions.asStateFlow()
    override val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    override fun clearError() {
        _errorMessage.value = null
    }

    override fun getAttractionById(id: String): AdminAttraction? =
        _attractions.value.firstOrNull { it.id == id }

    override fun createAttraction(input: AdminAttractionInput): Boolean {
        val newItem = input.toAttraction(id = UUID.randomUUID().toString())
        _attractions.update { existing -> (existing + newItem).sortedBy { it.name.lowercase() } }
        return true
    }

    override fun updateAttraction(id: String, input: AdminAttractionInput): Boolean {
        _attractions.update { existing ->
            existing.map { item ->
                if (item.id == id) input.toAttraction(id) else item
            }.sortedBy { it.name.lowercase() }
        }
        return true
    }

    override fun deleteAttraction(id: String): Boolean {
        _attractions.update { existing -> existing.filterNot { it.id == id } }
        return true
    }

    private fun AdminAttractionInput.toAttraction(id: String): AdminAttraction = AdminAttraction(
        id = id,
        name = name,
        knownFor = knownFor,
        description = description,
        category = category,
        latitude = latitude,
        longitude = longitude,
        area = area,
        isVisible = isVisible
    )

    private fun seedAttractions(): List<AdminAttraction> = listOf(
        AdminAttraction(
            id = "museo-ng-katipunan",
            name = "Museo ng Katipunan",
            knownFor = "Katipunan memorabilia",
            description = "Museum focused on Katipunan history and local heritage exhibits.",
            category = "Museum",
            latitude = 14.6042,
            longitude = 121.0287,
            area = "San Juan",
            isVisible = true
        ),
        AdminAttraction(
            id = "pinaglabanan-shrine",
            name = "Pinaglabanan Shrine",
            knownFor = "Historic battle landmark",
            description = "Memorial shrine and park honoring the Battle of Pinaglabanan.",
            category = "Historical Site",
            latitude = 14.6004,
            longitude = 121.0264,
            area = "Pinaglabanan",
            isVisible = true
        ),
        AdminAttraction(
            id = "ronac-art-center",
            name = "Ronac Art Center",
            knownFor = "Contemporary local art",
            description = "Modern art center showcasing rotating exhibits and creative spaces.",
            category = "Art Gallery",
            latitude = 14.6103,
            longitude = 121.0385,
            area = "Greenhills",
            isVisible = true
        ),
        AdminAttraction(
            id = "fundacion-sanso",
            name = "Fundacion Sanso",
            knownFor = "Juvenal Sanso collections",
            description = "Private foundation gallery featuring the works of Juvenal Sanso.",
            category = "Museum",
            latitude = 14.6073,
            longitude = 121.0314,
            area = "Little Baguio",
            isVisible = true
        ),
        AdminAttraction(
            id = "greenhills-shopping-center",
            name = "Greenhills Shopping Center",
            knownFor = "Shopping and dining hub",
            description = "Popular shopping and leisure destination in San Juan City.",
            category = "Commercial",
            latitude = 14.6019,
            longitude = 121.0446,
            area = "Greenhills",
            isVisible = true
        )
    ).sortedBy { it.name.lowercase() }
}
