package nl.amro.android.app.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import nl.amro.android.app.model.GenreListResponse
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.model.TrendingMoviesResponse
import nl.amro.android.app.model.Result
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "TmdbApiService"

/**
 * Implementation of TmdbApi using Ktor HttpClient.
 * Handles all network requests to The Movie Database API.
 */
@Singleton
class TmdbApiService @Inject constructor(
    private val httpClient: HttpClient
) : TmdbApi {

    companion object {
        private const val TRENDING_MOVIES_ENDPOINT = "trending/movie/week"
        private const val MOVIE_DETAILS_ENDPOINT = "movie"
        private const val GENRE_LIST_ENDPOINT = "genre/movie/list"
        private const val LANGUAGE = "en-US"
        private const val PAGES_TO_FETCH = 5 // To get 100 movies (20 per page)
    }

    /**
     * Fetches the top 100 trending movies of the week.
     * Makes 5 parallel API calls (20 movies per page) and combines the results.
     *
     * @return Result containing list of MovieDto or error
     */
    override suspend fun getTrendingMovies(): Result<List<MovieDto>> = coroutineScope {
        Log.d(TAG, "getTrendingMovies: Starting API calls")
        try {
            // Make 5 parallel API calls for pages 1-5
            val deferredResults = (1..PAGES_TO_FETCH).map { page ->
                async {
                    Log.d(TAG, "getTrendingMovies: Fetching page $page")
                    httpClient.get(TRENDING_MOVIES_ENDPOINT) {
                        parameter("language", LANGUAGE)
                        parameter("page", page)
                    }.body<TrendingMoviesResponse>()
                }
            }

            // Wait for all requests to complete and combine results
            Log.d(TAG, "getTrendingMovies: Waiting for all pages")
            val allResponses = deferredResults.awaitAll()
            val allMovies = allResponses.flatMap { it.results }
            Log.d(TAG, "getTrendingMovies: Got ${allMovies.size} movies total")

            Result.Success(allMovies)
        } catch (e: Exception) {
            Log.e(TAG, "getTrendingMovies: Error", e)
            Result.Error(e)
        }
    }

    /**
     * Fetches detailed information for a specific movie.
     *
     * @param movieId The TMDB movie ID
     * @return Result containing MovieDetails or error
     */
    override suspend fun getMovieDetails(movieId: Int): Result<MovieDetails> {
        return try {
            val response = httpClient.get("$MOVIE_DETAILS_ENDPOINT/$movieId") {
                parameter("language", LANGUAGE)
            }.body<MovieDetails>()

            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Fetches the list of official movie genres.
     *
     * @return Result containing GenreListResponse or error
     */
    override suspend fun getGenres(): Result<GenreListResponse> {
        return try {
            val response = httpClient.get(GENRE_LIST_ENDPOINT) {
                parameter("language", LANGUAGE)
            }.body<GenreListResponse>()

            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
