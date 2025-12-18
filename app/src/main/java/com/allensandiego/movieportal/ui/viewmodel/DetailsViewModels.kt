package com.allensandiego.movieportal.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.allensandiego.movieportal.data.model.Cast
import com.allensandiego.movieportal.data.model.EpisodeGroup
import com.allensandiego.movieportal.data.model.Image
import com.allensandiego.movieportal.data.model.MovieDetails
import com.allensandiego.movieportal.data.model.PersonDetails
import com.allensandiego.movieportal.data.model.Review
import com.allensandiego.movieportal.data.model.TVShowDetails
import com.allensandiego.movieportal.data.model.Video
import com.allensandiego.movieportal.data.repository.TMDBRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.allensandiego.movieportal.data.model.PersonCredit
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.allensandiego.movieportal.data.model.ImageResponse
import com.allensandiego.movieportal.data.model.VideoResponse
import com.allensandiego.movieportal.data.model.CreditsResponse
import com.allensandiego.movieportal.data.model.ReviewResponse
import javax.inject.Inject

data class MovieDetailsUiState(
    val details: MovieDetails? = null,
    val images: List<Image> = emptyList(),
    val videos: List<Video> = emptyList(),
    val cast: List<Cast> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: TMDBRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"]).let {
        if (it is String) it.toInt() else it.toString().toInt()
    } // Navigation arguments are strings usually unless properly typed, safe cast

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                coroutineScope {
                    val detailsDeferred = async { repository.getMovieDetails(movieId) }
                    val imagesDeferred = async { repository.getMovieImages(movieId) }
                    val videosDeferred = async { repository.getMovieVideos(movieId) }
                    val creditsDeferred = async { repository.getMovieCredits(movieId) }
                    val reviewsDeferred = async { repository.getMovieReviews(movieId) }

                    val detailsResult = detailsDeferred.await()
                    val imagesResult = imagesDeferred.await()
                    val videosResult = videosDeferred.await()
                    val creditsResult = creditsDeferred.await()
                    val reviewsResult = reviewsDeferred.await()

                    if (detailsResult.isSuccess) {
                        _uiState.value = _uiState.value.copy(
                            details = detailsResult.getOrNull(),
                            images = imagesResult.getOrNull()?.backdrops ?: emptyList(),
                            videos = videosResult.getOrNull()?.results ?: emptyList(),
                            cast = creditsResult.getOrNull()?.cast ?: emptyList(),
                            reviews = reviewsResult.getOrNull()?.results ?: emptyList(),
                            isLoading = false
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = detailsResult.exceptionOrNull()?.message ?: "Unknown error",
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }
}

data class TVDetailsUiState(
    val details: TVShowDetails? = null,
    val images: List<com.allensandiego.movieportal.data.model.Image> = emptyList(),
    val videos: List<com.allensandiego.movieportal.data.model.Video> = emptyList(),
    val cast: List<com.allensandiego.movieportal.data.model.Cast> = emptyList(),
    val reviews: List<com.allensandiego.movieportal.data.model.Review> = emptyList(),
    val episodeGroups: List<EpisodeGroup> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class TVDetailsViewModel @Inject constructor(
    private val repository: TMDBRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val seriesId: Int = checkNotNull(savedStateHandle["seriesId"]).let {
         if (it is String) it.toInt() else it.toString().toInt()
    }
    private val _uiState = MutableStateFlow(TVDetailsUiState())
    val uiState: StateFlow<TVDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val detailsDeferred = async { repository.getTVShowDetails(seriesId) }
            val imagesDeferred = async { repository.getTVImages(seriesId) }
            val videosDeferred = async { repository.getTVVideos(seriesId) }
            val creditsDeferred = async { repository.getTVCredits(seriesId) }
            val reviewsDeferred = async { repository.getTVReviews(seriesId) }
            val episodeGroupsDeferred = async { repository.getTVEpisodeGroups(seriesId) }

            val detailsResult = detailsDeferred.await()
            val imagesResult = imagesDeferred.await()
            val videosResult = videosDeferred.await()
            val creditsResult = creditsDeferred.await()
            val reviewsResult = reviewsDeferred.await()
            val episodeGroupsResult = episodeGroupsDeferred.await()

            if (detailsResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    details = detailsResult.getOrNull(),
                    images = imagesResult.getOrNull()?.backdrops ?: emptyList(),
                    videos = videosResult.getOrNull()?.results ?: emptyList(),
                    cast = creditsResult.getOrNull()?.cast ?: emptyList(),
                    reviews = reviewsResult.getOrNull()?.results ?: emptyList(),
                    episodeGroups = episodeGroupsResult.getOrNull()?.results ?: emptyList(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = detailsResult.exceptionOrNull()?.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }
}

data class PersonDetailsUiState(
    val details: PersonDetails? = null,
    val images: List<Image> = emptyList(),
    val movieCredits: List<PersonCredit> = emptyList(),
    val tvCredits: List<PersonCredit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PersonDetailsViewModel @Inject constructor(
    private val repository: TMDBRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val personId: Int = checkNotNull(savedStateHandle["personId"]).let {
         if (it is String) it.toInt() else it.toString().toInt()
    }
    private val _uiState = MutableStateFlow(PersonDetailsUiState())
    val uiState: StateFlow<PersonDetailsUiState> = _uiState.asStateFlow()

    init {
        loadDetails()
    }

    private fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            val detailsDeferred = async { repository.getPersonDetails(personId) }
            val imagesDeferred = async { repository.getPersonImages(personId) }
            val movieCreditsDeferred = async { repository.getPersonMovieCredits(personId) }
            val tvCreditsDeferred = async { repository.getPersonTVCredits(personId) }

            val detailsResult = detailsDeferred.await()
            val imagesResult = imagesDeferred.await()
            val movieCreditsResult = movieCreditsDeferred.await()
            val tvCreditsResult = tvCreditsDeferred.await()

            if (detailsResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    details = detailsResult.getOrNull(),
                    images = imagesResult.getOrNull()?.profiles ?: emptyList(),
                    movieCredits = movieCreditsResult.getOrNull()?.cast ?: emptyList(),
                    tvCredits = tvCreditsResult.getOrNull()?.cast ?: emptyList(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    error = detailsResult.exceptionOrNull()?.message ?: "Unknown error",
                    isLoading = false
                )
            }
        }
    }
}

