package nl.amro.android.app.ui.movies.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.ui.theme.AmroMoviesTheme

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
private val CARD_HEIGHT = 120.dp
private val POSTER_WIDTH = 80.dp // 2:3 ratio with 120dp height

/**
 * Movie card for list display.
 * Fixed height of 120dp with 2:3 poster aspect ratio.
 */
@Composable
fun MovieCard(
    movie: MovieDto,
    genres: List<Genre>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val genreNames = movie.genreIds
        .take(6)
        .mapNotNull { genreId -> genres.find { it.id == genreId }?.name }
        .joinToString(", ")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(CARD_HEIGHT)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Poster image (2:3 ratio)
            AsyncImage(
                model = movie.posterPath?.let { "$IMAGE_BASE_URL$it" },
                contentDescription = movie.title,
                modifier = Modifier
                    .width(POSTER_WIDTH)
                    .height(CARD_HEIGHT),
                contentScale = ContentScale.Crop
            )

            // Movie info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (genreNames.isNotEmpty()) {
                    Text(
                        text = genreNames,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                movie.releaseDate?.take(4)?.let { year ->
                    Text(
                        text = year,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieCardPreview() {
    AmroMoviesTheme {
        MovieCard(
            movie = MovieDto(
                id = 1,
                title = "The Lord of the Rings: The Return of the King",
                posterPath = "/rCzpDGLbOoPwLjy3OAm5NUPOTrC.jpg",
                genreIds = listOf(28, 12),
                releaseDate = "2023-05-15",
                popularity = 100.0
            ),
            genres = listOf(
                Genre(id = 28, name = "Action"),
                Genre(id = 12, name = "Adventure")
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MovieCardLongTitlePreview() {
    AmroMoviesTheme {
        MovieCard(
            movie = MovieDto(
                id = 2,
                title = "Everything Everywhere All at Once: The Extended Director's Cut Edition",
                posterPath = null,
                genreIds = listOf(28, 35, 878, 18, 10749, 12),
                releaseDate = "2022-03-25",
                popularity = 80.0
            ),
            genres = listOf(
                Genre(id = 28, name = "Action"),
                Genre(id = 35, name = "Comedy"),
                Genre(id = 878, name = "Science Fiction"),
                Genre(id = 18, name = "Drama"),
                Genre(id = 10749, name = "Romance"),
                Genre(id = 12, name = "Adventure")
            ),
            onClick = {}
        )
    }
}
