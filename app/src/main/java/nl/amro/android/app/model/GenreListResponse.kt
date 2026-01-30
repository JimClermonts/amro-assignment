package nl.amro.android.app.model

import kotlinx.serialization.Serializable

/**
 * Response model for genre list API endpoint.
 * GET /genre/movie/list
 */
@Serializable
data class GenreListResponse(
    val genres: List<Genre>
)
