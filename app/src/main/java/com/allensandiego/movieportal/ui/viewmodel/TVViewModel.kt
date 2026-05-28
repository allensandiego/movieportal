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

enum class TVTab {
    AiringToday, Popular, TopRated, OnTheAir
}

data class TVUiState(
    val selectedTab: TVTab = TVTab.AiringToday,
    val tvShows: List<TMDBItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1
)

@HiltViewModel
class TVViewModel @Inject constructor(
    private val repository: TMDBRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TVUiState())
    val uiState: StateFlow<TVUiState> = _uiState.asStateFlow()

    init {
        loadTVShows(TVTab.AiringToday)
    }

    fun onTabSelected(tab: TVTab) {
        if (_uiState.value.selectedTab == tab) return
        _uiState.value = TVUiState(selectedTab = tab)
        loadTVShows(tab)
    }

    private fun loadTVShows(tab: TVTab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = when (tab) {
                TVTab.AiringToday -> repository.getAiringTodayTV(1)
                TVTab.Popular -> repository.getPopularTV(1)
                TVTab.TopRated -> repository.getTopRatedTV(1)
                TVTab.OnTheAir -> repository.getOnTheAirTV(1)
            }
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    tvShows = response.results,
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
                TVTab.AiringToday -> repository.getAiringTodayTV(nextPage)
                TVTab.Popular -> repository.getPopularTV(nextPage)
                TVTab.TopRated -> repository.getTopRatedTV(nextPage)
                TVTab.OnTheAir -> repository.getOnTheAirTV(nextPage)
            }
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    tvShows = currentState.tvShows + response.results,
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
