package nl.amro.android.app.ui.movies.list

import nl.amro.android.app.domain.SortOption
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDto

/**
 * UI state for the movies list screen.
 */
data class MoviesUiState(
    val isLoading: Boolean = true,
    val movies: List<MovieDto> = emptyList(),
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

    /**
     * Returns true if a filter is active (genres selected).
     */
    val isFilterActive: Boolean
        get() = selectedGenreIds.isNotEmpty()
}

/**
 * Represents a movie item for display in the list.
 * Contains the movie data plus resolved genre names.
 */
data class MovieListItem(
    val movie: MovieDto,
    val genreNames: List<String>
) {
    /**
     * Returns genre names limited to 2, formatted as "Action, Adventure".
     */
    val displayGenres: String
        get() = genreNames.take(2).joinToString(", ")
}
