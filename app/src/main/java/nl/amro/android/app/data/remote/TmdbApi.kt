package nl.amro.android.app.data.remote

import nl.amro.android.app.model.GenreListResponse
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.model.Result

/**
 * Interface for TMDB API operations.
 * Allows for easy testing by providing mock implementations.
 */
interface TmdbApi {

    /**
     * Fetches the top 100 trending movies of the week.
     *
     * @return Result containing list of MovieDto or error
     */
    suspend fun getTrendingMovies(): Result<List<MovieDto>>

    /**
     * Fetches detailed information for a specific movie.
     *
     * @param movieId The TMDB movie ID
     * @return Result containing MovieDetails or error
     */
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails>

    /**
     * Fetches the list of official movie genres.
     *
     * @return Result containing GenreListResponse or error
     */
    suspend fun getGenres(): Result<GenreListResponse>
}
