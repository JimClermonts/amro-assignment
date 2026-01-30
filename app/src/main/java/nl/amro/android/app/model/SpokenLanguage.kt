package nl.amro.android.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Spoken language data transfer object.
 */
@Serializable
data class SpokenLanguage(
    @SerialName("english_name")
    val englishName: String? = null,
    @SerialName("iso_639_1")
    val iso6391: String,
    val name: String
)
