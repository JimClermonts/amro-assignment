package nl.amro.android.app.domain

import kotlinx.coroutines.CoroutineDispatcher
import nl.amro.android.app.data.repository.MoviesRepository
import nl.amro.android.app.di.IoDispatcher
import nl.amro.android.app.model.Genre
import javax.inject.Inject

/**
 * Use case for getting genre names from genre IDs.
 * Used to display genre names on movie cards (limited to 2 genres).
 * 
 * Actor: System
 * Goal: Map genre IDs to genre names for display
 * Precondition: Genres have been cached
 * Main Success Scenario:
 *   1. System receives list of genre IDs
 *   2. System looks up genre names from cache
 *   3. System returns genre names
 */
class GetGenresByIdsUseCase @Inject constructor(
    private val moviesRepository: MoviesRepository,
    @IoDispatcher ioDispatcher: CoroutineDispatcher
) : SuspendUseCase<List<Int>, List<Genre>>(ioDispatcher) {

    override suspend fun execute(parameters: List<Int>): List<Genre> {
        return moviesRepository.getGenresByIds(parameters)
    }
}
