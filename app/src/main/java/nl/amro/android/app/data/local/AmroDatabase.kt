package nl.amro.android.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import nl.amro.android.app.data.local.dao.GenreDao
import nl.amro.android.app.data.local.dao.MovieDao
import nl.amro.android.app.data.local.dao.MovieDetailsDao
import nl.amro.android.app.data.local.entity.GenreEntity
import nl.amro.android.app.data.local.entity.MovieDetailsEntity
import nl.amro.android.app.data.local.entity.MovieEntity

/**
 * Room database for caching movie data.
 */
@Database(
    entities = [
        MovieEntity::class,
        MovieDetailsEntity::class,
        GenreEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AmroDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun movieDetailsDao(): MovieDetailsDao
    abstract fun genreDao(): GenreDao

    companion object {
        const val DATABASE_NAME = "amro_movies_db"
    }
}
