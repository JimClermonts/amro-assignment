package nl.amro.android.app.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import nl.amro.android.app.TestData
import nl.amro.android.app.data.repository.MoviesRepository
import nl.amro.android.app.model.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetTrendingMoviesUseCaseTest {

    private lateinit var getTrendingMoviesUseCase: GetTrendingMoviesUseCase
    private lateinit var moviesRepository: MoviesRepository

    @Before
    fun setup() {
        moviesRepository = mock()
        getTrendingMoviesUseCase = GetTrendingMoviesUseCase(moviesRepository, Dispatchers.Unconfined)
    }

    @Test
    fun `when repository returns success, emits success result`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        whenever(moviesRepository.getTrendingMovies()).thenReturn(
            flowOf(Result.Success(movies))
        )

        // When
        val results = getTrendingMoviesUseCase(NoParams).toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Success)
        assertEquals(movies, (results[0] as Result.Success).data)
    }

    @Test
    fun `when repository returns loading then success, emits both states`() = runTest {
        // Given
        val movies = TestData.sampleMovies
        whenever(moviesRepository.getTrendingMovies()).thenReturn(
            flowOf(Result.Loading, Result.Success(movies))
        )

        // When
        val results = getTrendingMoviesUseCase(NoParams).toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
    }

    @Test
    fun `when repository returns error, emits error result`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        whenever(moviesRepository.getTrendingMovies()).thenReturn(
            flowOf(Result.Error(exception))
        )

        // When
        val results = getTrendingMoviesUseCase(NoParams).toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Error)
        assertEquals(exception, (results[0] as Result.Error).exception)
    }
}
