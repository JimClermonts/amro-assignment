package nl.amro.android.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nl.amro.android.app.data.local.entity.MovieEntity

/**
 * Data Access Object for movie list operations.
 */
@Dao
interface MovieDao {

    /**
     * Get all cached movies as a Flow for reactive updates.
     */
    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    /**
     * Get all cached movies (non-Flow version for one-time reads).
     */
    @Query("SELECT * FROM movies ORDER BY popularity DESC")
    suspend fun getAllMoviesOnce(): List<MovieEntity>

    /**
     * Get a single movie by ID.
     */
    @Query("SELECT * FROM movies WHERE id = :movieId")
    suspend fun getMovieById(movieId: Int): MovieEntity?

    /**
     * Insert or replace movies.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    /**
     * Delete all movies from cache.
     */
    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()

    /**
     * Get count of cached movies.
     */
    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int
}
