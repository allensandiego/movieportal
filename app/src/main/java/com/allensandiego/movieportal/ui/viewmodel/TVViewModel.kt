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
    val error: String? = null
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
        _uiState.value = _uiState.value.copy(selectedTab = tab)
        loadTVShows(tab)
    }

    private fun loadTVShows(tab: TVTab) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = when (tab) {
                TVTab.AiringToday -> repository.getAiringTodayTV()
                TVTab.Popular -> repository.getPopularTV()
                TVTab.TopRated -> repository.getTopRatedTV()
                TVTab.OnTheAir -> repository.getOnTheAirTV()
            }
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    tvShows = response.results,
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
