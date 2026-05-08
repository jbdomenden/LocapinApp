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
    suspend fun uploadImage(uri: android.net.Uri): String?
    fun refresh()
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
    val imageUrl: String? = null,
    val distance: String? = null,
    val rating: Double = 0.0,
    val reviews: Int = 0
)

@Singleton
class InMemoryAdminAttractionRepository @Inject constructor() : AdminAttractionRepository {
    override suspend fun uploadImage(uri: android.net.Uri): String? = null

    override fun refresh() {
        // Mock refresh logic
    }
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
        isVisible = isVisible,
        imageUrl = imageUrl,
        distance = distance
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
            area = "Kabayanan",
            isVisible = true,
            distance = "1.5 km"
        ),
        AdminAttraction(
            id = "pinaglabanan-shrine",
            name = "Pinaglabanan Shrine",
            knownFor = "Historic battle landmark",
            description = "Memorial shrine and park honoring the Battle of Pinaglabanan.",
            category = "Historical Site",
            latitude = 14.6004,
            longitude = 121.0264,
            area = "Corazon de Jesus",
            isVisible = true,
            distance = "1.2 km"
        ),
        AdminAttraction(
            id = "ronac-art-center",
            name = "Ronac Art Center",
            knownFor = "Contemporary local art",
            description = "Modern art center showcasing rotating exhibits and creative spaces.",
            category = "Art Gallery",
            latitude = 14.6103,
            longitude = 121.0385,
            area = "Pasadena",
            isVisible = true,
            distance = "2.8 km"
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
            isVisible = true,
            distance = "2.1 km"
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
            isVisible = true,
            distance = "3.2 km"
        ),
        AdminAttraction(
            id = "santolan-town-plaza",
            name = "Santolan Town Plaza",
            knownFor = "Lifestyle and dining",
            description = "A neighborhood retail and dining destination.",
            category = "Commercial",
            latitude = 14.6105,
            longitude = 121.0234,
            area = "Santa Lucia",
            isVisible = true,
            distance = "2.4 km"
        ),
        AdminAttraction(
            id = "cardinal-santos",
            name = "Cardinal Santos Medical Center",
            knownFor = "Premier healthcare",
            description = "One of the leading medical institutions in the country.",
            category = "Medical",
            latitude = 14.5986,
            longitude = 121.0450,
            area = "Greenhills",
            isVisible = true,
            distance = "3.5 km"
        ),
        AdminAttraction(
            id = "xavier-school",
            name = "Xavier School",
            knownFor = "Jesuit education",
            description = "A prestigious Catholic school for boys.",
            category = "Education",
            latitude = 14.6030,
            longitude = 121.0330,
            area = "Little Baguio",
            isVisible = true,
            distance = "2.3 km"
        ),
        AdminAttraction(
            id = "st-john-baptist",
            name = "St. John the Baptist Church",
            knownFor = "Historical church",
            description = "A 19th-century church and a landmark in San Juan.",
            category = "Religious",
            latitude = 14.6025,
            longitude = 121.0285,
            area = "Pedro Cruz",
            isVisible = true,
            distance = "1.8 km"
        ),
        AdminAttraction(
            id = "san-juan-medical",
            name = "San Juan Medical Center",
            knownFor = "Public healthcare",
            description = "The primary public hospital serving the residents of San Juan.",
            category = "Medical",
            latitude = 14.5960,
            longitude = 121.0320,
            area = "Addition Hills",
            isVisible = true,
            distance = "2.9 km"
        ),
        AdminAttraction(
            id = "salapan-creek-park",
            name = "Salapan Creek Park",
            knownFor = "Riverside greenery",
            description = "A small linear park along the Salapan creek area.",
            category = "Park",
            latitude = 14.6100,
            longitude = 121.0200,
            area = "Salapan",
            isVisible = true,
            distance = "1.6 km"
        ),
        AdminAttraction(
            id = "maytunas-park",
            name = "Maytunas Park",
            knownFor = "Local recreation",
            description = "A neighborhood park providing green space for the community.",
            category = "Park",
            latitude = 14.6020,
            longitude = 121.0335,
            area = "Maytunas",
            isVisible = true,
            distance = "2.5 km"
        ),
        AdminAttraction(
            id = "city-hall",
            name = "San Juan City Hall",
            knownFor = "Local government hub",
            description = "The center of local administration in San Juan.",
            category = "Government",
            latitude = 14.6045,
            longitude = 121.0265,
            area = "Corazon de Jesus",
            isVisible = true,
            distance = "1.3 km"
        )
    ).sortedBy { it.name.lowercase() }
}
