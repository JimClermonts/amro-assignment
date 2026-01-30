package nl.amro.android.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nl.amro.android.app.data.local.entity.MovieDetailsEntity

/**
 * Data Access Object for movie details operations.
 */
@Dao
interface MovieDetailsDao {

    /**
     * Get movie details by ID as a Flow for reactive updates.
     */
    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    fun getMovieDetailsFlow(movieId: Int): Flow<MovieDetailsEntity?>

    /**
     * Get movie details by ID (one-time read).
     */
    @Query("SELECT * FROM movie_details WHERE id = :movieId")
    suspend fun getMovieDetails(movieId: Int): MovieDetailsEntity?

    /**
     * Insert or replace movie details.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovieDetails(movieDetails: MovieDetailsEntity)

    /**
     * Delete movie details by ID.
     */
    @Query("DELETE FROM movie_details WHERE id = :movieId")
    suspend fun deleteMovieDetails(movieId: Int)

    /**
     * Delete all movie details from cache.
     */
    @Query("DELETE FROM movie_details")
    suspend fun deleteAllMovieDetails()
}
