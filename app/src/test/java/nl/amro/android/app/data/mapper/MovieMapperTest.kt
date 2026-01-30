package nl.amro.android.app.data.mapper

import nl.amro.android.app.TestData
import nl.amro.android.app.data.mapper.MovieMapper.toDto
import nl.amro.android.app.data.mapper.MovieMapper.toDtos
import nl.amro.android.app.data.mapper.MovieMapper.toEntities
import nl.amro.android.app.data.mapper.MovieMapper.toEntity
import nl.amro.android.app.data.mapper.MovieMapper.toGenreDtos
import nl.amro.android.app.model.Genre
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieMapperTest {

    // MovieDto <-> MovieEntity tests

    @Test
    fun `MovieDto toEntity converts correctly`() {
        // Given
        val movieDto = TestData.createMovieDto(
            id = 1,
            title = "Test Movie",
            genreIds = listOf(28, 12, 35)
        )

        // When
        val entity = movieDto.toEntity()

        // Then
        assertEquals(movieDto.id, entity.id)
        assertEquals(movieDto.title, entity.title)
        assertEquals(movieDto.posterPath, entity.posterPath)
        assertEquals("28,12,35", entity.genreIds)
        assertEquals(movieDto.popularity, entity.popularity, 0.01)
        assertEquals(movieDto.releaseDate, entity.releaseDate)
    }

    @Test
    fun `MovieEntity toDto converts correctly`() {
        // Given
        val entity = TestData.createMovieEntity(
            id = 1,
            title = "Test Movie",
            genreIds = "28,12,35"
        )

        // When
        val dto = entity.toDto()

        // Then
        assertEquals(entity.id, dto.id)
        assertEquals(entity.title, dto.title)
        assertEquals(entity.posterPath, dto.posterPath)
        assertEquals(listOf(28, 12, 35), dto.genreIds)
        assertEquals(entity.popularity, dto.popularity, 0.01)
        assertEquals(entity.releaseDate, dto.releaseDate)
    }

    @Test
    fun `MovieEntity with empty genreIds converts to empty list`() {
        // Given
        val entity = TestData.createMovieEntity(genreIds = "")

        // When
        val dto = entity.toDto()

        // Then
        assertTrue(dto.genreIds.isEmpty())
    }

    @Test
    fun `List of MovieDto toEntities converts all items`() {
        // Given
        val dtos = TestData.sampleMovies

        // When
        val entities = dtos.toEntities()

        // Then
        assertEquals(dtos.size, entities.size)
        dtos.forEachIndexed { index, dto ->
            assertEquals(dto.id, entities[index].id)
            assertEquals(dto.title, entities[index].title)
        }
    }

    @Test
    fun `List of MovieEntity toDtos converts all items`() {
        // Given
        val entities = listOf(
            TestData.createMovieEntity(id = 1, title = "Movie 1"),
            TestData.createMovieEntity(id = 2, title = "Movie 2")
        )

        // When
        val dtos = entities.toDtos()

        // Then
        assertEquals(entities.size, dtos.size)
        entities.forEachIndexed { index, entity ->
            assertEquals(entity.id, dtos[index].id)
            assertEquals(entity.title, dtos[index].title)
        }
    }

    // MovieDetails <-> MovieDetailsEntity tests

    @Test
    fun `MovieDetails toEntity converts genres to JSON`() {
        // Given
        val details = TestData.createMovieDetails(
            genres = listOf(Genre(28, "Action"), Genre(12, "Adventure"))
        )

        // When
        val entity = details.toEntity()

        // Then
        assertEquals(details.id, entity.id)
        assertEquals(details.title, entity.title)
        assertEquals(details.tagline, entity.tagline)
        assertEquals(details.budget, entity.budget)
        assertEquals(details.runtime, entity.runtime)
        assertTrue(entity.genres.contains("Action"))
        assertTrue(entity.genres.contains("Adventure"))
    }

    @Test
    fun `MovieDetailsEntity toDto parses genres from JSON`() {
        // Given
        val entity = TestData.createMovieDetailsEntity(
            genres = """[{"id":28,"name":"Action"},{"id":12,"name":"Adventure"}]"""
        )

        // When
        val dto = entity.toDto()

        // Then
        assertEquals(entity.id, dto.id)
        assertEquals(entity.title, dto.title)
        assertEquals(2, dto.genres.size)
        assertEquals("Action", dto.genres[0].name)
        assertEquals("Adventure", dto.genres[1].name)
    }

    @Test
    fun `MovieDetailsEntity with invalid JSON returns empty genres`() {
        // Given
        val entity = TestData.createMovieDetailsEntity(genres = "invalid json")

        // When
        val dto = entity.toDto()

        // Then
        assertTrue(dto.genres.isEmpty())
    }

    // Genre <-> GenreEntity tests

    @Test
    fun `Genre toEntity converts correctly`() {
        // Given
        val genre = TestData.createGenre(id = 28, name = "Action")

        // When
        val entity = genre.toEntity()

        // Then
        assertEquals(genre.id, entity.id)
        assertEquals(genre.name, entity.name)
    }

    @Test
    fun `GenreEntity toDto converts correctly`() {
        // Given
        val entity = TestData.createGenreEntity(id = 28, name = "Action")

        // When
        val dto = entity.toDto()

        // Then
        assertEquals(entity.id, dto.id)
        assertEquals(entity.name, dto.name)
    }

    @Test
    fun `List of Genre toEntities converts all items`() {
        // Given
        val genres = TestData.sampleGenres

        // When
        val entities = genres.toEntities()

        // Then
        assertEquals(genres.size, entities.size)
    }

    @Test
    fun `List of GenreEntity toGenreDtos converts all items`() {
        // Given
        val entities = listOf(
            TestData.createGenreEntity(id = 28, name = "Action"),
            TestData.createGenreEntity(id = 35, name = "Comedy")
        )

        // When
        val dtos = entities.toGenreDtos()

        // Then
        assertEquals(entities.size, dtos.size)
    }
}
