package nl.amro.android.app.ui.movies.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.amro.android.app.domain.GetMovieDetailsUseCase
import nl.amro.android.app.model.Result
import nl.amro.android.app.ui.navigation.NavArgs
import nl.amro.android.app.ui.navigation.NavRoutes
import nl.amro.android.app.ui.util.ErrorHandler
import javax.inject.Inject

/**
 * ViewModel for the movie detail screen.
 * Loads movie details using the movieId from navigation.
 */
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val errorHandler: ErrorHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = savedStateHandle.get<Int>(NavArgs.MOVIE_ID) ?: 0
    private val passedTitle: String = NavRoutes.decodeNavArg(
        savedStateHandle.get<String>(NavArgs.TITLE) ?: ""
    )
    private val passedPosterPath: String? = NavRoutes.decodeNavArg(
        savedStateHandle.get<String>(NavArgs.POSTER_PATH) ?: ""
    ).ifEmpty { null }

    private val _uiState = MutableStateFlow(
        MovieDetailUiState(
            title = passedTitle,
            posterPath = passedPosterPath
        )
    )
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    /**
     * Load movie details from repository.
     */
    private fun loadMovieDetails() {
        viewModelScope.launch {
            getMovieDetailsUseCase(movieId).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                movieDetails = result.data,
                                // Update title/poster if we got better data
                                title = result.data.title,
                                posterPath = result.data.posterPath
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = errorHandler.getErrorMessage(result.exception)
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Clear error message after it's been shown.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
