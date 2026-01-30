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

class GetMovieDetailsUseCaseTest {

    private lateinit var getMovieDetailsUseCase: GetMovieDetailsUseCase
    private lateinit var moviesRepository: MoviesRepository

    @Before
    fun setup() {
        moviesRepository = mock()
        getMovieDetailsUseCase = GetMovieDetailsUseCase(moviesRepository, Dispatchers.Unconfined)
    }

    @Test
    fun `when repository returns success, emits movie details`() = runTest {
        // Given
        val movieId = 1
        val movieDetails = TestData.createMovieDetails(id = movieId)
        whenever(moviesRepository.getMovieDetails(movieId)).thenReturn(
            flowOf(Result.Success(movieDetails))
        )

        // When
        val results = getMovieDetailsUseCase(movieId).toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Success)
        assertEquals(movieDetails, (results[0] as Result.Success).data)
    }

    @Test
    fun `when repository returns loading then success, emits both states`() = runTest {
        // Given
        val movieId = 1
        val movieDetails = TestData.createMovieDetails(id = movieId)
        whenever(moviesRepository.getMovieDetails(movieId)).thenReturn(
            flowOf(Result.Loading, Result.Success(movieDetails))
        )

        // When
        val results = getMovieDetailsUseCase(movieId).toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Success)
        assertEquals(movieDetails, (results[1] as Result.Success).data)
    }

    @Test
    fun `when repository returns error, emits error result`() = runTest {
        // Given
        val movieId = 999
        val exception = RuntimeException("Movie not found")
        whenever(moviesRepository.getMovieDetails(movieId)).thenReturn(
            flowOf(Result.Error(exception))
        )

        // When
        val results = getMovieDetailsUseCase(movieId).toList()

        // Then
        assertEquals(1, results.size)
        assertTrue(results[0] is Result.Error)
    }

    @Test
    fun `when repository returns cached then fresh data, emits both`() = runTest {
        // Given
        val movieId = 1
        val cachedDetails = TestData.createMovieDetails(id = movieId, title = "Cached Title")
        val freshDetails = TestData.createMovieDetails(id = movieId, title = "Fresh Title")
        whenever(moviesRepository.getMovieDetails(movieId)).thenReturn(
            flowOf(
                Result.Loading,
                Result.Success(cachedDetails),
                Result.Success(freshDetails)
            )
        )

        // When
        val results = getMovieDetailsUseCase(movieId).toList()

        // Then
        assertEquals(3, results.size)
        assertTrue(results[0] is Result.Loading)
        assertEquals("Cached Title", (results[1] as Result.Success).data.title)
        assertEquals("Fresh Title", (results[2] as Result.Success).data.title)
    }
}
