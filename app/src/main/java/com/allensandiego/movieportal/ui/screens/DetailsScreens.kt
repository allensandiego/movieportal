package com.allensandiego.movieportal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.allensandiego.movieportal.ui.navigation.Screen
import com.allensandiego.movieportal.data.model.Cast
import com.allensandiego.movieportal.data.model.Image
import com.allensandiego.movieportal.data.model.Review
import com.allensandiego.movieportal.data.model.Video
import com.allensandiego.movieportal.ui.components.ErrorMessage
import com.allensandiego.movieportal.ui.components.IMAGE_BASE_URL
import com.allensandiego.movieportal.ui.components.LoadingIndicator
import com.allensandiego.movieportal.ui.viewmodel.MovieDetailsViewModel
import com.allensandiego.movieportal.ui.viewmodel.PersonDetailsViewModel
import com.allensandiego.movieportal.ui.viewmodel.TVDetailsViewModel
import com.allensandiego.movieportal.util.DateUtils

@Composable
fun MovieDetailsScreen(
    navController: NavHostController,
    viewModel: MovieDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorMessage(message = uiState.error ?: "Error loading details")
        } else {
            val details = uiState.details
            if (details != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (details.backdropPath != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(IMAGE_BASE_URL + details.backdropPath)
                                .crossfade(true)
                                .build(),
                            contentDescription = details.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = details.title, style = MaterialTheme.typography.headlineMedium)
                        Text(text = "Release: ${DateUtils.formatDate(details.releaseDate)}", style = MaterialTheme.typography.bodyMedium)
                        if (details.runtime != null) {
                            Text(text = "Runtime: ${details.runtime} min", style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = details.overview ?: "", style = MaterialTheme.typography.bodyLarge)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Genres: ${details.genres?.joinToString { it.name }}")

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Videos & Images",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        ImagesVideosSection(uiState.images, uiState.videos, navController)

                        val adUnitId = androidx.compose.ui.res.stringResource(id = com.allensandiego.movieportal.R.string.admob_native_ad_unit_id)
                        com.allensandiego.movieportal.ui.components.NativeAdComponent(
                            adUnitId = adUnitId,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Credits",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        CreditsSection(uiState.cast) { personId ->
                            navController.navigate(Screen.PersonDetails.createRoute(personId))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        ReviewsSection(uiState.reviews)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ImagesVideosSection(images: List<Image>, videos: List<Video>, navController: NavHostController) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(videos) { video ->
            VideoItem(video) {
                navController.navigate(Screen.FullscreenVideo.createRoute(video.key))
            }
        }
        items(images) { image ->
            ImageItem(image) {
                navController.navigate(Screen.FullscreenImage.createRoute(java.net.URLEncoder.encode(IMAGE_BASE_URL + image.filePath, "UTF-8")))
            }
        }
    }
}

@Composable
fun VideoItem(video: Video, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(200.dp)
            .height(120.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://img.youtube.com/vi/${video.key}/0.jpg")
                    .crossfade(true)
                    .build(),
                contentDescription = video.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = "▶",
                style = MaterialTheme.typography.headlineLarge,
                color = androidx.compose.ui.graphics.Color.White
            )
            Text(
                text = video.name,
                style = MaterialTheme.typography.labelSmall,
                color = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ImageItem(image: Image, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(200.dp)
            .height(120.dp),
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(IMAGE_BASE_URL + image.filePath)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun CreditsSection(cast: List<Cast>, onPersonClick: (Int) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(cast) { person ->
            PersonItem(person) {
                onPersonClick(person.id)
            }
        }
    }
}

@Composable
fun PersonItem(person: Cast, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(IMAGE_BASE_URL + person.profilePath)
                .crossfade(true)
                .build(),
            contentDescription = person.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(120.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = person.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = person.character,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ReviewsSection(reviews: List<Review>) {
    if (reviews.isEmpty()) {
        Text(
            text = "No reviews available.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    } else {
        Column {
            reviews.take(5).forEach { review ->
                ReviewItem(review)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = review.author,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            if (review.authorDetails.rating != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⭐ ${review.authorDetails.rating}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = review.content,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TVDetailsScreen(
    navController: NavHostController,
    viewModel: TVDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorMessage(message = uiState.error ?: "Error loading details")
        } else {
            val details = uiState.details
            if (details != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (details.backdropPath != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(IMAGE_BASE_URL + details.backdropPath)
                                .crossfade(true)
                                .build(),
                            contentDescription = details.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = details.name, style = MaterialTheme.typography.headlineMedium)
                        Text(text = "First Air: ${DateUtils.formatDate(details.firstAirDate)}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Seasons: ${details.numberOfSeasons}", style = MaterialTheme.typography.bodyMedium)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = details.overview ?: "", style = MaterialTheme.typography.bodyLarge)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Genres: ${details.genres?.joinToString { it.name }}")

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Videos & Images",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        ImagesVideosSection(uiState.images, uiState.videos, navController)

                        val adUnitId = androidx.compose.ui.res.stringResource(id = com.allensandiego.movieportal.R.string.admob_native_ad_unit_id)
                        com.allensandiego.movieportal.ui.components.NativeAdComponent(
                            adUnitId = adUnitId,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        if (uiState.episodeGroups.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Episode Info",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            EpisodeInfoSection(uiState.episodeGroups)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Credits",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        CreditsSection(uiState.cast) { personId ->
                            navController.navigate(Screen.PersonDetails.createRoute(personId))
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        ReviewsSection(uiState.reviews)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PersonDetailsScreen(
    navController: NavHostController,
    viewModel: PersonDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorMessage(message = uiState.error ?: "Error loading details")
        } else {
            val details = uiState.details
            if (details != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    if (details.profilePath != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(IMAGE_BASE_URL + details.profilePath)
                                .crossfade(true)
                                .build(),
                            contentDescription = details.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = details.name, style = MaterialTheme.typography.headlineMedium)
                        if (details.birthday != null) {
                            Text(text = "Born: ${DateUtils.formatDate(details.birthday)}", style = MaterialTheme.typography.bodyMedium)
                        }
                        if (details.placeOfBirth != null) {
                            Text(text = details.placeOfBirth, style = MaterialTheme.typography.bodyMedium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        if (details.biography != null) {
                             Text(text = details.biography, style = MaterialTheme.typography.bodyLarge)
                        }

                        if (uiState.images.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Images",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            PersonImagesSection(uiState.images, navController)

                            val adUnitId = androidx.compose.ui.res.stringResource(id = com.allensandiego.movieportal.R.string.admob_native_ad_unit_id)
                            com.allensandiego.movieportal.ui.components.NativeAdComponent(
                                adUnitId = adUnitId,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }

                        if (uiState.movieCredits.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            PersonCreditsSection(
                                title = "Movie Credits",
                                credits = uiState.movieCredits
                            ) { movieId ->
                                navController.navigate(Screen.MovieDetails.createRoute(movieId))
                            }
                        }

                        if (uiState.tvCredits.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            PersonCreditsSection(
                                title = "TV Credits",
                                credits = uiState.tvCredits
                            ) { tvId ->
                                navController.navigate(Screen.TVDetails.createRoute(tvId))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PersonImagesSection(images: List<Image>, navController: NavHostController) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(images) { image ->
            ImageItem(image) {
                navController.navigate(Screen.FullscreenImage.createRoute(java.net.URLEncoder.encode(IMAGE_BASE_URL + image.filePath, "UTF-8")))
            }
        }
    }
}

@Composable
fun PersonCreditsSection(
    title: String,
    credits: List<com.allensandiego.movieportal.data.model.PersonCredit>,
    onCreditClick: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            items(credits) { credit ->
                CreditItem(credit) {
                    onCreditClick(credit.id)
                }
            }
        }
    }
}

@Composable
fun CreditItem(credit: com.allensandiego.movieportal.data.model.PersonCredit, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(100.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(IMAGE_BASE_URL + credit.posterPath)
                .crossfade(true)
                .build(),
            contentDescription = credit.title ?: credit.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(150.dp)
                .width(100.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = credit.title ?: credit.name ?: "",
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = credit.character ?: "",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun EpisodeInfoSection(episodeGroups: List<com.allensandiego.movieportal.data.model.EpisodeGroup>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(episodeGroups) { group ->
            EpisodeGroupItem(group)
        }
    }
}

@Composable
fun EpisodeGroupItem(group: com.allensandiego.movieportal.data.model.EpisodeGroup) {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(200.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = group.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${group.episodeCount} Episodes",
                style = MaterialTheme.typography.bodyMedium
            )
            if (group.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = group.description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

