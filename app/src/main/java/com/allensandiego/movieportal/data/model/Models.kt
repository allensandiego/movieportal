package com.allensandiego.movieportal.data.model

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    val page: Int,
    val results: List<T>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)

data class TMDBItem(
    val id: Int,
    @SerializedName("media_type") val mediaType: String? = null, // "movie", "tv", "person"
    
    // Common
    val popularity: Double? = null,
    val overview: String? = null,
    
    // Movie / TV
    val title: String? = null, // Movie
    val name: String? = null,  // TV / Person
    
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    
    // Person
    @SerializedName("profile_path") val profilePath: String? = null,
    // @SerializedName("known_for") val knownFor: List<TMDBItem>? = null, // Can cause recursion issues if not careful, skipping for now as it wasn't explicitly requested for the list view
    
    // Movie specific
    @SerializedName("release_date") val releaseDate: String? = null,
    
    // TV specific
    @SerializedName("first_air_date") val firstAirDate: String? = null,
    
    @SerializedName("genre_ids") val genreIds: List<Int>? = null
)

data class GenreResponse(
    val genres: List<Genre>
)

data class Genre(
    val id: Int,
    val name: String
)

// For Details (Placeholder for now, might need expansion)
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val genres: List<Genre>?,
    val runtime: Int?
)

data class TVShowDetails(
    val id: Int,
    val name: String,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    val genres: List<Genre>?,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int?
)

data class PersonDetails(
    val id: Int,
    val name: String,
    val biography: String?,
    @SerializedName("profile_path") val profilePath: String?,
    val birthday: String?,
    @SerializedName("place_of_birth") val placeOfBirth: String?
)



// Images
data class ImageResponse(
    val id: Int,
    val backdrops: List<Image>,
    val posters: List<Image>
)

data class Image(
    @SerializedName("file_path") val filePath: String,
    val width: Int,
    val height: Int,
    @SerializedName("aspect_ratio") val aspectRatio: Double
)

// Videos
data class VideoResponse(
    val id: Int,
    val results: List<Video>
)

data class Video(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)

// Credits
data class CreditsResponse(
    val id: Int,
    val cast: List<Cast>,
    val crew: List<Crew>
)

data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class Crew(
    val id: Int,
    val name: String,
    val job: String,
    @SerializedName("profile_path") val profilePath: String?
)

// Reviews
data class ReviewResponse(
    val id: Int,
    val results: List<Review>
)

data class Review(
    val id: String,
    val author: String,
    val content: String,
    @SerializedName("author_details") val authorDetails: AuthorDetails
)

data class AuthorDetails(
    val name: String,
    val username: String,
    @SerializedName("avatar_path") val avatarPath: String?,
    val rating: Double?
)

// Person Specific Media
data class PersonImagesResponse(
    val id: Int,
    val profiles: List<Image>
)

data class PersonCreditsResponse(
    val id: Int,
    val cast: List<PersonCredit>
)

data class PersonCredit(
    val id: Int,
    val title: String?,
    @SerializedName("poster_path") val posterPath: String?,
    val name: String?, // For TV
    val character: String?,
    @SerializedName("media_type") val mediaType: String?
)

// TV Episode Groups
data class EpisodeGroupResponse(
    val id: Int,
    val results: List<EpisodeGroup>
)

data class EpisodeGroup(
    val id: String,
    val name: String,
    val description: String,
    @SerializedName("episode_count") val episodeCount: Int,
    @SerializedName("group_count") val groupCount: Int,
    val type: Int
)

