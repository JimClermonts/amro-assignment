package nl.amro.android.app.domain

import kotlinx.coroutines.CoroutineDispatcher
import nl.amro.android.app.di.DefaultDispatcher
import nl.amro.android.app.model.MovieDto
import javax.inject.Inject

/**
 * Sort options for movies list.
 */
enum class SortOption {
    POPULARITY_DESC,    // Popularity (high to low) - default
    POPULARITY_ASC,     // Popularity (low to high)
    TITLE_ASC,          // Title (A-Z)
    TITLE_DESC,         // Title (Z-A)
    RELEASE_DATE_DESC,  // Release date (newest)
    RELEASE_DATE_ASC    // Release date (oldest)
}

/**
 * Use case for sorting movies by various criteria.
 * 
 * Actor: User
 * Goal: Sort movies by selected criteria
 * Precondition: User has selected a sort option from the sort menu
 * Main Success Scenario:
 *   1. User selects sort option
 *   2. System sorts movies according to selected criteria
 *   3. System displays sorted movie list
 */
class SortMoviesUseCase @Inject constructor(
    @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
) : SuspendUseCase<SortMoviesUseCase.Params, List<MovieDto>>(defaultDispatcher) {

    data class Params(
        val movies: List<MovieDto>,
        val sortOption: SortOption
    )

    override suspend fun execute(parameters: Params): List<MovieDto> {
        val (movies, sortOption) = parameters
        
        return when (sortOption) {
            SortOption.POPULARITY_DESC -> movies.sortedByDescending { it.popularity }
            SortOption.POPULARITY_ASC -> movies.sortedBy { it.popularity }
            SortOption.TITLE_ASC -> movies.sortedBy { it.title.lowercase() }
            SortOption.TITLE_DESC -> movies.sortedByDescending { it.title.lowercase() }
            SortOption.RELEASE_DATE_DESC -> movies.sortedByDescending { it.releaseDate ?: "" }
            SortOption.RELEASE_DATE_ASC -> movies.sortedBy { it.releaseDate ?: "" }
        }
    }
}
