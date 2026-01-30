package nl.amro.android.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import nl.amro.android.app.data.local.entity.GenreEntity

/**
 * Data Access Object for genre operations.
 */
@Dao
interface GenreDao {

    /**
     * Get all genres as a Flow for reactive updates.
     */
    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenres(): Flow<List<GenreEntity>>

    /**
     * Get all genres (one-time read).
     */
    @Query("SELECT * FROM genres ORDER BY name ASC")
    suspend fun getAllGenresOnce(): List<GenreEntity>

    /**
     * Get a genre by ID.
     */
    @Query("SELECT * FROM genres WHERE id = :genreId")
    suspend fun getGenreById(genreId: Int): GenreEntity?

    /**
     * Get genres by list of IDs.
     */
    @Query("SELECT * FROM genres WHERE id IN (:genreIds)")
    suspend fun getGenresByIds(genreIds: List<Int>): List<GenreEntity>

    /**
     * Insert or replace genres.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    /**
     * Delete all genres from cache.
     */
    @Query("DELETE FROM genres")
    suspend fun deleteAllGenres()

    /**
     * Get count of cached genres.
     */
    @Query("SELECT COUNT(*) FROM genres")
    suspend fun getGenreCount(): Int
}
