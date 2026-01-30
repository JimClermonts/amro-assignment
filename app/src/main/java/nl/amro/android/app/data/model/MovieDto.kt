package nl.amro.android.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Movie data transfer object matching TMDB API response.
 * Used for both trending list and basic movie info.
 */
@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    @SerialName("original_title")
    val originalTitle: String? = null,
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("genre_ids")
    val genreIds: List<Int> = emptyList(),
    val popularity: Double = 0.0,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    val adult: Boolean = false,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    val video: Boolean = false
)
