package com.allensandiego.movieportal.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.allensandiego.movieportal.ui.viewmodel.TVSeasonDetailsViewModel
import com.allensandiego.movieportal.data.model.Episode
import com.allensandiego.movieportal.data.model.Crew
import com.allensandiego.movieportal.data.model.Season
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
            for (review in reviews.take(5)) {
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

                        if (!details.seasons.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = "Seasons",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            SeasonsSection(details.seasons) { seasonNumber ->
                                navController.navigate(Screen.TVSeasonDetails.createRoute(details.id, seasonNumber))
                            }
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

@Composable
fun SeasonsSection(
    seasons: List<com.allensandiego.movieportal.data.model.Season>,
    onSeasonClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(seasons) { season ->
            SeasonItem(season) {
                onSeasonClick(season.seasonNumber)
            }
        }
    }
}

@Composable
fun SeasonItem(
    season: com.allensandiego.movieportal.data.model.Season,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(end = 8.dp)
            .width(120.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(IMAGE_BASE_URL + season.posterPath)
                .crossfade(true)
                .build(),
            contentDescription = season.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(180.dp)
                .width(120.dp)
                .clip(RoundedCornerShape(8.dp))
        )
        Text(
            text = season.name,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "${season.episodeCount} Episodes",
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.secondary
        )
        if (season.airDate != null) {
            Text(
                text = DateUtils.formatDate(season.airDate),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TVSeasonDetailsScreen(
    navController: NavHostController,
    viewModel: TVSeasonDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedEpisode by remember { mutableStateOf<Episode?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.error != null) {
            ErrorMessage(message = uiState.error ?: "Error loading season details")
        } else {
            val details = uiState.details
            if (details != null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = details.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(details.episodes ?: emptyList()) { episode ->
                            EpisodeCard(episode) {
                                selectedEpisode = episode
                                showBottomSheet = true
                            }
                        }
                    }
                }
            }
        }

        if (showBottomSheet && selectedEpisode != null) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState
            ) {
                EpisodeDetailsContent(selectedEpisode!!) { personId ->
                    showBottomSheet = false
                    navController.navigate(Screen.PersonDetails.createRoute(personId))
                }
            }
        }
    }
}

@Composable
fun EpisodeCard(episode: Episode, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(IMAGE_BASE_URL + episode.stillPath)
                    .crossfade(true)
                    .build(),
                contentDescription = episode.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 300f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.name}",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "⭐ ${String.format("%.1f", episode.voteAverage ?: 0.0)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EpisodeDetailsContent(
    episode: Episode,
    onPersonClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(IMAGE_BASE_URL + episode.stillPath)
                .crossfade(true)
                .build(),
            contentDescription = episode.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = episode.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(text = "Air Date: ${DateUtils.formatDate(episode.airDate)}", style = MaterialTheme.typography.bodyMedium)
        if (episode.runtime != null) {
            Text(text = "Runtime: ${episode.runtime} min", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = episode.overview ?: "No overview available.", style = MaterialTheme.typography.bodyLarge)
        
        if (!episode.guestStars.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Guest Stars", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                val itemWidth = 110.dp
                for (guest in episode.guestStars.take(12)) {
                    CreditCard(
                        name = guest.name,
                        subtext = guest.character,
                        profilePath = guest.profilePath,
                        modifier = Modifier.width(itemWidth),
                        onClick = { onPersonClick(guest.id) }
                    )
                }
            }
        }

        if (!episode.crew.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Crew", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 3
            ) {
                val itemWidth = 110.dp
                for (crewMember in episode.crew.take(12)) {
                    CreditCard(
                        name = crewMember.name,
                        subtext = crewMember.job,
                        profilePath = crewMember.profilePath,
                        modifier = Modifier.width(itemWidth),
                        onClick = { onPersonClick(crewMember.id) }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CreditCard(
    name: String,
    subtext: String,
    profilePath: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(IMAGE_BASE_URL + profilePath)
                    .crossfade(true)
                    .build(),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay for readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 100f
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
