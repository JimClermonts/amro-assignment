package nl.amro.android.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import nl.amro.android.app.ui.movies.detail.MovieDetailScreen
import nl.amro.android.app.ui.movies.list.MoviesScreen

/**
 * Main navigation host for the app.
 */
@Composable
fun AmroNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.MOVIES_LIST,
        modifier = modifier
    ) {
        // Movies List Screen
        composable(route = NavRoutes.MOVIES_LIST) {
            MoviesScreen(
                onMovieClick = { movie ->
                    navController.navigate(
                        NavRoutes.movieDetailRoute(
                            movieId = movie.id,
                            title = movie.title,
                            posterPath = movie.posterPath
                        )
                    )
                }
            )
        }

        // Movie Detail Screen
        composable(
            route = NavRoutes.MOVIE_DETAIL,
            arguments = listOf(
                navArgument(NavArgs.MOVIE_ID) { type = NavType.IntType },
                navArgument(NavArgs.TITLE) { type = NavType.StringType },
                navArgument(NavArgs.POSTER_PATH) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt(NavArgs.MOVIE_ID) ?: 0
            val title = NavRoutes.decodeNavArg(
                backStackEntry.arguments?.getString(NavArgs.TITLE) ?: ""
            )
            val posterPath = NavRoutes.decodeNavArg(
                backStackEntry.arguments?.getString(NavArgs.POSTER_PATH) ?: ""
            ).ifEmpty { null }

            MovieDetailScreen(
                movieId = movieId,
                title = title,
                posterPath = posterPath,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
