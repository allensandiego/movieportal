package com.allensandiego.movieportal.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.allensandiego.movieportal.ui.components.ErrorMessage
import com.allensandiego.movieportal.ui.components.LoadingIndicator
import com.allensandiego.movieportal.ui.components.MediaGrid
import com.allensandiego.movieportal.ui.navigation.Screen
import com.allensandiego.movieportal.ui.viewmodel.PersonViewModel

@Composable
fun PersonScreen(
    navController: NavController,
    viewModel: PersonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorMessage(message = uiState.error ?: "Unknown error")
        } else {
            MediaGrid(
                items = uiState.people,
                onItemClick = { person ->
                    navController.navigate(Screen.PersonDetails.createRoute(person.id))
                },
                onLoadMore = { viewModel.loadNextPage() },
                isLoadingMore = uiState.isLoadingMore,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
