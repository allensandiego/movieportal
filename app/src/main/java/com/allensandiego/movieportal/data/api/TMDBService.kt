package com.allensandiego.movieportal.data.api

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
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBService {

    // Search
    @GET("search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): BaseResponse<TMDBItem>

    // Trending
    @GET("trending/all/day")
    suspend fun getTrendingAll(
        @Query("page") page: Int = 1
    ): BaseResponse<TMDBItem>

    // Movies
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetails

    @GET("movie/{movie_id}/images")
    suspend fun getMovieImages(@Path("movie_id") movieId: Int): ImageResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(@Path("movie_id") movieId: Int): VideoResponse

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Int): CreditsResponse

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(@Path("movie_id") movieId: Int): ReviewResponse


    // TV Shows
    @GET("tv/airing_today")
    suspend fun getAiringTodayTV(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("tv/popular")
    suspend fun getPopularTV(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("tv/top_rated")
    suspend fun getTopRatedTV(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTV(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("tv/{series_id}")
    suspend fun getTVShowDetails(@Path("series_id") seriesId: Int): TVShowDetails

    @GET("tv/{series_id}/images")
    suspend fun getTVImages(@Path("series_id") seriesId: Int): ImageResponse

    @GET("tv/{series_id}/videos")
    suspend fun getTVVideos(@Path("series_id") seriesId: Int): VideoResponse

    @GET("tv/{series_id}/credits")
    suspend fun getTVCredits(@Path("series_id") seriesId: Int): CreditsResponse

    @GET("tv/{series_id}/reviews")
    suspend fun getTVReviews(@Path("series_id") seriesId: Int): ReviewResponse

    @GET("tv/{series_id}/episode_groups")
    suspend fun getTVEpisodeGroups(@Path("series_id") seriesId: Int): EpisodeGroupResponse

    @GET("tv/{series_id}/season/{season_number}")
    suspend fun getTVSeasonDetails(
        @Path("series_id") seriesId: Int,
        @Path("season_number") seasonNumber: Int
    ): TVSeasonDetails

    // People
    @GET("person/popular")
    suspend fun getPopularKey(@Query("page") page: Int = 1): BaseResponse<TMDBItem>

    @GET("person/{person_id}")
    suspend fun getPersonDetails(@Path("person_id") personId: Int): PersonDetails

    @GET("person/{person_id}/images")
    suspend fun getPersonImages(@Path("person_id") personId: Int): PersonImagesResponse

    @GET("person/{person_id}/movie_credits")
    suspend fun getPersonMovieCredits(@Path("person_id") personId: Int): PersonCreditsResponse

    @GET("person/{person_id}/tv_credits")
    suspend fun getPersonTVCredits(@Path("person_id") personId: Int): PersonCreditsResponse

    // Genres
    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreResponse

    @GET("genre/tv/list")
    suspend fun getTVGenres(): GenreResponse
}
