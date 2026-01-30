package nl.amro.android.app.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import nl.amro.android.app.data.local.dao.GenreDao
import nl.amro.android.app.data.local.dao.MovieDao
import nl.amro.android.app.data.local.dao.MovieDetailsDao
import nl.amro.android.app.data.mapper.MovieMapper.toDto
import nl.amro.android.app.data.mapper.MovieMapper.toDtos
import nl.amro.android.app.data.mapper.MovieMapper.toEntities
import nl.amro.android.app.data.mapper.MovieMapper.toEntity
import nl.amro.android.app.data.mapper.MovieMapper.toGenreDtos
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local data source for Room database operations.
 * Provides cached movie data for offline support.
 */
@Singleton
class LocalDataSource @Inject constructor(
    private val movieDao: MovieDao,
    private val movieDetailsDao: MovieDetailsDao,
    private val genreDao: GenreDao
) {
    // Movies
    fun getMoviesFlow(): Flow<List<MovieDto>> =
        movieDao.getAllMovies().map { it.toDtos() }

    suspend fun getMovies(): List<MovieDto> =
        movieDao.getAllMoviesOnce().toDtos()

    suspend fun saveMovies(movies: List<MovieDto>) {
        movieDao.insertMovies(movies.toEntities())
    }

    suspend fun clearMovies() {
        movieDao.deleteAllMovies()
    }

    suspend fun hasMovies(): Boolean =
        movieDao.getMovieCount() > 0

    // Movie Details
    fun getMovieDetailsFlow(movieId: Int): Flow<MovieDetails?> =
        movieDetailsDao.getMovieDetailsFlow(movieId).map { it?.toDto() }

    suspend fun getMovieDetails(movieId: Int): MovieDetails? =
        movieDetailsDao.getMovieDetails(movieId)?.toDto()

    suspend fun saveMovieDetails(movieDetails: MovieDetails) {
        movieDetailsDao.insertMovieDetails(movieDetails.toEntity())
    }

    // Genres
    fun getGenresFlow(): Flow<List<Genre>> =
        genreDao.getAllGenres().map { it.toGenreDtos() }

    suspend fun getGenres(): List<Genre> =
        genreDao.getAllGenresOnce().toGenreDtos()

    suspend fun saveGenres(genres: List<Genre>) {
        genreDao.insertGenres(genres.toEntities())
    }

    suspend fun hasGenres(): Boolean =
        genreDao.getGenreCount() > 0

    suspend fun getGenresByIds(genreIds: List<Int>): List<Genre> =
        genreDao.getGenresByIds(genreIds).toGenreDtos()
}
