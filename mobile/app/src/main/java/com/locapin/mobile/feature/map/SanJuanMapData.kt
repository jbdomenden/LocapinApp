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
        sector("salapan", "Salapan", 130f, 120f, color = 0xFFEFE8A7, points = listOf(70f to 120f, 95f to 85f, 150f to 95f, 210f to 105f, 195f to 140f, 140f to 155f, 95f to 160f)),
        sector("ermitaño", "Ermitaño", 245f, 120f, color = 0xFFDCC9B9, points = listOf(195f to 110f, 235f to 95f, 285f to 100f, 305f to 130f, 255f to 145f, 210f to 140f)),
        sector("balong-bato", "Balong-Bato", 145f, 190f, color = 0xFFC9C0D9, points = listOf(95f to 160f, 140f to 155f, 195f to 140f, 230f to 170f, 190f to 205f, 120f to 230f, 85f to 205f)),
        sector("rivera", "Rivera", 70f, 250f, color = 0xFFC8DDB8, points = listOf(55f to 215f, 85f to 205f, 120f to 230f, 95f to 265f, 60f to 275f, 40f to 245f)),
        sector("pedro-cruz", "Pedro Cruz", 145f, 255f, color = 0xFFE7D7AE, points = listOf(95f to 265f, 120f to 230f, 190f to 205f, 215f to 250f, 165f to 285f, 120f to 295f)),
        sector("corazon-de-jesus", "Corazon de Jesus", 255f, 245f, color = 0xFFF3EEA9, points = listOf(190f to 205f, 230f to 170f, 305f to 165f, 340f to 205f, 330f to 255f, 275f to 270f, 215f to 250f)),
        sector("pasadena", "Pasadena", 315f, 200f, color = 0xFFC5CFDF, points = listOf(255f to 145f, 305f to 130f, 350f to 135f, 370f to 165f, 385f to 200f, 360f to 220f, 340f to 205f, 305f to 165f)),
        sector("west-crame", "West Crame", 720f, 185f, color = 0xFFCDC1D9, points = listOf(660f to 130f, 740f to 115f, 760f to 165f, 745f to 205f, 720f to 195f, 700f to 160f, 675f to 180f)),
        sector("greenhills", "Greenhills", 610f, 305f, color = 0xFFC7D9AB, points = listOf(430f to 190f, 485f to 150f, 560f to 115f, 620f to 145f, 675f to 140f, 700f to 235f, 850f to 190f, 900f to 300f, 825f to 320f, 760f to 320f, 650f to 410f, 570f to 490f, 520f to 500f, 470f to 350f, 430f to 285f)),
        sector("san-perfecto", "San Perfecto", 105f, 290f, color = 0xFFDEE4AC, points = listOf(55f to 275f, 95f to 265f, 120f to 295f, 90f to 325f, 55f to 335f, 40f to 305f)),
        sector("progress", "Progress", 45f, 315f, color = 0xFFDDBFA8, points = listOf(40f to 305f, 55f to 335f, 30f to 350f, 20f to 315f)),
        sector("batis", "Batis", 135f, 375f, color = 0xFFBDC8DE, points = listOf(90f to 325f, 120f to 295f, 165f to 285f, 200f to 330f, 215f to 375f, 195f to 430f, 130f to 490f, 110f to 455f, 85f to 410f, 55f to 360f)),
        sector("isabelita", "Isabelita", 245f, 305f, color = 0xFFE0DCB3, points = listOf(215f to 250f, 275f to 270f, 270f to 305f, 230f to 320f, 205f to 300f)),
        sector("halo-halo", "Halo-Halo", 300f, 300f, color = 0xFFCAD0E2, points = listOf(270f to 270f, 330f to 255f, 325f to 290f, 300f to 315f, 270f to 305f)),
        sector("onse", "Onse", 290f, 345f, color = 0xFFC2D0E6, points = listOf(230f to 320f, 270f to 305f, 300f to 315f, 305f to 345f, 280f to 370f, 245f to 365f)),
        sector("tibagan", "Tibagan", 220f, 375f, color = 0xFFDABFB0, points = listOf(205f to 300f, 230f to 320f, 245f to 365f, 235f to 425f, 200f to 430f, 185f to 370f)),
        sector("kabayanan", "Kabayanan", 250f, 435f, color = 0xFFE6D8A7, points = listOf(200f to 430f, 235f to 425f, 280f to 370f, 320f to 400f, 300f to 455f, 255f to 480f, 210f to 470f)),
        sector("santa-lucia", "Santa Lucia", 355f, 410f, color = 0xFFF2EEA8, points = listOf(320f to 400f, 380f to 390f, 430f to 350f, 470f to 350f, 480f to 410f, 450f to 450f, 380f to 470f, 300f to 455f)),
        sector("little-baguio", "Little Baguio", 430f, 345f, color = 0xFFDCC0B8, points = listOf(325f to 290f, 365f to 280f, 430f to 285f, 470f to 315f, 485f to 350f, 470f to 375f, 430f to 350f, 380f to 390f, 320f to 400f, 305f to 345f)),
        sector("maytunas", "Maytunas", 315f, 500f, color = 0xFFC9BED8, points = listOf(255f to 480f, 300f to 455f, 380f to 470f, 365f to 520f, 325f to 540f, 260f to 535f, 235f to 510f)),
        sector("addition-hills", "Addition Hills", 470f, 510f, color = 0xFFE4D6A9, points = listOf(365f to 520f, 450f to 450f, 520f to 500f, 545f to 560f, 515f to 585f, 460f to 580f, 395f to 575f, 345f to 605f, 315f to 565f, 260f to 535f, 325f to 540f))
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
