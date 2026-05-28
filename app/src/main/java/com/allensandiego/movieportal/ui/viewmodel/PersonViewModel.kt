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

data class PersonUiState(
    val people: List<TMDBItem> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1
)

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val repository: TMDBRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonUiState())
    val uiState: StateFlow<PersonUiState> = _uiState.asStateFlow()

    init {
        loadPopularPeople()
    }

    private fun loadPopularPeople() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.getPopularPeople(1)
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    people = response.results,
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
            val result = repository.getPopularPeople(nextPage)
            result.onSuccess { response ->
                _uiState.value = _uiState.value.copy(
                    people = currentState.people + response.results,
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
