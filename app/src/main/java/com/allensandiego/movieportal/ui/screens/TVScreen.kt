package com.allensandiego.movieportal.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import com.allensandiego.movieportal.ui.viewmodel.TVTab
import com.allensandiego.movieportal.ui.viewmodel.TVViewModel

@Composable
fun TVScreen(
    navController: NavController,
    viewModel: TVViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val tabs = TVTab.values()

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = uiState.selectedTab.ordinal,
            edgePadding = 0.dp
        ) {
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
                items = uiState.tvShows,
                onItemClick = { tv ->
                    navController.navigate(Screen.TVDetails.createRoute(tv.id))
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
