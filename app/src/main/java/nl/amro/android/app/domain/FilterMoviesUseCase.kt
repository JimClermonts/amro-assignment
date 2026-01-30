package nl.amro.android.app.domain

import kotlinx.coroutines.CoroutineDispatcher
import nl.amro.android.app.di.DefaultDispatcher
import nl.amro.android.app.model.MovieDto
import javax.inject.Inject

/**
 * Use case for filtering movies by selected genres.
 * 
 * Actor: User
 * Goal: Filter movies by genre
 * Precondition: User has selected one or more genres from the filter menu
 * Main Success Scenario:
 *   1. User selects genres to filter by
 *   2. System filters movies that match selected genres
 *   3. System displays filtered movie list
 * Special Rule:
 *   - When no genres are selected, all movies are shown (no filter applied)
 */
class FilterMoviesUseCase @Inject constructor(
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
) : SuspendUseCase<FilterMoviesUseCase.Params, List<MovieDto>>(defaultDispatcher) {

    data class Params(
        val movies: List<MovieDto>,
        val selectedGenreIds: Set<Int>
    )

    override suspend fun execute(parameters: Params): List<MovieDto> {
        val (movies, selectedGenreIds) = parameters
        
        // When no genres are selected, show all movies (no filter applied)
        if (selectedGenreIds.isEmpty()) {
            return movies
        }
        
        // Filter movies that have at least one of the selected genres
        return movies.filter { movie ->
            movie.genreIds.any { genreId -> genreId in selectedGenreIds }
        }
    }
}
