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
        val currentSeedVersion = 7 // Increment to version 7 for new attractions
        val lastSeedVersion = getPopulatedVersion()
        
        if (lastSeedVersion >= currentSeedVersion) return@runCatching

        android.util.Log.d("FirebasePopulator", "Seeding database (version $currentSeedVersion)...")

        // Removed clearCollection to prevent wiping manual updates (like images)
        // clearCollection("attractions") 
        // clearCollection("map_areas")

        populateAttractions()
        populateMapAreas()
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

        // Fetch existing attraction IDs to avoid overwriting manual updates
        val existingDocs = firestore.collection("attractions").get().await()
        val existingIds = existingDocs.documents.map { it.id }.toSet()

        val attractions = listOf(
            FirebaseAdminAttractionModel(
                name = "Greenhills Shopping Center",
                knownFor = "Bargain shopping, pearls, and gadgets.",
                description = "A popular shopping destination famous for pearls, souvenirs, gadgets, bargain shopping, and food outlets.",
                category = "Commercial",
                latitude = 425.0,
                longitude = 175.0,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800"
            ),
            // ... (rest of the attractions list remains the same)
            FirebaseAdminAttractionModel(
                name = "Greenhills Promenade",
                knownFor = "Upscale dining and lifestyle area.",
                description = "The upscale dining and lifestyle area of Greenhills with restaurants, cafés, cinemas, and entertainment spots.",
                category = "Commercial",
                latitude = 445.0,
                longitude = 185.0,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Virra Mall (V-Mall)",
                knownFor = "Electronics, tiangge stalls, and budget-friendly items.",
                description = "A shopping mall known for electronics, tiangge stalls, and budget-friendly items, especially popular with students.",
                category = "Commercial",
                latitude = 415.0,
                longitude = 195.0,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1550745165-9bc0b252726f?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Unimart at Greenhills",
                knownFor = "Premium grocery and shopping spot.",
                description = "A well-known premium grocery and shopping spot inside Greenhills, popular for local and imported goods.",
                category = "Commercial",
                latitude = 435.0,
                longitude = 205.0,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Pinaglabanan Shrine",
                knownFor = "Commemorating the Battle of San Juan del Monte.",
                description = "A national historical landmark commemorating the Battle of San Juan del Monte, the first major battle of the Philippine Revolution.",
                category = "Historical Site",
                latitude = 285.0,
                longitude = 315.0,
                area = "addition-hills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Santuario del Santo Cristo Parish",
                knownFor = "Oldest church in San Juan.",
                description = "One of the oldest churches in San Juan, rich in religious heritage and local devotion.",
                category = "Religious Site",
                latitude = 305.0,
                longitude = 325.0,
                area = "addition-hills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1548625361-195feee1a4ce?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "El Deposito Museum",
                knownFor = "Spanish-era water system reservoir.",
                description = "An underground reservoir turned museum showcasing the Spanish-era water system of Manila and its role in Philippine history.",
                category = "Museum",
                latitude = 275.0,
                longitude = 335.0,
                area = "addition-hills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1534430480872-3498386e7a56?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Xavier School",
                knownFor = "Exclusive Catholic school for boys.",
                description = "A prestigious Jesuit school known for its excellence in education and strong alumni network.",
                category = "Educational Site",
                latitude = 255.0,
                longitude = 175.0,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Mary the Queen Parish",
                knownFor = "Beautiful parish church in San Juan.",
                description = "A popular church for weddings and religious services in the Little Baguio area.",
                category = "Religious Site",
                latitude = 265.0,
                longitude = 195.0,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "San Juan City Hall",
                knownFor = "The seat of government of San Juan City.",
                description = "The center of local administration and public services in San Juan.",
                category = "Government",
                latitude = 235.0,
                longitude = 185.0,
                area = "corazon-de-jesus",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1577495508048-b635879837f1?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Santisimo Rosario Parish",
                knownFor = "Dominican-run parish church.",
                description = "A historic church managed by the Dominican Order, serving the local community.",
                category = "Religious Site",
                latitude = 195.0,
                longitude = 265.0,
                area = "santa-lucia",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1516026672322-bc52d61a55d5?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Salapan Creek",
                knownFor = "A historic waterway in San Juan.",
                description = "A key waterway that defines the boundary of the barangay and has historic significance.",
                category = "Landmark",
                latitude = 105.0,
                longitude = 120.0,
                area = "salapan",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Ermitaño River",
                knownFor = "A major river system in San Juan.",
                description = "Its role in the local ecology and history of the city.",
                category = "Landmark",
                latitude = 175.0,
                longitude = 110.0,
                area = "ermitaño",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1437333306198-097bb19646fa?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Balong-Bato Park",
                knownFor = "A local community park.",
                description = "Being a gathering spot for local residents.",
                category = "Park",
                latitude = 105.0,
                longitude = 175.0,
                area = "balong-bato",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Rivera Basketball Court",
                knownFor = "Center of local sports activities.",
                description = "Hosting various local sports tournaments.",
                category = "Recreation",
                latitude = 105.0,
                longitude = 185.0,
                area = "rivera",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1546519638-68e109498ffc?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Progreso Multi-purpose Hall",
                knownFor = "Community center for events.",
                description = "Local government and community gatherings.",
                category = "Government",
                latitude = 65.0,
                longitude = 210.0,
                area = "progreso",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "San Perfecto Parish",
                knownFor = "A spiritual center in the barangay.",
                description = "Religious services and community festivals.",
                category = "Religious Site",
                latitude = 85.0,
                longitude = 215.0,
                area = "san-perfecto",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1519676867240-f03562e64548?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Pedro Cruz Plaza",
                knownFor = "A small open space for the community.",
                description = "Local leisure and relaxation.",
                category = "Park",
                latitude = 135.0,
                longitude = 185.0,
                area = "pedro-cruz",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1464851707681-f9d5fdaccea8?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Pasadena Landmark",
                knownFor = "A recognizable point in the area.",
                description = "Guiding visitors through the barangay.",
                category = "Landmark",
                latitude = 245.0,
                longitude = 155.0,
                area = "pasadena",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1480714378408-67cf0d13bc1b?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Batis Community Center",
                knownFor = "Heart of the Batis community.",
                description = "Social services and public programs.",
                category = "Government",
                latitude = 125.0,
                longitude = 275.0,
                area = "batis",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1517048676732-d65bc937f952?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Tibagan Elementary School",
                knownFor = "A local educational institution.",
                description = "Providing primary education to local children.",
                category = "Educational Site",
                latitude = 145.0,
                longitude = 215.0,
                area = "tibagan",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1509062522246-3755977927d7?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Kabayanan Church",
                knownFor = "A place of worship in the heart of Kabayanan.",
                description = "Its long-standing presence in the community.",
                category = "Religious Site",
                latitude = 135.0,
                longitude = 275.0,
                area = "kabayanan",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Maytunas Creek View",
                knownFor = "Scenic spot near the waterway.",
                description = "Local scenery and environmental awareness.",
                category = "Landmark",
                latitude = 205.0,
                longitude = 305.0,
                area = "maytunas",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Isabelita Plaza",
                knownFor = "Community hub of the barangay.",
                description = "Public gatherings and local events.",
                category = "Park",
                latitude = 180.0,
                longitude = 205.0,
                area = "isabelita",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Halo-Halo Food Spot",
                knownFor = "Famous local dessert stalls.",
                description = "Authentic Filipino halo-halo.",
                category = "Food & Dining",
                latitude = 195.0,
                longitude = 195.0,
                area = "halo-halo",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1551024506-0bccd828d307?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Onse Market",
                knownFor = "Bustling local marketplace.",
                description = "Fresh produce and local goods.",
                category = "Commercial",
                latitude = 215.0,
                longitude = 205.0,
                area = "onse",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1488459711635-de72f15d3fef?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "West Crame Park",
                knownFor = "Recreational area for the neighborhood.",
                description = "Outdoor activities and family outings.",
                category = "Park",
                latitude = 355.0,
                longitude = 135.0,
                area = "west-crame",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Museo ng Katipunan",
                knownFor = "A historical museum about the Katipunan and the 1896 Philippine Revolution.",
                description = "Featuring artifacts, documents, and multimedia exhibits about Andres Bonifacio.",
                category = "Museum",
                latitude = 290.0,
                longitude = 300.0,
                area = "addition-hills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "The Corner House",
                knownFor = "Modern lifestyle and dining hub.",
                description = "Featuring a curated mix of cafés, restaurants, dessert spots, and retail stalls in a stylish, open-concept space.",
                category = "Commercial",
                latitude = 250.0,
                longitude = 210.0,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Club Filipino",
                knownFor = "Historic clubhouse of the 1986 inauguration.",
                description = "A historic clubhouse where Corazon Aquino was inaugurated as President in 1986 after the People Power Revolution.",
                category = "Historical Site",
                latitude = 410.0,
                longitude = 160.0,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1577495508048-b635879837f1?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Ronac Art Center",
                knownFor = "Contemporary art center and design hub.",
                description = "Showcasing modern Filipino art, large installations, rotating exhibitions, cafés, and design stores along Ortigas Avenue.",
                category = "Museum",
                latitude = 380.0,
                longitude = 450.0,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1499781350541-7783f6c6a0c8?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Fundacion Sanso",
                knownFor = "Art museum featuring works of National Artist Juvenal Sansó.",
                description = "An art and cultural museum featuring works of National Artist Juvenal Sansó and curated exhibitions by Filipino visual artists.",
                category = "Museum",
                latitude = 270.0,
                longitude = 220.0,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1544967082-d9d25d867d66?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Good Pastry Cafe",
                knownFor = "Cozy café known for freshly baked pastries.",
                description = "Offers a variety of croissants, cakes, and sweet treats in a relaxing and aesthetic ambiance.",
                category = "Food & Dining",
                latitude = 260.0,
                longitude = 230.0,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Santolan Town Plaza",
                knownFor = "Open-air lifestyle mall with specialty restaurants.",
                description = "Featuring specialty restaurants, cafés, and boutique shops in an open-air setting.",
                category = "Commercial",
                latitude = 350.0,
                longitude = 480.0,
                area = "greenhills",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1519167758481-83f550bb49b3?auto=format&fit=crop&w=800"
            ),
            FirebaseAdminAttractionModel(
                name = "Gaea",
                knownFor = "Modern all-day restaurant and bar.",
                description = "Stylish, moody interiors and upscale yet cozy ambiance, serving comfort-style dishes and creative cocktails.",
                category = "Food & Dining",
                latitude = 240.0,
                longitude = 240.0,
                area = "little-baguio",
                visible = true,
                imageUrl = "https://images.unsplash.com/photo-1514362545857-3bc16c4c7d1b?auto=format&fit=crop&w=800"
            )
        )

        val batch = firestore.batch()
        attractions.forEach { attraction ->
            val id = attraction.name.lowercase(Locale.US).replace(" ", "_").replace("[^a-z0-9_]".toRegex(), "")
            
            // Skip if the attraction already exists to preserve manual edits (like image updates)
            if (existingIds.contains(id)) return@forEach

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
                "distance" to String.format(Locale.US, "%.1f km", dist),
                "rating" to (3.5 + Math.random() * 1.5), // Random rating between 3.5 and 5.0
                "reviews" to (10 + (Math.random() * 200).toInt()) // Random reviews between 10 and 210
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

    private suspend fun populateMapAreas() {
        val seedZones = seedDataSource.mapZones()
        val batch = firestore.batch()
        
        seedZones.forEach { zone ->
            val docRef = firestore.collection("map_areas").document(zone.id)
            val areaData = mapOf(
                "name" to zone.displayName,
                "description" to "Historical and vibrant district in San Juan.",
                "districtLabel" to zone.displayName,
                "centerLatitude" to zone.centerLat,
                "centerLongitude" to zone.centerLng,
                "polygonPoints" to zone.polygonPoints.joinToString(";") { "${it.lng},${it.lat}" },
                "hexColor" to "#F0F4A4",
                "isPremium" to false
            )
            batch.set(docRef, areaData)
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
            "progreso" to "Progreso",
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
                val docRef = firestore.collection("map_areas").document(id)
                val areaData = mapOf(
                    "name" to name,
                    "description" to "Local community area in San Juan.",
                    "districtLabel" to name,
                    "centerLatitude" to 14.6000,
                    "centerLongitude" to 121.0300,
                    "polygonPoints" to "",
                    "hexColor" to "#F0F4A4",
                    "isPremium" to false
                )
                batch.set(docRef, areaData)
            }
        }

        batch.commit().await()
    }
}
