package nl.amro.android.app.data.datasource

import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.data.remote.TmdbApi
import nl.amro.android.app.model.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for TMDB API operations.
 * Wraps the TmdbApi for consistent data access.
 */
@Singleton
class RemoteDataSource @Inject constructor(
    private val tmdbApi: TmdbApi
) {
    /**
     * Fetch trending movies from the API.
     */
    suspend fun getTrendingMovies(): Result<List<MovieDto>> =
        tmdbApi.getTrendingMovies()

    /**
     * Fetch movie details from the API.
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> =
        tmdbApi.getMovieDetails(movieId)

    /**
     * Fetch genres from the API.
     */
    suspend fun getGenres(): Result<List<Genre>> {
        return when (val result = tmdbApi.getGenres()) {
            is Result.Success -> Result.Success(result.data.genres)
            is Result.Error -> result
            is Result.Loading -> Result.Loading
        }
    }
}
