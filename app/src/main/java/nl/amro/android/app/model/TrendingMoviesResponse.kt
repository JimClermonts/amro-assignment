package nl.amro.android.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response model for trending movies API endpoint.
 * GET /trending/movie/week
 */
@Serializable
data class TrendingMoviesResponse(
    val page: Int,
    val results: List<MovieDto>,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_results")
    val totalResults: Int
)
