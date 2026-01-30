package nl.amro.android.app.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nl.amro.android.app.TestData
import nl.amro.android.app.model.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FilterMoviesUseCaseTest {

    private lateinit var filterMoviesUseCase: FilterMoviesUseCase

    @Before
    fun setup() {
        filterMoviesUseCase = FilterMoviesUseCase(Dispatchers.Unconfined)
    }

    @Test
    fun `when no genres selected, returns all movies`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        val selectedGenres = emptySet<Int>()

        // When
        val result = filterMoviesUseCase(FilterMoviesUseCase.Params(movies, selectedGenres))

        // Then
        assertTrue(result is Result.Success)
        assertEquals(movies.size, (result as Result.Success).data.size)
        assertEquals(movies, result.data)
    }

    @Test
    fun `when one genre selected, returns only movies with that genre`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        val selectedGenres = setOf(28) // Action

        // When
        val result = filterMoviesUseCase(FilterMoviesUseCase.Params(movies, selectedGenres))

        // Then
        assertTrue(result is Result.Success)
        val filteredMovies = (result as Result.Success).data
        assertEquals(2, filteredMovies.size) // Alpha Movie and Gamma Movie have Action genre
        assertTrue(filteredMovies.all { movie -> movie.genreIds.contains(28) })
    }

    @Test
    fun `when multiple genres selected, returns movies with any of those genres`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        val selectedGenres = setOf(28, 35) // Action and Comedy

        // When
        val result = filterMoviesUseCase(FilterMoviesUseCase.Params(movies, selectedGenres))

        // Then
        assertTrue(result is Result.Success)
        val filteredMovies = (result as Result.Success).data
        assertEquals(4, filteredMovies.size) // Alpha, Beta, Gamma, Epsilon
        assertTrue(filteredMovies.all { movie ->
            movie.genreIds.any { it in selectedGenres }
        })
    }

    @Test
    fun `when selected genre has no movies, returns empty list`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        val selectedGenres = setOf(9999) // Non-existent genre

        // When
        val result = filterMoviesUseCase(FilterMoviesUseCase.Params(movies, selectedGenres))

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }

    @Test
    fun `when movies list is empty, returns empty list`() = runTest {
        // Given
        val movies = emptyList<nl.amro.android.app.model.MovieDto>()
        val selectedGenres = setOf(28)

        // When
        val result = filterMoviesUseCase(FilterMoviesUseCase.Params(movies, selectedGenres))

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
}
