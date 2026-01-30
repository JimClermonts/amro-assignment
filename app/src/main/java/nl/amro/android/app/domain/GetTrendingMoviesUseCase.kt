package nl.amro.android.app.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import nl.amro.android.app.data.repository.MoviesRepository
import nl.amro.android.app.di.IoDispatcher
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.model.Result
import javax.inject.Inject

/**
 * Use case for getting the top 100 trending movies of the week.
 * 
 * Actor: User
 * Goal: View trending movies list
 * Precondition: App is launched
 * Main Success Scenario:
 *   1. System fetches cached movies (if available)
 *   2. System fetches fresh movies from API
 *   3. System displays updated movie list
 * Extensions:
 *   2a. Network unavailable: System shows cached data with error message
 *   2b. No cache and no network: System shows error message
 */
class GetTrendingMoviesUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<NoParams, List<MovieDto>>(ioDispatcher) {

    override fun execute(parameters: NoParams): Flow<Result<List<MovieDto>>> {
        return moviesRepository.getTrendingMovies()
    }
}
