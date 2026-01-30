package nl.amro.android.app.ui.navigation

import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Navigation routes for the app.
 */
object NavRoutes {
    const val MOVIES_LIST = "movies_list"
    const val MOVIE_DETAIL = "movie_detail/{movieId}/{title}/{posterPath}"

    /**
     * Create the route for movie detail screen.
     * Encodes the title and posterPath to handle special characters.
     */
    fun movieDetailRoute(movieId: Int, title: String, posterPath: String?): String {
        val encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString())
        val encodedPosterPath = URLEncoder.encode(posterPath ?: "", StandardCharsets.UTF_8.toString())
        return "movie_detail/$movieId/$encodedTitle/$encodedPosterPath"
    }

    /**
     * Decode URL-encoded strings for navigation arguments.
     */
    fun decodeNavArg(encoded: String): String {
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
    }
}

/**
 * Navigation argument keys.
 */
object NavArgs {
    const val MOVIE_ID = "movieId"
    const val TITLE = "title"
    const val POSTER_PATH = "posterPath"
}
