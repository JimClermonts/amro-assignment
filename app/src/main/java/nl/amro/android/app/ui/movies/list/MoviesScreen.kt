package nl.amro.android.app.ui.movies.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import nl.amro.android.app.R
import nl.amro.android.app.domain.SortOption
import nl.amro.android.app.model.Genre
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.ui.common.EmptyState
import nl.amro.android.app.ui.common.LoadingIndicator
import nl.amro.android.app.ui.theme.AmroMoviesTheme

/**
 * Movies list screen showing trending movies with filter and sort options.
 */
@Composable
fun MoviesScreen(
    onMovieClick: (MovieDto) -> Unit,
    viewModel: MoviesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MoviesScreenContent(
        uiState = uiState,
        onMovieClick = onMovieClick,
        onGenreToggle = viewModel::toggleGenreFilter,
        onFilterOpen = viewModel::loadGenresIfNeeded,
        onSortOptionSelected = viewModel::setSortOption,
        onErrorShown = viewModel::clearError
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreenContent(
    uiState: MoviesUiState,
    onMovieClick: (MovieDto) -> Unit,
    onGenreToggle: (Int) -> Unit,
    onFilterOpen: () -> Unit,
    onSortOptionSelected: (SortOption) -> Unit,
    onErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Create a NEW LazyListState when sort option or filters change
    // Using remember with keys forces a new state (starting at index 0) when sort/filter changes
    val listState = remember(uiState.sortOption, uiState.selectedGenreIds) {
        LazyListState(
            firstVisibleItemIndex = 0,
            firstVisibleItemScrollOffset = 0
        )
    }

    // Show error in snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short // 5 seconds
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
                            text = stringResource(R.string.app_bar_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        Row {
                            FilterDropdown(
                                genres = uiState.genres,
                                selectedGenreIds = uiState.selectedGenreIds,
                                onGenreToggle = onGenreToggle,
                                onFilterOpen = onFilterOpen
                            )
                            SortDropdown(
                                currentSortOption = uiState.sortOption,
                                onSortOptionSelected = onSortOptionSelected
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
            when {
                uiState.isEmpty -> {
                    EmptyState(message = stringResource(R.string.empty_movies))
                }
                else -> {
                    MoviesList(
                        movies = uiState.displayMovies,
                        genres = uiState.genres,
                        onMovieClick = onMovieClick,
                        listState = listState
                    )
                }
            }
        }
    }
}

@Composable
private fun MoviesList(
    movies: List<MovieDto>,
    genres: List<Genre>,
    onMovieClick: (MovieDto) -> Unit,
    listState: LazyListState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = movies
            // Note: Removed key parameter to prevent scroll position preservation on reorder
        ) { movie ->
            MovieCard(
                movie = movie,
                genres = genres,
                onClick = { onMovieClick(movie) }
            )
        }
    }
}

// Preview data
private val previewGenres = listOf(
    Genre(id = 28, name = "Action"),
    Genre(id = 12, name = "Adventure"),
    Genre(id = 35, name = "Comedy"),
    Genre(id = 18, name = "Drama"),
    Genre(id = 878, name = "Science Fiction")
)

private val previewMovies = listOf(
    MovieDto(
        id = 1,
        title = "The Dark Knight",
        posterPath = "/qJ2tW6WMUDux911r6m7haRef0WH.jpg",
        genreIds = listOf(28, 18),
        releaseDate = "2008-07-16",
        popularity = 150.0
    ),
    MovieDto(
        id = 2,
        title = "Inception",
        posterPath = "/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
        genreIds = listOf(28, 878),
        releaseDate = "2010-07-16",
        popularity = 140.0
    ),
    MovieDto(
        id = 3,
        title = "Everything Everywhere All at Once",
        posterPath = "/w3LxiVYdWWRvEVdn5RYq6jIqkb1.jpg",
        genreIds = listOf(28, 12, 35),
        releaseDate = "2022-03-25",
        popularity = 130.0
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MoviesScreenPreview() {
    AmroMoviesTheme {
        MoviesScreenContent(
            uiState = MoviesUiState(
                isLoading = false,
                filteredMovies = previewMovies,
                genres = previewGenres,
                selectedGenreIds = emptySet(),
                sortOption = SortOption.POPULARITY_DESC,
                errorMessage = null
            ),
            onMovieClick = {},
            onGenreToggle = {},
            onFilterOpen = {},
            onSortOptionSelected = {},
            onErrorShown = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MoviesScreenLoadingPreview() {
    AmroMoviesTheme {
        MoviesScreenContent(
            uiState = MoviesUiState(
                isLoading = true,
                filteredMovies = emptyList(),
                genres = emptyList(),
                selectedGenreIds = emptySet(),
                sortOption = SortOption.POPULARITY_DESC,
                errorMessage = null
            ),
            onMovieClick = {},
            onGenreToggle = {},
            onFilterOpen = {},
            onSortOptionSelected = {},
            onErrorShown = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MoviesScreenEmptyPreview() {
    AmroMoviesTheme {
        MoviesScreenContent(
            uiState = MoviesUiState(
                isLoading = false,
                filteredMovies = emptyList(),
                genres = emptyList(),
                selectedGenreIds = emptySet(),
                sortOption = SortOption.POPULARITY_DESC,
                errorMessage = null
            ),
            onMovieClick = {},
            onGenreToggle = {},
            onFilterOpen = {},
            onSortOptionSelected = {},
            onErrorShown = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MoviesScreenFilteredPreview() {
    AmroMoviesTheme {
        MoviesScreenContent(
            uiState = MoviesUiState(
                isLoading = false,
                filteredMovies = previewMovies.filter { 28 in it.genreIds },
                genres = previewGenres,
                selectedGenreIds = setOf(28),
                sortOption = SortOption.POPULARITY_DESC,
                errorMessage = null
            ),
            onMovieClick = {},
            onGenreToggle = {},
            onFilterOpen = {},
            onSortOptionSelected = {},
            onErrorShown = {}
        )
    }
}
