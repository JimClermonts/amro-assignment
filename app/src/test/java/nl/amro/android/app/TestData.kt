package nl.amro.android.app

import nl.amro.android.app.data.local.entity.GenreEntity
import nl.amro.android.app.data.local.entity.MovieDetailsEntity
import nl.amro.android.app.data.local.entity.MovieEntity
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto

/**
 * Test data factory for unit tests.
 */
object TestData {

    fun createMovieDto(
        id: Int = 1,
        title: String = "Test Movie",
        posterPath: String? = "/test.jpg",
        genreIds: List<Int> = listOf(28, 12),
        popularity: Double = 100.0,
        releaseDate: String? = "2024-01-15",
        voteAverage: Double = 7.5,
        voteCount: Int = 1000
    ) = MovieDto(
        id = id,
        title = title,
        posterPath = posterPath,
        genreIds = genreIds,
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount
    )

    fun createMovieEntity(
        id: Int = 1,
        title: String = "Test Movie",
        posterPath: String? = "/test.jpg",
        genreIds: String = "28,12",
        popularity: Double = 100.0,
        releaseDate: String? = "2024-01-15",
        voteAverage: Double = 7.5,
        voteCount: Int = 1000
    ) = MovieEntity(
        id = id,
        title = title,
        posterPath = posterPath,
        genreIds = genreIds,
        popularity = popularity,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount
    )

    fun createMovieDetails(
        id: Int = 1,
        title: String = "Test Movie",
        tagline: String? = "Test Tagline",
        overview: String? = "Test Overview",
        posterPath: String? = "/test.jpg",
        genres: List<Genre> = listOf(Genre(28, "Action"), Genre(12, "Adventure")),
        popularity: Double = 100.0,
        releaseDate: String? = "2024-01-15",
        voteAverage: Double = 7.5,
        voteCount: Int = 1000,
        budget: Long = 100000000,
        revenue: Long = 500000000,
        runtime: Int? = 120,
        status: String? = "Released",
        imdbId: String? = "tt1234567"
    ) = MovieDetails(
        id = id,
        title = title,
        tagline = tagline,
        overview = overview,
        posterPath = posterPath,
        genres = genres,
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

    fun createMovieDetailsEntity(
        id: Int = 1,
        title: String = "Test Movie",
        tagline: String? = "Test Tagline",
        overview: String? = "Test Overview",
        posterPath: String? = "/test.jpg",
        genres: String = """[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]""",
        popularity: Double = 100.0,
        releaseDate: String? = "2024-01-15",
        voteAverage: Double = 7.5,
        voteCount: Int = 1000,
        budget: Long = 100000000,
        revenue: Long = 500000000,
        runtime: Int? = 120,
        status: String? = "Released",
        imdbId: String? = "tt1234567"
    ) = MovieDetailsEntity(
        id = id,
        title = title,
        tagline = tagline,
        overview = overview,
        posterPath = posterPath,
        backdropPath = null,
        genres = genres,
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

    fun createGenre(id: Int = 28, name: String = "Action") = Genre(id, name)

    fun createGenreEntity(id: Int = 28, name: String = "Action") = GenreEntity(id, name)

    // Sample movie lists for testing
    val sampleMovies = listOf(
        createMovieDto(id = 1, title = "Alpha Movie", genreIds = listOf(28), popularity = 50.0, releaseDate = "2024-03-01"),
        createMovieDto(id = 2, title = "Beta Movie", genreIds = listOf(35), popularity = 100.0, releaseDate = "2024-01-01"),
        createMovieDto(id = 3, title = "Gamma Movie", genreIds = listOf(28, 12), popularity = 75.0, releaseDate = "2024-02-01"),
        createMovieDto(id = 4, title = "Delta Movie", genreIds = listOf(18), popularity = 25.0, releaseDate = "2023-12-01"),
        createMovieDto(id = 5, title = "Epsilon Movie", genreIds = listOf(35, 10749), popularity = 150.0, releaseDate = "2024-04-01")
    )

    val sampleGenres = listOf(
        Genre(28, "Action"),
        Genre(12, "Adventure"),
        Genre(35, "Comedy"),
        Genre(18, "Drama"),
        Genre(10749, "Romance")
    )
}
