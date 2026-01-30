package nl.amro.android.app.data.repository

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import nl.amro.android.app.TestData
import nl.amro.android.app.data.datasource.LocalDataSource
import nl.amro.android.app.data.datasource.RemoteDataSource
import nl.amro.android.app.model.Result
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class MoviesRepositoryTest {

    private lateinit var repository: MoviesRepositoryImpl
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        remoteDataSource = mock()
        localDataSource = mock()
        repository = MoviesRepositoryImpl(remoteDataSource, localDataSource)
    }

    // getTrendingMovies tests

    @Test
    fun `getTrendingMovies emits loading first`() = runTest {
        // Given
        whenever(localDataSource.getMovies()).thenReturn(emptyList())
        whenever(remoteDataSource.getTrendingMovies()).thenReturn(Result.Success(TestData.sampleMovies))

        // When
        val results = repository.getTrendingMovies().toList()

        // Then
        assertTrue(results[0] is Result.Loading)
    }

    @Test
    fun `getTrendingMovies emits cached data when available`() = runTest {
        // Given
        val cachedMovies = TestData.sampleMovies
        whenever(localDataSource.getMovies()).thenReturn(cachedMovies)
        whenever(remoteDataSource.getTrendingMovies()).thenReturn(Result.Success(cachedMovies))

        // When
        val results = repository.getTrendingMovies().toList()

        // Then
        // Should have Loading, cached Success, fresh Success
        assertTrue(results.any { it is Result.Success && (it as Result.Success).data == cachedMovies })
    }

    @Test
    fun `getTrendingMovies saves fresh data to cache on success`() = runTest {
        // Given
        val freshMovies = TestData.sampleMovies
        whenever(localDataSource.getMovies()).thenReturn(emptyList())
        whenever(remoteDataSource.getTrendingMovies()).thenReturn(Result.Success(freshMovies))

        // When
        repository.getTrendingMovies().toList()

        // Then
        verify(localDataSource).clearMovies()
        verify(localDataSource).saveMovies(freshMovies)
    }

    @Test
    fun `getTrendingMovies emits error when no cache and API fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        whenever(localDataSource.getMovies()).thenReturn(emptyList())
        whenever(remoteDataSource.getTrendingMovies()).thenReturn(Result.Error(exception))

        // When
        val results = repository.getTrendingMovies().toList()

        // Then
        assertTrue(results.last() is Result.Error)
    }

    @Test
    fun `getTrendingMovies does not emit error when cache exists and API fails`() = runTest {
        // Given
        val cachedMovies = TestData.sampleMovies
        val exception = RuntimeException("Network error")
        whenever(localDataSource.getMovies()).thenReturn(cachedMovies)
        whenever(remoteDataSource.getTrendingMovies()).thenReturn(Result.Error(exception))

        // When
        val results = repository.getTrendingMovies().toList()

        // Then
        // Should have Loading and cached Success, but no Error (because cache was emitted)
        val errors = results.filterIsInstance<Result.Error>()
        assertTrue(errors.isEmpty())
    }

    // getMovieDetails tests

    @Test
    fun `getMovieDetails emits cached data first when available`() = runTest {
        // Given
        val movieId = 1
        val cachedDetails = TestData.createMovieDetails(id = movieId)
        whenever(localDataSource.getMovieDetails(movieId)).thenReturn(cachedDetails)
        whenever(remoteDataSource.getMovieDetails(movieId)).thenReturn(Result.Success(cachedDetails))

        // When
        val results = repository.getMovieDetails(movieId).toList()

        // Then
        val successResults = results.filterIsInstance<Result.Success<*>>()
        assertTrue(successResults.isNotEmpty())
    }

    @Test
    fun `getMovieDetails saves fresh data to cache`() = runTest {
        // Given
        val movieId = 1
        val freshDetails = TestData.createMovieDetails(id = movieId)
        whenever(localDataSource.getMovieDetails(movieId)).thenReturn(null)
        whenever(remoteDataSource.getMovieDetails(movieId)).thenReturn(Result.Success(freshDetails))

        // When
        repository.getMovieDetails(movieId).toList()

        // Then
        verify(localDataSource).saveMovieDetails(freshDetails)
    }

    @Test
    fun `getMovieDetails emits error when no cache and API fails`() = runTest {
        // Given
        val movieId = 999
        val exception = RuntimeException("Movie not found")
        whenever(localDataSource.getMovieDetails(movieId)).thenReturn(null)
        whenever(remoteDataSource.getMovieDetails(movieId)).thenReturn(Result.Error(exception))

        // When
        val results = repository.getMovieDetails(movieId).toList()

        // Then
        assertTrue(results.last() is Result.Error)
    }

    // getGenres tests

    @Test
    fun `getGenres uses cached genres when available`() = runTest {
        // Given
        val cachedGenres = TestData.sampleGenres
        whenever(localDataSource.getGenres()).thenReturn(cachedGenres)

        // When
        val results = repository.getGenres().toList()

        // Then
        val successResults = results.filterIsInstance<Result.Success<*>>()
        assertTrue(successResults.isNotEmpty())
        // Should not call remote when cache exists
        verify(remoteDataSource, never()).getGenres()
    }

    @Test
    fun `getGenres fetches from API when no cache`() = runTest {
        // Given
        val freshGenres = TestData.sampleGenres
        whenever(localDataSource.getGenres()).thenReturn(emptyList())
        whenever(remoteDataSource.getGenres()).thenReturn(Result.Success(freshGenres))

        // When
        repository.getGenres().toList()

        // Then
        verify(remoteDataSource).getGenres()
        verify(localDataSource).saveGenres(freshGenres)
    }

    // refreshMovies tests

    @Test
    fun `refreshMovies clears cache and saves fresh data`() = runTest {
        // Given
        val freshMovies = TestData.sampleMovies
        whenever(remoteDataSource.getTrendingMovies()).thenReturn(Result.Success(freshMovies))

        // When
        val result = repository.refreshMovies()

        // Then
        assertTrue(result is Result.Success)
        verify(localDataSource).clearMovies()
        verify(localDataSource).saveMovies(freshMovies)
    }

    @Test
    fun `refreshMovies returns error on API failure`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        whenever(remoteDataSource.getTrendingMovies()).thenReturn(Result.Error(exception))

        // When
        val result = repository.refreshMovies()

        // Then
        assertTrue(result is Result.Error)
        verify(localDataSource, never()).clearMovies()
    }
}
