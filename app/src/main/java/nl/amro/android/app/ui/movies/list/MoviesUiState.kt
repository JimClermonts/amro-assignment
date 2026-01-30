package nl.amro.android.app.ui.movies.list

import nl.amro.android.app.domain.SortOption
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDto

/**
 * UI state for the movies list screen.
 */
data class MoviesUiState(
    val isLoading: Boolean = true,
    val filteredMovies: List<MovieDto> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val selectedGenreIds: Set<Int> = emptySet(),
    val sortOption: SortOption = SortOption.POPULARITY_DESC,
    val errorMessage: String? = null
) {
    /**
     * Returns display-ready movies (filtered and sorted).
     */
    val displayMovies: List<MovieDto>
        get() = filteredMovies

    /**
     * Returns true if there are no movies to display.
     */
    val isEmpty: Boolean
        get() = !isLoading && filteredMovies.isEmpty()
}
