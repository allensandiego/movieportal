package com.allensandiego.movieportal.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.allensandiego.movieportal.ui.components.ErrorMessage
import com.allensandiego.movieportal.ui.components.LoadingIndicator
import com.allensandiego.movieportal.ui.components.MediaGrid
import com.allensandiego.movieportal.ui.navigation.Screen
import com.allensandiego.movieportal.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            onSearch = { /* Search triggered by query change */ },
            active = false,
            onActiveChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search movies, tv, people...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            }
        ) {
            // No history or suggestions required
        }

        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorMessage(message = uiState.error ?: "Unknown error")
        } else {
            val itemsToShow = if (uiState.searchQuery.isNotEmpty()) {
                uiState.searchResults
            } else {
                uiState.trendingItems
            }
            
            val sectionTitle = if (uiState.searchQuery.isNotEmpty()) {
                "Search Results"
            } else {
                "Trending"
            }

            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            val adUnitId = androidx.compose.ui.res.stringResource(id = com.allensandiego.movieportal.R.string.admob_native_ad_unit_id)

            MediaGrid(
                items = itemsToShow,
                onItemClick = { item ->
                    // Navigate to details based on media_type
                    when (item.mediaType) {
                        "movie" -> navController.navigate(Screen.MovieDetails.createRoute(item.id))
                        "tv" -> navController.navigate(Screen.TVDetails.createRoute(item.id))
                        "person" -> navController.navigate(Screen.PersonDetails.createRoute(item.id))
                        else -> {
                            // Fallback if media_type is missing, maybe check fields
                            if (item.title != null) navController.navigate(Screen.MovieDetails.createRoute(item.id))
                            else if (item.name != null) navController.navigate(Screen.TVDetails.createRoute(item.id)) // Could be Person or TV...
                        }
                    }
                },
                adUnitId = adUnitId,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
