package nl.amro.android.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Production country data transfer object.
 */
@Serializable
data class ProductionCountry(
    @SerialName("iso_3166_1")
    val iso31661: String,
    val name: String
)
