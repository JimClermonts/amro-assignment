package nl.amro.android.app.data.mapper

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.amro.android.app.data.local.entity.GenreEntity
import nl.amro.android.app.data.local.entity.MovieDetailsEntity
import nl.amro.android.app.data.local.entity.MovieEntity
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto

/**
 * Mapper functions for converting between API DTOs and Room entities.
 */
object MovieMapper {

    private val json = Json { ignoreUnknownKeys = true }

    // MovieDto -> MovieEntity
    fun MovieDto.toEntity(): MovieEntity = MovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        genreIds = genreIds.joinToString(","),
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount
    )

    // MovieEntity -> MovieDto
    fun MovieEntity.toDto(): MovieDto = MovieDto(
        id = id,
        title = title,
        posterPath = posterPath,
        genreIds = if (genreIds.isBlank()) emptyList() else genreIds.split(",").map { it.toInt() },
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount
    )

    // MovieDetails -> MovieDetailsEntity
    fun MovieDetails.toEntity(): MovieDetailsEntity = MovieDetailsEntity(
        id = id,
        title = title,
        tagline = tagline,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        genres = json.encodeToString(genres),
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        budget = budget,
        revenue = revenue,
        runtime = runtime,
        status = status,
        imdbId = imdbId
    )

    // MovieDetailsEntity -> MovieDetails
    fun MovieDetailsEntity.toDto(): MovieDetails = MovieDetails(
        id = id,
        title = title,
        tagline = tagline,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        genres = try {
            json.decodeFromString<List<Genre>>(genres)
        } catch (e: Exception) {
            emptyList()
        },
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        budget = budget,
        revenue = revenue,
        runtime = runtime,
        status = status,
        imdbId = imdbId
    )

    // Genre -> GenreEntity
    fun Genre.toEntity(): GenreEntity = GenreEntity(
        id = id,
        name = name
    )

    // GenreEntity -> Genre
    fun GenreEntity.toDto(): Genre = Genre(
        id = id,
        name = name
    )

    // List extensions
    @JvmName("movieDtosToEntities")
    fun List<MovieDto>.toEntities(): List<MovieEntity> = map { it.toEntity() }
    
    @JvmName("movieEntitiesToDtos")
    fun List<MovieEntity>.toDtos(): List<MovieDto> = map { it.toDto() }
    
    @JvmName("genresToEntities")
    fun List<Genre>.toEntities(): List<GenreEntity> = map { it.toEntity() }
    
    @JvmName("genreEntitiesToDtos")
    fun List<GenreEntity>.toGenreDtos(): List<Genre> = map { it.toDto() }
}
