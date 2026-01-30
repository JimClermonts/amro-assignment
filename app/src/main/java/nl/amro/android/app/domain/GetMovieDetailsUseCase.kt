package nl.amro.android.app.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import nl.amro.android.app.data.repository.MoviesRepository
import nl.amro.android.app.di.IoDispatcher
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.model.Result
import javax.inject.Inject

/**
 * Use case for getting detailed information about a specific movie.
 * 
 * Actor: User
 * Goal: View movie details
 * Precondition: User has selected a movie from the list
 * Main Success Scenario:
 *   1. System fetches cached movie details (if available)
 *   2. System fetches fresh movie details from API
 *   3. System saves movie details to cache
 *   4. System displays movie details
 * Extensions:
 *   2a. Network unavailable with cache: System shows cached data
 *   2b. Network unavailable without cache: System shows error message
 */
class GetMovieDetailsUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<Int, MovieDetails>(ioDispatcher) {

    /**
     * @param parameters The movie ID to fetch details for
     */
    override fun execute(parameters: Int): Flow<Result<MovieDetails>> {
        return moviesRepository.getMovieDetails(parameters)
    }
}
