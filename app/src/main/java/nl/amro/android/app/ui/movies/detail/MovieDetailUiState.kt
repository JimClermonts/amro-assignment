package nl.amro.android.app.ui.movies.detail

import nl.amro.android.app.model.MovieDetails
import java.text.NumberFormat
import java.util.Locale

/**
 * UI state for the movie detail screen.
 */
data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movieDetails: MovieDetails? = null,
    val basicTitle: String? = null,
    val basicPosterPath: String? = null,
    val errorMessage: String? = null
) {
    /**
     * Returns the title to display (from details or basic info).
     */
    val displayTitle: String
        get() = movieDetails?.title ?: basicTitle ?: ""

    /**
     * Returns the poster path to display (from details or basic info).
     */
    val displayPosterPath: String?
        get() = movieDetails?.posterPath ?: basicPosterPath

    /**
     * Returns formatted budget in Dutch format (€1.000.000).
     */
    val formattedBudget: String
        get() {
            val budget = movieDetails?.budget ?: 0
            if (budget == 0L) return "-"
            return formatCurrency(budget)
        }

    /**
     * Returns formatted revenue in Dutch format (€1.000.000).
     */
    val formattedRevenue: String
        get() {
            val revenue = movieDetails?.revenue ?: 0
            if (revenue == 0L) return "-"
            return formatCurrency(revenue)
        }

    /**
     * Returns formatted runtime (e.g., "120 min").
     */
    val formattedRuntime: String
        get() {
            val runtime = movieDetails?.runtime ?: return "-"
            return "$runtime min"
        }

    /**
     * Returns formatted vote average (e.g., "7.5 / 10").
     */
    val formattedVoteAverage: String
        get() {
            val voteAverage = movieDetails?.voteAverage ?: return "-"
            return String.format(Locale.US, "%.1f / 10", voteAverage)
        }

    /**
     * Returns the genre names as a comma-separated string.
     */
    val genreNames: String
        get() = movieDetails?.genres?.joinToString(", ") { it.name } ?: "-"

    /**
     * Returns the IMDB URL if available.
     */
    val imdbUrl: String?
        get() = movieDetails?.imdbId?.let { "https://www.imdb.com/title/$it" }

    /**
     * Returns true if movie details have been loaded.
     */
    val hasDetails: Boolean
        get() = movieDetails != null

    private fun formatCurrency(amount: Long): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("nl-NL"))
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }
}
