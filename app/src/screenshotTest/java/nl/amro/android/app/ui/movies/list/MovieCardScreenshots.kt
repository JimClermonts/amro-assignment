package nl.amro.android.app.ui.movies.list

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.ui.theme.AmroMoviesTheme

/**
 * Screenshot tests for MovieCard component.
 */
class MovieCardScreenshots {

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun MovieCardDefaultPreview() {
        AmroMoviesTheme {
            MovieCard(
                movie = MovieDto(
                    id = 1,
                    title = "The Dark Knight",
                    posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                    genreIds = listOf(28, 18),
                    releaseDate = "2008-07-16",
                    popularity = 150.0
                ),
                genres = listOf(
                    Genre(id = 28, name = "Action"),
                    Genre(id = 18, name = "Drama")
                ),
                onClick = {}
            )
        }
    }

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun MovieCardLongTitlePreview() {
        AmroMoviesTheme {
            MovieCard(
                movie = MovieDto(
                    id = 2,
                    title = "Everything Everywhere All at Once: The Extended Director's Cut Edition",
                    posterPath = null,
                    genreIds = listOf(28, 35, 878),
                    releaseDate = "2022-03-25",
                    popularity = 80.0
                ),
                genres = listOf(
                    Genre(id = 28, name = "Action"),
                    Genre(id = 35, name = "Comedy"),
                    Genre(id = 878, name = "Science Fiction")
                ),
                onClick = {}
            )
        }
    }

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun MovieCardNoGenresPreview() {
        AmroMoviesTheme {
            MovieCard(
                movie = MovieDto(
                    id = 3,
                    title = "Unknown Movie",
                    posterPath = null,
                    genreIds = emptyList(),
                    releaseDate = "2023-01-01",
                    popularity = 50.0
                ),
                genres = emptyList(),
                onClick = {}
            )
        }
    }

    @PreviewTest
    @Preview(showBackground = true)
    @Composable
    fun MovieCardSingleGenrePreview() {
        AmroMoviesTheme {
            MovieCard(
                movie = MovieDto(
                    id = 4,
                    title = "Action Movie",
                    posterPath = "/test.jpg",
                    genreIds = listOf(28),
                    releaseDate = "2024-06-15",
                    popularity = 120.0
                ),
                genres = listOf(
                    Genre(id = 28, name = "Action")
                ),
                onClick = {}
            )
        }
    }
}
