package com.locapin.mobile.data.local

import com.google.firebase.firestore.FirebaseFirestore
import com.locapin.mobile.feature.admin.FirebaseAdminAttractionModel
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Locale
import kotlinx.coroutines.tasks.await

@Singleton
class FirebasePopulator @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val seedDataSource: SanJuanSeedDataSource
) {
    suspend fun populate() = runCatching {
        val currentSeedVersion = 3 // Increment this to force re-seeding
        val lastSeedVersion = getPopulatedVersion()
        
        if (lastSeedVersion >= currentSeedVersion) return@runCatching

        android.util.Log.d("FirebasePopulator", "Seeding database (version $currentSeedVersion)...")

        // Clean database once before re-populating with stable IDs
        clearCollection("attractions")
        clearCollection("map_zones")

        populateAttractions()
        populateMapZones()
        markAsPopulated(currentSeedVersion)
    }.onFailure { e ->
        android.util.Log.e("FirebasePopulator", "Failed to populate Firestore: ${e.message}")
    }

    private suspend fun getPopulatedVersion(): Int {
        return try {
            val snapshot = firestore.collection("metadata").document("seed_info").get().await()
            if (snapshot.exists()) {
                (snapshot.get("version") as? Long)?.toInt() ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    private suspend fun markAsPopulated(version: Int) {
        firestore.collection("metadata").document("seed_info")
            .set(mapOf(
                "populated" to true, 
                "version" to version,
                "timestamp" to System.currentTimeMillis()
            ))
            .await()
    }

    private suspend fun clearCollection(collectionName: String) {
        val snapshot = firestore.collection(collectionName).get().await()
        if (snapshot.isEmpty) return
        val batch = firestore.batch()
        snapshot.documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    private suspend fun populateAttractions() {
        val startLat = 14.5960
        val startLng = 121.0116

        val attractions = listOf(
            FirebaseAdminAttractionModel(
                name = "Museo ng Katipunan",
                knownFor = "Katipunan memorabilia",
                description = "A historical museum about the Katipunan and the 1896 Philippine Revolution, featuring artifacts, documents, and multimedia exhibits about Andres Bonifacio.",
                category = "Museum",
                latitude = 14.6042,
                longitude = 121.0287,
                area = "kabayanan",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Pinaglabanan Shrine",
                knownFor = "Historic battle landmark",
                description = "A national historical landmark commemorating the Battle of San Juan del Monte, the first major battle of the Philippine Revolution.",
                category = "Historical Site",
                latitude = 14.6004,
                longitude = 121.0264,
                area = "addition-hills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1590076247841-7c8d01aa4851?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "El Deposito Museum",
                knownFor = "Spanish-era water system",
                description = "An underground reservoir turned museum showcasing the Spanish-era water system of Manila and its role in Philippine history.",
                category = "Museum",
                latitude = 14.5998,
                longitude = 121.0314,
                area = "addition-hills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1582555172866-f73bb12a2ab3?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "The Corner House",
                knownFor = "Modern lifestyle and dining hub",
                description = "A modern lifestyle and dining hub featuring a curated mix of cafés, restaurants, dessert spots, and retail stalls in a stylish, open-concept space.",
                category = "Commercial",
                latitude = 14.6025,
                longitude = 121.0355,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Club Filipino",
                knownFor = "Historical Landmark",
                description = "A historic clubhouse where Corazon Aquino was inaugurated as President in 1986 after the People Power Revolution.",
                category = "Historical Site",
                latitude = 14.6035,
                longitude = 121.0210,
                area = "batis",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Santuario del Santo Cristo Parish",
                knownFor = "Religious heritage",
                description = "One of the oldest churches in San Juan, rich in religious heritage and local devotion.",
                category = "Religious Site",
                latitude = 14.5979,
                longitude = 121.0232,
                area = "addition-hills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Ronac Art Center",
                knownFor = "Contemporary local art",
                description = "A contemporary art center showcasing modern Filipino art, large installations, rotating exhibitions, cafés, and design stores along Ortigas Avenue.",
                category = "Art Gallery",
                latitude = 14.6103,
                longitude = 121.0385,
                area = "pasadena",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1493397212122-2b85def82820?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Fundacion Sanso",
                knownFor = "Juvenal Sanso collections",
                description = "An art and cultural museum featuring works of National Artist Juvenal Sansó and curated exhibitions by Filipino visual artists.",
                category = "Museum",
                latitude = 14.6073,
                longitude = 121.0314,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1518998053574-53fd1f61a837?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Good Pastry Cafe",
                knownFor = "Freshly baked pastries",
                description = "A cozy café in known for its freshly baked pastries and desserts. It offers a variety of croissants, cakes, and sweet treats.",
                category = "Food & Dining",
                latitude = 14.5996,
                longitude = 121.0371,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Greenhills Shopping Center",
                knownFor = "Shopping and dining hub",
                description = "A popular shopping destination famous for pearls, souvenirs, gadgets, bargain shopping, and food outlets.",
                category = "Commercial",
                latitude = 14.6019,
                longitude = 121.0446,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Greenhills Promenade",
                knownFor = "Upscale dining and lifestyle",
                description = "The upscale dining and lifestyle area of Greenhills with restaurants, cafés, cinemas, and entertainment spots.",
                category = "Commercial",
                latitude = 14.6025,
                longitude = 121.0455,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Virra Mall (V-Mall)",
                knownFor = "Electronics and budget shopping",
                description = "A shopping mall known for electronics, tiangge stalls, and budget-friendly items, especially popular with students.",
                category = "Commercial",
                latitude = 14.6030,
                longitude = 121.0435,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1460317442991-0ec209397118?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Unimart at Greenhills",
                knownFor = "Premium grocery",
                description = "A well-known premium grocery and shopping spot inside Greenhills, popular for local and imported goods.",
                category = "Commercial",
                latitude = 14.6010,
                longitude = 121.0440,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Santolan Town Plaza",
                knownFor = "Open-air lifestyle mall",
                description = "An open-air lifestyle mall featuring specialty restaurants, cafés, and boutique shops.",
                category = "Commercial",
                latitude = 14.6105,
                longitude = 121.0234,
                area = "santa-lucia",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800&q=80"
            ),
            FirebaseAdminAttractionModel(
                name = "Gaea",
                knownFor = "Modern restaurant and bar",
                description = "GAEA is a modern all-day restaurant and bar in San Juan known for its stylish, moody interiors and upscale yet cozy ambiance.",
                category = "Food & Dining",
                latitude = 14.6045,
                longitude = 121.0225,
                area = "batis",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&w=800&q=80"
            )
        )

        val batch = firestore.batch()
        attractions.forEach { attraction ->
            val id = attraction.name.lowercase(Locale.US).replace(" ", "_").replace("[^a-z0-9_]".toRegex(), "")
            val docRef = firestore.collection("attractions").document(id)
            // Calculate mock distance from starting point
            val dist = calculateDistance(startLat, startLng, attraction.latitude, attraction.longitude)
            val data = mapOf(
                "name" to attraction.name,
                "knownFor" to attraction.knownFor,
                "description" to attraction.description,
                "category" to attraction.category,
                "latitude" to attraction.latitude,
                "longitude" to attraction.longitude,
                "area" to attraction.area,
                "imageUrl" to attraction.imageUrl,
                "visible" to attraction.visible,
                "distance" to String.format(Locale.US, "%.1f km", dist)
            )
            batch.set(docRef, data)
        }
        batch.commit().await()
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    private suspend fun populateMapZones() {
        val seedZones = seedDataSource.mapZones()
        val batch = firestore.batch()
        
        seedZones.forEach { zone ->
            val docRef = firestore.collection("map_zones").document(zone.id)
            val zoneData = mapOf(
                "displayName" to zone.displayName,
                "centerLat" to zone.centerLat,
                "centerLng" to zone.centerLng,
                "polygonPoints" to zone.polygonPoints.map { mapOf("lat" to it.lat, "lng" to it.lng) }
            )
            batch.set(docRef, zoneData)
        }
        
        // Add placeholders for other sectors if they don't exist in seed data
        val sectors = listOf(
            "salapan" to "Salapan",
            "ermitaño" to "Ermitaño",
            "balong-bato" to "Balong-Bato",
            "rivera" to "Rivera",
            "pedro-cruz" to "Pedro Cruz",
            "corazon-de-jesus" to "Corazon de Jesus",
            "pasadena" to "Pasadena",
            "west-crame" to "West Crame",
            "san-perfecto" to "San Perfecto",
            "progress" to "Progress",
            "batis" to "Batis",
            "isabelita" to "Isabelita",
            "halo-halo" to "Halo-Halo",
            "onse" to "Onse",
            "tibagan" to "Tibagan",
            "kabayanan" to "Kabayanan",
            "santa-lucia" to "Santa Lucia",
            "little-baguio" to "Little Baguio",
            "maytunas" to "Maytunas",
            "addition-hills" to "Addition Hills"
        )

        sectors.forEach { (id, name) ->
            if (seedZones.none { it.id == id }) {
                val docRef = firestore.collection("map_zones").document(id)
                val zoneData = mapOf(
                    "displayName" to name,
                    "centerLat" to 14.6000,
                    "centerLng" to 121.0300,
                    "polygonPoints" to emptyList<Map<String, Double>>()
                )
                batch.set(docRef, zoneData)
            }
        }

        batch.commit().await()
    }
}
