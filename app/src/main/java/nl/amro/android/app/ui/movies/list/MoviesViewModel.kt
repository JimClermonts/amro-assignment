package nl.amro.android.app.ui.movies.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nl.amro.android.app.domain.FilterMoviesUseCase
import nl.amro.android.app.domain.GetGenresUseCase
import nl.amro.android.app.domain.GetTrendingMoviesUseCase
import nl.amro.android.app.domain.NoParams
import nl.amro.android.app.domain.SortMoviesUseCase
import nl.amro.android.app.domain.SortOption
import nl.amro.android.app.model.MovieDto
import nl.amro.android.app.model.Result
import nl.amro.android.app.ui.util.ErrorHandler
import javax.inject.Inject

/**
 * ViewModel for the movies list screen.
 * Follows MVVM principles with StateFlow for UI state updates.
 */
@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val filterMoviesUseCase: FilterMoviesUseCase,
    private val sortMoviesUseCase: SortMoviesUseCase,
    private val errorHandler: ErrorHandler
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    // Keep track of all movies (unfiltered) for filtering/sorting
    private var allMovies: List<MovieDto> = emptyList()

    init {
        loadMovies()
    }

    /**
     * Load trending movies from repository.
     */
    private fun loadMovies() {
        viewModelScope.launch {
            getTrendingMoviesUseCase(NoParams).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }
                    is Result.Success -> {
                        allMovies = result.data
                        val filteredAndSorted = applyFilterAndSortSync(result.data)
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                movies = result.data,
                                filteredMovies = filteredAndSorted
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
     * Load genres for filtering.
     * Called when user opens the filter menu for the first time.
     */
    fun loadGenresIfNeeded() {
        if (_uiState.value.genres.isNotEmpty()) return

        viewModelScope.launch {
            getGenresUseCase(NoParams).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.update { it.copy(genres = result.data) }
                    }
                    is Result.Error -> {
                        _uiState.update { 
                            it.copy(errorMessage = errorHandler.getErrorMessage(result.exception)) 
                        }
                    }
                    is Result.Loading -> { /* Ignore loading for genres */ }
                }
            }
        }
    }

    /**
     * Toggle genre selection for filtering.
     */
    fun toggleGenreFilter(genreId: Int) {
        val currentSelected = _uiState.value.selectedGenreIds
        val newSelected = if (genreId in currentSelected) {
            currentSelected - genreId
        } else {
            currentSelected + genreId
        }
        
        _uiState.update { it.copy(selectedGenreIds = newSelected) }
        applyFilterAndSort()
    }

    /**
     * Clear all genre filters.
     */
    fun clearGenreFilters() {
        _uiState.update { it.copy(selectedGenreIds = emptySet()) }
        applyFilterAndSort()
    }

    /**
     * Set the sort option.
     */
    fun setSortOption(sortOption: SortOption) {
        _uiState.update { it.copy(sortOption = sortOption) }
        applyFilterAndSort()
    }

    /**
     * Clear error message after it's been shown.
     */
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Apply current filter and sort settings to the movies list.
     */
    private fun applyFilterAndSort() {
        viewModelScope.launch {
            val filteredAndSorted = applyFilterAndSortSync(allMovies)
            _uiState.update { it.copy(filteredMovies = filteredAndSorted) }
        }
    }

    /**
     * Apply filter and sort synchronously (within same coroutine).
     */
    private suspend fun applyFilterAndSortSync(movies: List<MovieDto>): List<MovieDto> {
        val currentState = _uiState.value
        
        // First filter
        val filterResult = filterMoviesUseCase(
            FilterMoviesUseCase.Params(
                movies = movies,
                selectedGenreIds = currentState.selectedGenreIds
            )
        )
        
        val filteredMovies = when (filterResult) {
            is Result.Success -> filterResult.data
            else -> movies
        }
        
        // Then sort
        val sortResult = sortMoviesUseCase(
            SortMoviesUseCase.Params(
                movies = filteredMovies,
                sortOption = currentState.sortOption
            )
        )
        
        return when (sortResult) {
            is Result.Success -> sortResult.data
            else -> filteredMovies
        }
    }
}
