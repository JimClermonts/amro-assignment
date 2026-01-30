package nl.amro.android.app.ui.movies.detail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import nl.amro.android.app.R
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.ui.common.LoadingIndicator
import nl.amro.android.app.ui.theme.AmroMoviesTheme
import java.text.NumberFormat
import java.util.Locale
import androidx.core.net.toUri

private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val IMDB_BASE_URL = "https://www.imdb.com/title/"

/**
 * Movie detail screen showing full movie information.
 * Note: movieId, title, posterPath are passed via navigation but retrieved from SavedStateHandle in ViewModel.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun MovieDetailScreen(
    movieId: Int,
    title: String,
    posterPath: String?,
    onBackClick: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MovieDetailScreenContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onErrorShown = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreenContent(
    uiState: MovieDetailUiState,
    onBackClick: () -> Unit,
    onErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Show error in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            onErrorShown()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = uiState.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.navigate_back)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                LoadingIndicator(isLoading = uiState.isLoading)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.hasDetails) {
                MovieDetailContent(
                    movieDetails = uiState.movieDetails!!,
                    onImdbClick = { imdbId ->
                        val intent = Intent(Intent.ACTION_VIEW, "$IMDB_BASE_URL$imdbId".toUri())
                        context.startActivity(intent)
                    }
                )
            } else if (!uiState.isLoading) {
                // Show poster while loading details or on error
                PosterPlaceholder(
                    posterPath = uiState.posterPath,
                    title = uiState.title
                )
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movieDetails: MovieDetails,
    onImdbClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Poster and basic info row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            // Poster - fixed width for layout stability
            AsyncImage(
                model = movieDetails.posterPath?.let { "$IMAGE_BASE_URL$it" },
                contentDescription = stringResource(R.string.movie_poster),
                modifier = Modifier
                    .width(140.dp)
                    .aspectRatio(2f / 3f)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Basic info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title
                Text(
                    text = movieDetails.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // Tagline
                movieDetails.tagline?.takeIf { it.isNotBlank() }?.let { tagline ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Release date and status
                movieDetails.releaseDate?.let { date ->
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                movieDetails.status?.let { status ->
                    Text(
                        text = status,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Runtime
                movieDetails.runtime?.let { runtime ->
                    Text(
                        text = stringResource(R.string.runtime_format, runtime),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Vote average
                if (movieDetails.voteAverage > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.vote_format,
                                String.format(Locale.US, "%.1f", movieDetails.voteAverage)
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = pluralStringResource(R.plurals.vote_count, movieDetails.voteCount, movieDetails.voteCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Genres
        if (movieDetails.genres.isNotEmpty()) {
            Text(
                text = movieDetails.genres.joinToString(", ") { it.name },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Overview/Description
        movieDetails.overview?.takeIf { it.isNotBlank() }?.let { overview ->
            Text(
                text = overview,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Budget and Revenue
        if (movieDetails.budget > 0 || movieDetails.revenue > 0) {
            BudgetRevenueSection(
                budget = movieDetails.budget,
                revenue = movieDetails.revenue
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // IMDB Link
        movieDetails.imdbId?.let { imdbId ->
            Button(
                onClick = { onImdbClick(imdbId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.view_on_imdb))
            }
        }

        // Bottom spacing
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun BudgetRevenueSection(
    budget: Long,
    revenue: Long
) {
    val dutchFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("nl-NL")).apply {
            maximumFractionDigits = 0
        }
    }

    Column {
        if (budget > 0) {
            Row {
                Text(
                    text = stringResource(R.string.budget_label) + ": ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dutchFormat.format(budget),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (revenue > 0) {
            Row {
                Text(
                    text = stringResource(R.string.revenue_label) + ": ",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dutchFormat.format(revenue),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun PosterPlaceholder(
    posterPath: String?,
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = posterPath?.let { "$IMAGE_BASE_URL$it" },
            contentDescription = stringResource(R.string.movie_poster),
            modifier = Modifier
                .width(200.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

// Preview composables
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MovieDetailScreenLoadingPreview() {
    AmroMoviesTheme {
        MovieDetailScreenContent(
            uiState = MovieDetailUiState(
                isLoading = true,
                title = "The Dark Knight",
                posterPath = "/test.jpg"
            ),
            onBackClick = {},
            onErrorShown = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MovieDetailScreenSuccessPreview() {
    AmroMoviesTheme {
        MovieDetailScreenContent(
            uiState = MovieDetailUiState(
                isLoading = false,
                title = "The Dark Knight",
                posterPath = "/test.jpg",
                movieDetails = MovieDetails(
                    id = 155,
                    title = "The Dark Knight",
                    tagline = "Why so serious?",
                    overview = "Batman raises the stakes in his war on crime. With the help of Lt. Jim Gordon and District Attorney Harvey Dent, Batman sets out to dismantle the remaining criminal organizations that plague the streets. The partnership proves to be effective, but they soon find themselves prey to a reign of chaos unleashed by a rising criminal mastermind known to the terrified citizens of Gotham as the Joker.",
                    posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                    backdropPath = "/hqkIcbrOHL86UncnHIsHVcVmzue.jpg",
                    budget = 185000000,
                    revenue = 1004558444,
                    runtime = 152,
                    voteAverage = 8.516,
                    voteCount = 32000,
                    status = "Released",
                    releaseDate = "2008-07-16",
                    imdbId = "tt0468569",
                    genres = listOf(
                        Genre(28, "Action"),
                        Genre(80, "Crime"),
                        Genre(18, "Drama"),
                        Genre(53, "Thriller")
                    )
                )
            ),
            onBackClick = {},
            onErrorShown = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MovieDetailScreenErrorPreview() {
    AmroMoviesTheme {
        MovieDetailScreenContent(
            uiState = MovieDetailUiState(
                isLoading = false,
                title = "Unknown Movie",
                posterPath = null,
                errorMessage = "Film niet gevonden."
            ),
            onBackClick = {},
            onErrorShown = {}
        )
    }
}
