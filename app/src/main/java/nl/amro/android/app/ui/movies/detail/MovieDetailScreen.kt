package nl.amro.android.app.ui.movies.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nl.amro.android.app.R
import nl.amro.android.app.model.MovieDetails
import nl.amro.android.app.ui.common.LoadingIndicator
import nl.amro.android.app.ui.theme.AmroMoviesTheme

/**
 * Movie detail screen showing full movie information.
 */
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
            // Placeholder content - will be implemented in Step 10
            if (uiState.hasDetails) {
                Text(
                    text = "Movie Details loaded: ${uiState.movieDetails?.title}",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else if (!uiState.isLoading) {
                Text(
                    text = "Loading movie: ${uiState.title}",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }
    }
}

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
                    overview = "Batman raises the stakes in his war on crime.",
                    posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
                    backdropPath = "/hqkIcbrOHL86UncnHIsHVcVmzue.jpg",
                    budget = 185000000,
                    revenue = 1004558444,
                    runtime = 152,
                    voteAverage = 8.5,
                    voteCount = 29000,
                    status = "Released",
                    releaseDate = "2008-07-16",
                    imdbId = "tt0468569",
                    genres = listOf(
                        nl.amro.android.app.model.Genre(28, "Action"),
                        nl.amro.android.app.model.Genre(18, "Drama")
                    )
                )
            ),
            onBackClick = {},
            onErrorShown = {}
        )
    }
}
