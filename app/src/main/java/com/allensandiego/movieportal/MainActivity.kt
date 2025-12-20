package com.allensandiego.movieportal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.allensandiego.movieportal.ui.navigation.BottomNavItem
import com.allensandiego.movieportal.ui.navigation.Screen
import com.allensandiego.movieportal.ui.screens.FullscreenImageScreen
import com.allensandiego.movieportal.ui.screens.FullscreenVideoScreen
import com.allensandiego.movieportal.ui.screens.HomeContent
import com.allensandiego.movieportal.ui.screens.MovieDetailsScreen
import com.allensandiego.movieportal.ui.screens.MovieScreen
import com.allensandiego.movieportal.ui.screens.PersonDetailsScreen
import com.allensandiego.movieportal.ui.screens.PersonScreen
import com.allensandiego.movieportal.ui.screens.SplashScreen
import com.allensandiego.movieportal.ui.screens.TVDetailsScreen
import com.allensandiego.movieportal.ui.screens.TVSeasonDetailsScreen
import com.allensandiego.movieportal.ui.screens.TVScreen
import com.allensandiego.movieportal.ui.theme.TMDBTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TMDBTheme {
                MovieApp()
            }
        }
    }
}

@Composable
fun MovieApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Movie.route,
        Screen.TV.route,
        Screen.Person.route
    )
    
    // Check if current route matches any of the bottom bar routes or is a sub-route if needed.
    // Actually, Screen.Home is not in bottom bar items, but we should probably show it?
    // Requirement says: "Bottom navigation bar having 3 buttons, movie, tv show and person."
    // So if I am on Home (Search/Trending), should I see the bottom bar?
    // Yes, generally.
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(navController)
            }
            
            composable(Screen.Home.route) {
                HomeContent(navController)
            }
            
            composable(Screen.Movie.route) {
                MovieScreen(navController)
            }
            
            composable(Screen.TV.route) {
                TVScreen(navController)
            }
            
            composable(Screen.Person.route) {
                PersonScreen(navController)
            }
            
            composable(
                Screen.MovieDetails.route,
                arguments = listOf(navArgument("movieId") { type = NavType.IntType })
            ) {
                MovieDetailsScreen(navController)
            }
            
            composable(
                Screen.TVDetails.route,
                arguments = listOf(navArgument("seriesId") { type = NavType.IntType })
            ) {
                TVDetailsScreen(navController)
            }
            
            composable(
                Screen.PersonDetails.route,
                arguments = listOf(navArgument("personId") { type = NavType.IntType })
            ) {
                PersonDetailsScreen(navController)
            }

            composable(
                Screen.FullscreenImage.route,
                arguments = listOf(navArgument("url") { type = NavType.StringType })
            ) { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: ""
                FullscreenImageScreen(url) { navController.popBackStack() }
            }

            composable(
                Screen.FullscreenVideo.route,
                arguments = listOf(navArgument("key") { type = NavType.StringType })
            ) { backStackEntry ->
                val key = backStackEntry.arguments?.getString("key") ?: ""
                FullscreenVideoScreen(key) { navController.popBackStack() }
            }

            composable(
                Screen.TVSeasonDetails.route,
                arguments = listOf(
                    navArgument("seriesId") { type = NavType.IntType },
                    navArgument("seasonNumber") { type = NavType.IntType }
                )
            ) {
                TVSeasonDetailsScreen(navController)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Movie,
        BottomNavItem.TV,
        BottomNavItem.Person
    )
    
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.iconRes), contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
