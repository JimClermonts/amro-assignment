package nl.amro.android.app.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import nl.amro.android.app.TestData
import nl.amro.android.app.model.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SortMoviesUseCaseTest {

    private lateinit var sortMoviesUseCase: SortMoviesUseCase

    @Before
    fun setup() {
        sortMoviesUseCase = SortMoviesUseCase(Dispatchers.Unconfined)
    }

    @Test
    fun `sort by popularity descending returns highest popularity first`() = runTest {
        // Given
        val movies = TestData.sampleMovies

        // When
        val result = sortMoviesUseCase(SortMoviesUseCase.Params(movies, SortOption.POPULARITY_DESC))

        // Then
        assertTrue(result is Result.Success)
        val sortedMovies = (result as Result.Success).data
        assertEquals("Epsilon Movie", sortedMovies[0].title) // popularity 150
        assertEquals("Beta Movie", sortedMovies[1].title)    // popularity 100
        assertEquals("Gamma Movie", sortedMovies[2].title)   // popularity 75
        assertEquals("Alpha Movie", sortedMovies[3].title)   // popularity 50
        assertEquals("Delta Movie", sortedMovies[4].title)   // popularity 25
    }

    @Test
    fun `sort by popularity ascending returns lowest popularity first`() = runTest {
        // Given
        val movies = TestData.sampleMovies

        // When
        val result = sortMoviesUseCase(SortMoviesUseCase.Params(movies, SortOption.POPULARITY_ASC))

        // Then
        assertTrue(result is Result.Success)
        val sortedMovies = (result as Result.Success).data
        assertEquals("Delta Movie", sortedMovies[0].title)   // popularity 25
        assertEquals("Alpha Movie", sortedMovies[1].title)   // popularity 50
    }

    @Test
    fun `sort by title A-Z returns alphabetical order`() = runTest {
        // Given
        val movies = TestData.sampleMovies

        // When
        val result = sortMoviesUseCase(SortMoviesUseCase.Params(movies, SortOption.TITLE_ASC))

        // Then
        assertTrue(result is Result.Success)
        val sortedMovies = (result as Result.Success).data
        assertEquals("Alpha Movie", sortedMovies[0].title)
        assertEquals("Beta Movie", sortedMovies[1].title)
        assertEquals("Delta Movie", sortedMovies[2].title)
        assertEquals("Epsilon Movie", sortedMovies[3].title)
        assertEquals("Gamma Movie", sortedMovies[4].title)
    }

    @Test
    fun `sort by title Z-A returns reverse alphabetical order`() = runTest {
        // Given
        val movies = TestData.sampleMovies

        // When
        val result = sortMoviesUseCase(SortMoviesUseCase.Params(movies, SortOption.TITLE_DESC))

        // Then
        assertTrue(result is Result.Success)
        val sortedMovies = (result as Result.Success).data
        assertEquals("Gamma Movie", sortedMovies[0].title)
        assertEquals("Epsilon Movie", sortedMovies[1].title)
    }

    @Test
    fun `sort by release date newest returns most recent first`() = runTest {
        // Given
        val movies = TestData.sampleMovies

        // When
        val result = sortMoviesUseCase(SortMoviesUseCase.Params(movies, SortOption.RELEASE_DATE_DESC))

        // Then
        assertTrue(result is Result.Success)
        val sortedMovies = (result as Result.Success).data
        assertEquals("Epsilon Movie", sortedMovies[0].title) // 2024-04-01
        assertEquals("Alpha Movie", sortedMovies[1].title)   // 2024-03-01
        assertEquals("Gamma Movie", sortedMovies[2].title)   // 2024-02-01
        assertEquals("Beta Movie", sortedMovies[3].title)    // 2024-01-01
        assertEquals("Delta Movie", sortedMovies[4].title)   // 2023-12-01
    }

    @Test
    fun `sort by release date oldest returns oldest first`() = runTest {
        // Given
        val movies = TestData.sampleMovies

        // When
        val result = sortMoviesUseCase(SortMoviesUseCase.Params(movies, SortOption.RELEASE_DATE_ASC))

        // Then
        assertTrue(result is Result.Success)
        val sortedMovies = (result as Result.Success).data
        assertEquals("Delta Movie", sortedMovies[0].title)   // 2023-12-01
        assertEquals("Beta Movie", sortedMovies[1].title)    // 2024-01-01
    }

    @Test
    fun `sort empty list returns empty list`() = runTest {
        // Given
        val movies = emptyList<nl.amro.android.app.model.MovieDto>()

        // When
        val result = sortMoviesUseCase(SortMoviesUseCase.Params(movies, SortOption.POPULARITY_DESC))

        // Then
        assertTrue(result is Result.Success)
        assertTrue((result as Result.Success).data.isEmpty())
    }
}
