package nl.amro.android.app.model

import kotlinx.serialization.Serializable

/**
 * Genre data transfer object.
 */
@Serializable
data class Genre(
    val id: Int,
    val name: String
)
