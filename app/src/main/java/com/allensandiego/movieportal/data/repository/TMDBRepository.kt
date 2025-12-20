package com.allensandiego.movieportal.data.repository

import com.allensandiego.movieportal.data.api.TMDBService
import com.allensandiego.movieportal.data.model.BaseResponse
import com.allensandiego.movieportal.data.model.CreditsResponse
import com.allensandiego.movieportal.data.model.EpisodeGroupResponse
import com.allensandiego.movieportal.data.model.GenreResponse
import com.allensandiego.movieportal.data.model.ImageResponse
import com.allensandiego.movieportal.data.model.MovieDetails
import com.allensandiego.movieportal.data.model.PersonCreditsResponse
import com.allensandiego.movieportal.data.model.PersonDetails
import com.allensandiego.movieportal.data.model.PersonImagesResponse
import com.allensandiego.movieportal.data.model.ReviewResponse
import com.allensandiego.movieportal.data.model.TMDBItem
import com.allensandiego.movieportal.data.model.TVShowDetails
import com.allensandiego.movieportal.data.model.TVSeasonDetails
import com.allensandiego.movieportal.data.model.VideoResponse
import javax.inject.Inject

interface TMDBRepository {
    suspend fun searchMulti(query: String): Result<BaseResponse<TMDBItem>>
    suspend fun getTrendingAll(): Result<BaseResponse<TMDBItem>>
    
    // Movies
    suspend fun getNowPlayingMovies(): Result<BaseResponse<TMDBItem>>
    suspend fun getPopularMovies(): Result<BaseResponse<TMDBItem>>
    suspend fun getTopRatedMovies(): Result<BaseResponse<TMDBItem>>
    suspend fun getUpcomingMovies(): Result<BaseResponse<TMDBItem>>
    suspend fun getMovieDetails(movieId: Int): Result<MovieDetails>
    suspend fun getMovieImages(movieId: Int): Result<ImageResponse>
    suspend fun getMovieVideos(movieId: Int): Result<VideoResponse>
    suspend fun getMovieCredits(movieId: Int): Result<CreditsResponse>
    suspend fun getMovieReviews(movieId: Int): Result<ReviewResponse>

    // TV
    suspend fun getAiringTodayTV(): Result<BaseResponse<TMDBItem>>
    suspend fun getPopularTV(): Result<BaseResponse<TMDBItem>>
    suspend fun getTopRatedTV(): Result<BaseResponse<TMDBItem>>
    suspend fun getOnTheAirTV(): Result<BaseResponse<TMDBItem>>
    suspend fun getTVShowDetails(seriesId: Int): Result<TVShowDetails>
    suspend fun getTVImages(seriesId: Int): Result<ImageResponse>
    suspend fun getTVVideos(seriesId: Int): Result<VideoResponse>
    suspend fun getTVCredits(seriesId: Int): Result<CreditsResponse>
    suspend fun getTVReviews(seriesId: Int): Result<ReviewResponse>
    suspend fun getTVEpisodeGroups(seriesId: Int): Result<EpisodeGroupResponse>
    suspend fun getTVSeasonDetails(seriesId: Int, seasonNumber: Int): Result<TVSeasonDetails>

    // Person
    suspend fun getPopularPeople(): Result<BaseResponse<TMDBItem>>
    suspend fun getPersonDetails(personId: Int): Result<PersonDetails>
    suspend fun getPersonImages(personId: Int): Result<PersonImagesResponse>
    suspend fun getPersonMovieCredits(personId: Int): Result<PersonCreditsResponse>
    suspend fun getPersonTVCredits(personId: Int): Result<PersonCreditsResponse>

    // Genres
    suspend fun getMovieGenres(): Result<GenreResponse>
    suspend fun getTVGenres(): Result<GenreResponse>
}

class TMDBRepositoryImpl @Inject constructor(
    private val service: TMDBService
) : TMDBRepository {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchMulti(query: String) = safeApiCall { service.searchMulti(query) }
    override suspend fun getTrendingAll() = safeApiCall { service.getTrendingAll() }

    override suspend fun getNowPlayingMovies() = safeApiCall { service.getNowPlayingMovies() }
    override suspend fun getPopularMovies() = safeApiCall { service.getPopularMovies() }
    override suspend fun getTopRatedMovies() = safeApiCall { service.getTopRatedMovies() }
    override suspend fun getUpcomingMovies() = safeApiCall { service.getUpcomingMovies() }
    override suspend fun getMovieDetails(movieId: Int) = safeApiCall { service.getMovieDetails(movieId) }
    override suspend fun getMovieImages(movieId: Int) = safeApiCall { service.getMovieImages(movieId) }
    override suspend fun getMovieVideos(movieId: Int) = safeApiCall { service.getMovieVideos(movieId) }
    override suspend fun getMovieCredits(movieId: Int) = safeApiCall { service.getMovieCredits(movieId) }
    override suspend fun getMovieReviews(movieId: Int) = safeApiCall { service.getMovieReviews(movieId) }

    override suspend fun getAiringTodayTV() = safeApiCall { service.getAiringTodayTV() }
    override suspend fun getPopularTV() = safeApiCall { service.getPopularTV() }
    override suspend fun getTopRatedTV() = safeApiCall { service.getTopRatedTV() }
    override suspend fun getOnTheAirTV() = safeApiCall { service.getOnTheAirTV() }
    override suspend fun getTVShowDetails(seriesId: Int) = safeApiCall { service.getTVShowDetails(seriesId) }
    override suspend fun getTVImages(seriesId: Int) = safeApiCall { service.getTVImages(seriesId) }
    override suspend fun getTVVideos(seriesId: Int) = safeApiCall { service.getTVVideos(seriesId) }
    override suspend fun getTVCredits(seriesId: Int) = safeApiCall { service.getTVCredits(seriesId) }
    override suspend fun getTVReviews(seriesId: Int) = safeApiCall { service.getTVReviews(seriesId) }
    override suspend fun getTVEpisodeGroups(seriesId: Int) = safeApiCall { service.getTVEpisodeGroups(seriesId) }
    override suspend fun getTVSeasonDetails(seriesId: Int, seasonNumber: Int) = safeApiCall { service.getTVSeasonDetails(seriesId, seasonNumber) }

    override suspend fun getPopularPeople() = safeApiCall { service.getPopularKey() } 
    override suspend fun getPersonDetails(personId: Int) = safeApiCall { service.getPersonDetails(personId) }
    override suspend fun getPersonImages(personId: Int) = safeApiCall { service.getPersonImages(personId) }
    override suspend fun getPersonMovieCredits(personId: Int) = safeApiCall { service.getPersonMovieCredits(personId) }
    override suspend fun getPersonTVCredits(personId: Int) = safeApiCall { service.getPersonTVCredits(personId) }

    override suspend fun getMovieGenres() = safeApiCall { service.getMovieGenres() }
    override suspend fun getTVGenres() = safeApiCall { service.getTVGenres() }
}
