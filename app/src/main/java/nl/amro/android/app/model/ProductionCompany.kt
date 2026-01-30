package nl.amro.android.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Production company data transfer object.
 */
@Serializable
data class ProductionCompany(
    val id: Int,
    val name: String,
    @SerialName("logo_path")
    val logoPath: String? = null,
    @SerialName("origin_country")
    val originCountry: String? = null
)
