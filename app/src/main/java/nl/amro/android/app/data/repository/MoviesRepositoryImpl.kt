package nl.amro.android.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import nl.amro.android.app.data.datasource.LocalDataSource
import nl.amro.android.app.data.datasource.RemoteDataSource
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.model.Result
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MoviesRepository.
 * Follows offline-first approach: emit cached data first, then fetch fresh data from API.
 * 
 * Pattern inspired by Google I/O iosched ConferenceDataRepository.
 */
@Singleton
class MoviesRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : MoviesRepository {

    /**
     * Get trending movies as a Flow.
     * Strategy:
     * 1. Emit Loading state
     * 2. If cache exists, emit cached data first (fast)
     * 3. Fetch from API
     * 4. If API succeeds, save to cache and emit fresh data
     * 5. If API fails and cache exists, continue with cached data
     * 6. If API fails and no cache, emit error
     */
    override fun getTrendingMovies(): Flow<Result<List<MovieDto>>> = flow {
        emit(Result.Loading)

        // First, emit cached data if available (fast response)
        val cachedMovies = localDataSource.getMovies()
        if (cachedMovies.isNotEmpty()) {
            emit(Result.Success(cachedMovies))
        }

        // Then fetch fresh data from API
        when (val remoteResult = remoteDataSource.getTrendingMovies()) {
            is Result.Success -> {
                // Save to cache
                localDataSource.clearMovies()
                localDataSource.saveMovies(remoteResult.data)
                // Emit fresh data
                emit(Result.Success(remoteResult.data))
            }
            is Result.Error -> {
                // If we already emitted cached data, don't emit error
                // If no cache was available, emit error
                if (cachedMovies.isEmpty()) {
                    emit(Result.Error(remoteResult.exception))
                }
                // Otherwise, we continue with the cached data already emitted
            }
            is Result.Loading -> { /* Already emitted Loading */ }
        }
    }

    /**
     * Get movie details as a Flow.
     * Strategy:
     * 1. Emit Loading state
     * 2. If cached details exist, emit them first (fast)
     * 3. Fetch from API
     * 4. If API succeeds, save to cache and emit fresh data
     * 5. If API fails and cache exists, continue with cached data
     * 6. If API fails and no cache, emit error
     */
    override fun getMovieDetails(movieId: Int): Flow<Result<MovieDetails>> = flow {
        emit(Result.Loading)

        // First, check for cached details
        val cachedDetails = localDataSource.getMovieDetails(movieId)
        if (cachedDetails != null) {
            emit(Result.Success(cachedDetails))
        }

        // Then fetch fresh data from API
        when (val remoteResult = remoteDataSource.getMovieDetails(movieId)) {
            is Result.Success -> {
                // Save to cache
                localDataSource.saveMovieDetails(remoteResult.data)
                // Emit fresh data
                emit(Result.Success(remoteResult.data))
            }
            is Result.Error -> {
                // If we already emitted cached data, don't emit error
                // If no cache was available, emit error
                if (cachedDetails == null) {
                    emit(Result.Error(remoteResult.exception))
                }
            }
            is Result.Loading -> { /* Already emitted Loading */ }
        }
    }

    /**
     * Get genres as a Flow.
     * Strategy:
     * 1. Emit Loading state
     * 2. If cached genres exist, emit them first
     * 3. Fetch from API (only if not cached, to reduce API calls)
     * 4. Save and emit fresh data
     */
    override fun getGenres(): Flow<Result<List<Genre>>> = flow {
        emit(Result.Loading)

        // First, check for cached genres
        val cachedGenres = localDataSource.getGenres()
        if (cachedGenres.isNotEmpty()) {
            emit(Result.Success(cachedGenres))
        }

        // Fetch from API if no cache
        if (cachedGenres.isEmpty()) {
            when (val remoteResult = remoteDataSource.getGenres()) {
                is Result.Success -> {
                    localDataSource.saveGenres(remoteResult.data)
                    emit(Result.Success(remoteResult.data))
                }
                is Result.Error -> {
                    emit(Result.Error(remoteResult.exception))
                }
                is Result.Loading -> { /* Already emitted Loading */ }
            }
        }
    }

    /**
     * Force refresh movies from API.
     */
    override suspend fun refreshMovies(): Result<List<MovieDto>> {
        return when (val remoteResult = remoteDataSource.getTrendingMovies()) {
            is Result.Success -> {
                localDataSource.clearMovies()
                localDataSource.saveMovies(remoteResult.data)
                Result.Success(remoteResult.data)
            }
            is Result.Error -> remoteResult
            is Result.Loading -> Result.Loading
        }
    }

    /**
     * Get genres for a list of IDs using cached data.
     */
    override suspend fun getGenresByIds(genreIds: List<Int>): List<Genre> {
        return localDataSource.getGenresByIds(genreIds)
    }
}
