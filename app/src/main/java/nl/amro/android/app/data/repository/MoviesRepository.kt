package nl.amro.android.app.data.repository

import kotlinx.coroutines.flow.Flow
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.model.Result

/**
 * Repository interface for movie data operations.
 * Provides an abstraction layer between data sources and the domain layer.
 */
interface MoviesRepository {

    /**
     * Get trending movies as a Flow.
     * Emits cached data first (if available), then fetches fresh data from API.
     * If API call fails and cache exists, continues with cached data.
     */
    fun getTrendingMovies(): Flow<Result<List<MovieDto>>>

    /**
     * Get movie details as a Flow.
     * Emits cached data first (if available), then fetches fresh data from API.
     * Saves fetched data to cache for offline access.
     *
     * @param movieId The TMDB movie ID
     */
    fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>>

    /**
     * Get all genres as a Flow.
     * Emits cached data first (if available), then fetches fresh data from API on first open.
     */
    fun getGenres(): Flow<Result<List<Genre>>>
}
