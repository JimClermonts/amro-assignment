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
import javax.inject.Inject

/**
 * ViewModel for the movie detail screen.
 * Follows MVVM principles with StateFlow for UI state updates.
 */
@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    // Movie ID from navigation arguments
    private val movieId: Int = savedStateHandle.get<Int>("movieId") ?: -1

    init {
        // Get basic info from navigation arguments for immediate display
        val basicTitle = savedStateHandle.get<String>("title")
        val basicPosterPath = savedStateHandle.get<String>("posterPath")
        
        _uiState.update { 
            it.copy(
                basicTitle = basicTitle,
                basicPosterPath = basicPosterPath
            )
        }
        
        if (movieId != -1) {
            loadMovieDetails()
        }
    }

    /**
     * Load movie details from repository.
     * Emits cached data first (if available), then fresh data from API.
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
                                movieDetails = result.data
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                errorMessage = result.exception.message
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

    /**
     * Retry loading movie details.
     */
    fun retry() {
        if (movieId != -1) {
            loadMovieDetails()
        }
    }
}
