package com.allensandiego.movieportal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.allensandiego.movieportal.R
import com.allensandiego.movieportal.ui.components.ErrorMessage
import com.allensandiego.movieportal.ui.components.LoadingIndicator
import com.allensandiego.movieportal.ui.components.MediaGrid
import com.allensandiego.movieportal.ui.navigation.Screen
import com.allensandiego.movieportal.ui.viewmodel.MovieTab
import com.allensandiego.movieportal.ui.viewmodel.MovieViewModel

@Composable
fun MovieScreen(
    navController: NavController,
    viewModel: MovieViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = MovieTab.values()
    val adUnitId = stringResource(id = R.string.admob_native_ad_unit_id)

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Movies",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(
                onClick = { navController.navigate(Screen.Settings.route) }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        TabRow(selectedTabIndex = uiState.selectedTab.ordinal) {
            tabs.forEach { tab ->
                Tab(
                    selected = uiState.selectedTab == tab,
                    onClick = { viewModel.onTabSelected(tab) },
                    text = { Text(text = tab.name.replace("([A-Z])".toRegex(), " $1").trim()) }
                )
            }
        }

        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorMessage(message = uiState.error ?: "Unknown error")
        } else {
            MediaGrid(
                items = uiState.movies,
                onItemClick = { movie ->
                    navController.navigate(Screen.MovieDetails.createRoute(movie.id))
                },
                adUnitId = adUnitId,
                onLoadMore = { viewModel.loadNextPage() },
                isLoadingMore = uiState.isLoadingMore,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
