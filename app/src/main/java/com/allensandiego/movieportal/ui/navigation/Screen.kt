package com.allensandiego.movieportal.ui.navigation

import com.allensandiego.movieportal.R


sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Movie : Screen("movie")
    object TV : Screen("tv")
    object Person : Screen("person")
    
    // Details
    object MovieDetails : Screen("movie_details/{movieId}") {
        fun createRoute(movieId: Int) = "movie_details/$movieId"
    }
    object TVDetails : Screen("tv_details/{seriesId}") {
        fun createRoute(seriesId: Int) = "tv_details/$seriesId"
    }
    object PersonDetails : Screen("person_details/{personId}") {
        fun createRoute(personId: Int) = "person_details/$personId"
    }
    object TVSeasonDetails : Screen("tv_details/{seriesId}/season/{seasonNumber}") {
        fun createRoute(seriesId: Int, seasonNumber: Int) = "tv_details/$seriesId/season/$seasonNumber"
    }

    // Media
    object FullscreenImage : Screen("fullscreen_image?url={url}") {
        fun createRoute(url: String) = "fullscreen_image?url=$url"
    }
    object FullscreenVideo : Screen("fullscreen_video?key={key}") {
        fun createRoute(key: String) = "fullscreen_video?key=$key"
    }
    object Settings : Screen("settings")
}

enum class BottomNavItem(
    val route: String,
    val title: String,
    val iconRes: Int // We will use drawable resources
) {
    Movie(Screen.Movie.route, "Movie", R.drawable.film_slate),
    TV(Screen.TV.route, "TV Show", R.drawable.television),
    Person(Screen.Person.route, "Person", R.drawable.actress)
}
