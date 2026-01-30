package nl.amro.android.app.data.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nl.amro.android.app.data.local.AmroDatabase
import nl.amro.android.app.data.local.dao.GenreDao
import nl.amro.android.app.data.local.dao.MovieDao
import nl.amro.android.app.data.local.dao.MovieDetailsDao
import nl.amro.android.app.data.remote.TmdbApi
import nl.amro.android.app.data.remote.TmdbApiService
import nl.amro.android.app.data.repository.MoviesRepository
import nl.amro.android.app.data.repository.MoviesRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AmroDatabase {
        return Room.databaseBuilder(
            context,
            AmroDatabase::class.java,
            AmroDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideMovieDao(database: AmroDatabase): MovieDao = database.movieDao()

    @Provides
    fun provideMovieDetailsDao(database: AmroDatabase): MovieDetailsDao = database.movieDetailsDao()

    @Provides
    fun provideGenreDao(database: AmroDatabase): GenreDao = database.genreDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMoviesRepository(impl: MoviesRepositoryImpl): MoviesRepository

    @Binds
    @Singleton
    abstract fun bindTmdbApi(impl: TmdbApiService): TmdbApi
}
