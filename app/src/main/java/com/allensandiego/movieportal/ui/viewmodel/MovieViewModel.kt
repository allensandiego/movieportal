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
    val error: String? = null
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
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        loadMovies(tab)
    }

    private fun loadMovies(tab: MovieTab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = when (tab) {
                MovieTab.NowPlaying -> repository.getNowPlayingMovies()
                MovieTab.Popular -> repository.getPopularMovies()
                MovieTab.TopRated -> repository.getTopRatedMovies()
                MovieTab.Upcoming -> repository.getUpcomingMovies()
            }
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    movies = response.results,
                    isLoading = false,
                    error = null
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}
