package nl.amro.android.app.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import nl.amro.android.app.data.repository.MoviesRepository
import nl.amro.android.app.di.IoDispatcher
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.Result
import javax.inject.Inject

/**
 * Use case for getting the list of movie genres.
 * Used for filtering movies by genre.
 * 
 * Actor: User
 * Goal: View available genres for filtering
 * Precondition: User opens the filter menu
 * Main Success Scenario:
 *   1. System fetches cached genres (if available)
 *   2. If no cache, system fetches genres from API
 *   3. System displays genre list
 * Extensions:
 *   2a. Network unavailable without cache: System shows error message
 */
class GetGenresUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : FlowUseCase<NoParams, List<Genre>>(ioDispatcher) {

    override fun execute(parameters: NoParams): Flow<Result<List<Genre>>> {
        return moviesRepository.getGenres()
    }
}
