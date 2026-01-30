package nl.amro.android.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Detailed movie information from movie details endpoint.
 * GET /movie/{movie_id}
 */
@Serializable
data class MovieDetails(
    val id: Int,
    val title: String,
    @SerialName("original_title")
    val originalTitle: String? = null,
    val tagline: String? = null,
    val overview: String? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    val genres: List<Genre> = emptyList(),
    val popularity: Double = 0.0,
    @SerialName("release_date")
    val releaseDate: String? = null,
    @SerialName("vote_average")
    val voteAverage: Double = 0.0,
    @SerialName("vote_count")
    val voteCount: Int = 0,
    val budget: Long = 0,
    val revenue: Long = 0,
    val runtime: Int? = null,
    val status: String? = null,
    @SerialName("imdb_id")
    val imdbId: String? = null,
    val homepage: String? = null,
    val adult: Boolean = false,
    @SerialName("original_language")
    val originalLanguage: String? = null,
    val video: Boolean = false,
    @SerialName("production_companies")
    val productionCompanies: List<ProductionCompany> = emptyList(),
    @SerialName("production_countries")
    val productionCountries: List<ProductionCountry> = emptyList(),
    @SerialName("spoken_languages")
    val spokenLanguages: List<SpokenLanguage> = emptyList()
)
