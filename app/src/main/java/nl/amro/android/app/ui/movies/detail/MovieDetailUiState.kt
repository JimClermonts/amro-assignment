package nl.amro.android.app.ui.movies.detail

import nl.amro.android.app.model.MovieDetails

/**
 * UI state for the movie detail screen.
 */
data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movieDetails: MovieDetails? = null,
    val errorMessage: String? = null,
    // Pass-through data for immediate display while loading
    val title: String = "",
    val posterPath: String? = null
) {
    /**
     * Whether the screen has loaded movie details successfully.
     */
    val hasDetails: Boolean
        get() = movieDetails != null
}
