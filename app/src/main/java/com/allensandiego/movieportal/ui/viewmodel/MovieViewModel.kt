package com.allensandiego.movieportal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allensandiego.movieportal.data.model.TMDBItem
import com.allensandiego.movieportal.data.repository.TMDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MovieTab {
    NowPlaying, Popular, TopRated, Upcoming
}

data class MovieUiState(
    val selectedTab: MovieTab = MovieTab.NowPlaying,
    val movies: List<TMDBItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1
)

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val repository: TMDBRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieUiState())
    val uiState: StateFlow<MovieUiState> = _uiState.asStateFlow()

    init {
        loadMovies(MovieTab.NowPlaying)
    }

    fun onTabSelected(tab: MovieTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.value = MovieUiState(selectedTab = tab)
        loadMovies(tab)
    }

    private fun loadMovies(tab: MovieTab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = when (tab) {
                MovieTab.NowPlaying -> repository.getNowPlayingMovies(1)
                MovieTab.Popular -> repository.getPopularMovies(1)
                MovieTab.TopRated -> repository.getTopRatedMovies(1)
                MovieTab.Upcoming -> repository.getUpcomingMovies(1)
            }
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    movies = response.results,
                    isLoading = false,
                    error = null,
                    currentPage = response.page,
                    totalPages = response.totalPages
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || currentState.currentPage >= currentState.totalPages) return

        val nextPage = currentState.currentPage + 1
        _uiState.value = currentState.copy(isLoadingMore = true)

        viewModelScope.launch {
            val result = when (currentState.selectedTab) {
                MovieTab.NowPlaying -> repository.getNowPlayingMovies(nextPage)
                MovieTab.Popular -> repository.getPopularMovies(nextPage)
                MovieTab.TopRated -> repository.getTopRatedMovies(nextPage)
                MovieTab.Upcoming -> repository.getUpcomingMovies(nextPage)
            }
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    movies = currentState.movies + response.results,
                    isLoadingMore = false,
                    error = null,
                    currentPage = response.page,
                    totalPages = response.totalPages
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoadingMore = false,
                    error = e.message
                )
            }
        }
    }
}
