package nl.amro.android.app.data.model

import kotlinx.serialization.Serializable

/**
 * Genre data transfer object.
 */
@Serializable
data class Genre(
    val id: Int,
    val name: String
)
