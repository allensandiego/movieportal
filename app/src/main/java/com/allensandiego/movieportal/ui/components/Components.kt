package com.allensandiego.movieportal.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import com.allensandiego.movieportal.ui.components.NativeAdComponent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.allensandiego.movieportal.R
import com.allensandiego.movieportal.data.model.TMDBItem

import com.allensandiego.movieportal.util.DateUtils

const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

@Composable
fun MediaCard(
    item: TMDBItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            val imagePath = item.posterPath ?: item.profilePath
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f / 3f)
            ) {
                if (imagePath != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(IMAGE_BASE_URL + imagePath)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title ?: item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Placeholder
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Image")
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = item.title ?: item.name ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.releaseDate != null || item.firstAirDate != null) {
                    Text(
                        text = DateUtils.formatDate(item.releaseDate ?: item.firstAirDate),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (item.popularity != null) {
                   Text(
                        text = "Pop: ${item.popularity.toInt()}", // Truncate for simplicity
                        style = MaterialTheme.typography.labelSmall
                    ) 
                }
                // Genre display would require mapping generic IDs to names which we need to fetch
            }
        }
    }
}

@Composable
fun MediaGrid(
    items: List<TMDBItem>,
    onItemClick: (TMDBItem) -> Unit,
    modifier: Modifier = Modifier,
    adUnitId: String? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        // We can insert an ad every 6 items for example
        val itemsWithAds = items.chunked(6)
        
        itemsWithAds.forEachIndexed { index, chunk ->
            items(chunk) { item ->
                MediaCard(item = item, onClick = { onItemClick(item) })
            }
            
            // Insert ad after each chunk if adUnitId is provided
            if (adUnitId != null && index < itemsWithAds.size - 1) {
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    NativeAdComponent(adUnitId = adUnitId)
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}
