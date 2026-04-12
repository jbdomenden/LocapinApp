package com.locapin.mobile.domain.model

import com.google.firebase.firestore.PropertyName

data class FirebaseAttraction(
    @get:PropertyName("id") @set:PropertyName("id") var id: String = "",
    @get:PropertyName("name") @set:PropertyName("name") var name: String = "",
    @get:PropertyName("knownFor") @set:PropertyName("knownFor") var knownFor: String = "",
    @get:PropertyName("distance") @set:PropertyName("distance") var distance: String = "",
    @get:PropertyName("imageUrl") @set:PropertyName("imageUrl") var imageUrl: String = "",
    @get:PropertyName("sectorId") @set:PropertyName("sectorId") var sectorId: String = "",
    @get:PropertyName("description") @set:PropertyName("description") var description: String = ""
) {
    fun toAttraction(): com.locapin.mobile.feature.map.Attraction {
        return com.locapin.mobile.feature.map.Attraction(
            name = name,
            knownFor = knownFor,
            distance = distance,
            imageUrl = imageUrl
        )
    }
}
