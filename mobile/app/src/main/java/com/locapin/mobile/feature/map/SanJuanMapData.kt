package com.locapin.mobile.feature.map

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

object SanJuanMapData {
    const val mapWidth = 1000f
    const val mapHeight = 620f

    val attractionsBySector: Map<String, List<Attraction>> = mapOf(
        "greenhills" to listOf(
            Attraction(
                "Greenhills Shopping Center",
                "Premier shopping destination for gadgets and pearls.",
                "2.8 km",
                "https://share.google/a065afgLuqEpcOcKb",
                "Known for: A popular shopping destination famous for pearls, souvenirs, gadgets, bargain shopping, and food outlets."
            ),
            Attraction(
                "Greenhills Promenade",
                "Upscale lifestyle center with luxury cinemas.",
                "3.1 km",
                "https://share.google/MKsJAiGlTZoQrmv7j",
                "Known for: The upscale dining and lifestyle area of Greenhills with restaurants, cafés, cinemas, and entertainment spots."
            ),
            Attraction(
                "Virra Mall (V-Mall)",
                "Shopping mall known for electronics and budget-friendly items.",
                "2.9 km",
                "https://share.google/tXc6Bhhsb7xw1rtx6",
                "Known for: A shopping mall known for electronics, tiangge stalls, and budget-friendly items, especially popular with students."
            ),
            Attraction(
                "Unimart at Greenhills",
                "Premium grocery and shopping spot.",
                "3.0 km",
                "https://share.google/8bO96AZWQSag7ZaQG",
                "Known for: A well-known premium grocery and shopping spot inside Greenhills, popular for local and imported goods."
            )
        ),
        "addition-hills" to listOf(
            Attraction(
                "Pinaglabanan Shrine",
                "Commemorating the first battle of the Katipunan.",
                "0.9 km",
                "https://share.google/KTGjRwqyUvzVyZbkS",
                "Known for: A national historical landmark commemorating the Battle of San Juan del Monte, the first major battle of the Philippine Revolution."
            ),
            Attraction(
                "Santuario del Santo Cristo Parish",
                "One of the oldest churches in San Juan.",
                "1.1 km",
                "https://share.google/98z3Kh3qiUNmCEiNs",
                "Known for: One of the oldest churches in San Juan, rich in religious heritage and local devotion."
            ),
            Attraction(
                "El Deposito Museum",
                "Underground reservoir turned museum.",
                "0.8 km",
                "https://share.google/mtZb9LYrFu2ea9WjY",
                "Known for: An underground reservoir turned museum showcasing the Spanish-era water system of Manila and its role in Philippine history."
            )
        ),
        "little-baguio" to listOf(
            Attraction(
                "The Corner House",
                "Modern lifestyle and dining hub.",
                "1.5 km",
                "https://share.google/CWHsen0F8JoxHUF0l",
                "Known for: A modern lifestyle and dining hub featuring a curated mix of cafés, restaurants, dessert spots, and retail stalls in a stylish, open-concept space."
            ),
            Attraction(
                "Fundacion Sanso",
                "Art museum featuring works of National Artist Juvenal Sansó.",
                "1.6 km",
                "https://share.google/F1P9LZjFN6NKfKRyX",
                "Known for: An art and cultural museum featuring works of National Artist Juvenal Sansó and curated exhibitions by Filipino visual artists."
            ),
            Attraction(
                "Good Pastry Cafe",
                "Cozy café known for freshly baked pastries.",
                "1.4 km",
                "https://share.google/mqRVHvi4wPxlbFmcJ",
                "A cozy café known for its freshly baked pastries and desserts. It offers a variety of croissants, cakes, and sweet treats."
            )
        ),
        "pasadena" to listOf(
            Attraction(
                "Ronac Art Center",
                "Contemporary art center on Ortigas Avenue.",
                "1.3 km",
                "https://share.google/nZxNrAeoiPMhEI7go",
                "Known for: A contemporary art center showcasing modern Filipino art, large installations, rotating exhibitions, cafés, and design stores."
            )
        ),
        "batis" to listOf(
            Attraction(
                "Club Filipino",
                "Historic site of Corazon Aquino's inauguration.",
                "2.4 km",
                "https://share.google/lk8di0SW01riSp8tF",
                "Known for: A historic clubhouse where Corazon Aquino was inaugurated as President in 1986 after the People Power Revolution."
            ),
            Attraction(
                "Gaea",
                "Modern all-day restaurant and bar.",
                "2.2 km",
                "https://share.google/4wFTs7JUC4QlS1QOI",
                "GAEA is a modern all-day restaurant and bar in San Juan known for its stylish, moody interiors and upscale yet cozy ambiance."
            )
        ),
        "kabayanan" to listOf(
            Attraction(
                "Museo ng Katipunan",
                "Features artifacts of the Philippine Revolution.",
                "0.8 km",
                "https://share.google/VKJwnOaFpjPMRZNVZ",
                "Known for: A historical museum about the Katipunan and the 1896 Philippine Revolution, featuring artifacts, documents, and multimedia exhibits."
            )
        ),
        "santa-lucia" to listOf(
            Attraction(
                "Santolan Town Plaza",
                "Open-air lifestyle mall with specialty restaurants.",
                "3.2 km",
                "https://share.google/Th2CJGpgzFw4bCkwP",
                "Known for: An open-air lifestyle mall featuring specialty restaurants, cafés, and boutique shops."
            )
        )
    )

    val sectors: List<MapSector> = listOf(
        sector("salapan", "Salapan", 180f, 100f, color = 0xFFF0F4A4, 
            points = listOf(140f to 60f, 175f to 55f, 220f to 75f, 280f to 80f, 260f to 150f, 220f to 140f, 185f to 145f, 150f to 175f, 110f to 160f, 120f to 100f)),
        sector("ermitaño", "Ermitaño", 310f, 110f, color = 0xFFF4D2C1, 
            points = listOf(280f to 80f, 340f to 95f, 370f to 140f, 340f to 175f, 300f to 175f, 260f to 150f)),
        sector("balong-bato", "Balong-Bato", 200f, 180f, color = 0xFFD7D3E4, 
            points = listOf(150f to 175f, 185f to 145f, 220f to 140f, 260f to 150f, 300f to 175f, 310f to 215f, 280f to 260f, 180f to 230f)),
        sector("rivera", "Rivera", 110f, 220f, color = 0xFFC7E2B0, 
            points = listOf(110f to 160f, 150f to 175f, 180f to 230f, 135f to 255f, 100f to 265f, 70f to 245f)),
        sector("progreso", "Progreso", 80f, 290f, color = 0xFFE9DCC9, 
            points = listOf(70f to 245f, 100f to 265f, 95f to 285f, 55f to 315f, 40f to 275f)),
        sector("san-perfecto", "San Perfecto", 140f, 300f, color = 0xFFF0F4A4, 
            points = listOf(100f to 265f, 135f to 255f, 220f to 320f, 180f to 365f, 120f to 345f, 95f to 285f)),
        sector("pedro-cruz", "Pedro Cruz", 220f, 270f, color = 0xFFF0E6D2, 
            points = listOf(180f to 230f, 280f to 260f, 310f to 315f, 220f to 320f, 135f to 255f)),
        sector("corazon-de-jesus", "Corazon de Jesus", 350f, 280f, color = 0xFFF0F4A4, 
            points = listOf(280f to 260f, 310f to 215f, 350f to 260f, 380f to 320f, 310f to 315f)),
        sector("pasadena", "Pasadena", 400f, 220f, color = 0xFFC9DFEE, 
            points = listOf(300f to 175f, 340f to 175f, 370f to 140f, 420f to 180f, 460f to 230f, 460f to 280f, 350f to 260f, 310f to 215f)),
        sector("batis", "Batis", 200f, 450f, color = 0xFFC9DFEE, 
            points = listOf(95f to 285f, 120f to 345f, 180f to 365f, 225f to 415f, 280f to 480f, 220f to 570f, 180f to 480f, 55f to 365f, 55f to 315f)),
        sector("tibagan", "Tibagan", 280f, 400f, color = 0xFFF0E6D2, 
            points = listOf(220f to 320f, 265f to 320f, 285f to 365f, 330f to 375f, 330f to 440f, 280f to 480f, 225f to 415f, 180f to 365f)),
        sector("kabayanan", "Kabayanan", 320f, 500f, color = 0xFFF0E6D2, 
            points = listOf(280f to 480f, 330f to 440f, 335f to 435f, 395f to 405f, 470f to 460f, 360f to 520f)),
        sector("maytunas", "Maytunas", 380f, 550f, color = 0xFFD7D3E4, 
            points = listOf(360f to 520f, 470f to 460f, 470f to 540f, 430f to 560f, 325f to 540f, 260f to 535f)),
        sector("santa-lucia", "Santa Lucia", 410f, 420f, color = 0xFFF0F4A4, 
            points = listOf(380f to 320f, 380f to 345f, 450f to 315f, 490f to 385f, 395f to 405f, 385f to 365f)),
        sector("isabelita", "Isabelita", 320f, 335f, color = 0xFFC7E2B0, 
            points = listOf(310f to 315f, 345f to 320f, 330f to 350f, 300f to 355f, 285f to 365f, 265f to 320f)),
        sector("halo-halo", "Halo-Halo", 360f, 345f, color = 0xFFD7D3E4, 
            points = listOf(345f to 320f, 380f to 320f, 385f to 365f, 330f to 375f, 300f to 355f, 330f to 350f)),
        sector("onse", "Onse", 360f, 400f, color = 0xFFC9DFEE, 
            points = listOf(330f to 375f, 385f to 365f, 395f to 405f, 335f to 435f, 330f to 440f)),
        sector("little-baguio", "Little Baguio", 480f, 370f, color = 0xFFE9B7B7, 
            points = listOf(350f to 260f, 460f to 280f, 520f to 320f, 580f to 450f, 530f to 480f, 470f to 460f, 380f to 345f, 380f to 320f)),
        sector("greenhills", "Greenhills", 700f, 350f, color = 0xFFC7E2B0, 
            points = listOf(460f to 230f, 550f to 165f, 630f to 125f, 690f to 160f, 715f to 230f, 810f to 215f, 920f to 315f, 830f to 335f, 750f to 330f, 650f to 410f, 570f to 490f, 530f to 480f, 580f to 450f, 520f to 320f, 460f to 280f)),
        sector("west-crame", "West Crame", 740f, 180f, color = 0xFFD7D3E4, 
            points = listOf(690f to 160f, 765f to 145f, 790f to 205f, 715f to 230f, 690f to 160f)),
        sector("addition-hills", "Addition Hills", 520f, 540f, color = 0xFFF0E6D2, 
            points = listOf(470f to 460f, 530f to 480f, 570f to 490f, 545f to 560f, 515f to 585f, 460f to 580f, 395f to 575f, 345f to 605f, 315f to 565f, 260f to 535f, 325f to 540f, 430f to 560f, 470f to 540f, 470f to 460f))
    )

    private fun sector(
        id: String,
        name: String,
        labelX: Float,
        labelY: Float,
        color: Long,
        points: List<Pair<Float, Float>>
    ): MapSector {
        val polygon = points.map { Offset(it.first, it.second) }
        val attractionCount = attractionsBySector[id]?.size ?: 0
        return MapSector(
            id = id,
            name = name,
            fillColor = Color(color),
            labelPosition = Offset(labelX, labelY),
            polygonPoints = polygon,
            attractionsCount = attractionCount
        )
    }
}
