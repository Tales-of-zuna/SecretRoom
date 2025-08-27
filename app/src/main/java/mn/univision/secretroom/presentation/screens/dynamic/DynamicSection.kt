package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.models.ViewSubItem
import mn.univision.secretroom.data.storage.DynamicContentManager
import mn.univision.secretroom.presentation.common.FeaturedMoviesCarousel
import mn.univision.secretroom.presentation.common.ItemDirection
import mn.univision.secretroom.presentation.common.MoviesRow

@Composable
fun DynamicSection(
    section: ViewSubItem,
    contentManager: DynamicContentManager,
    onMovieClick: (Movie) -> Unit,
    goToVideoPlayer: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val sectionId = remember(section) { section._id }
    val contentStates by contentManager.contentStates.collectAsStateWithLifecycle()
    val contentState = remember(contentStates, sectionId) {
        contentStates[sectionId] ?: DynamicContentManager.ContentState.Initial
    }

    // Load content with proper lifecycle management
    LaunchedEffect(sectionId, section.uri) {
        if (contentState is DynamicContentManager.ContentState.Initial) {
            contentManager.loadContent(sectionId, section.uri)
        }
    }

    // Handle cleanup when section is no longer visible
    DisposableEffect(sectionId) {
        onDispose {
            // Optional: Clear section state after some delay to free memory
            // contentManager.clearSection(sectionId)
        }
    }

    when (contentState) {
        is DynamicContentManager.ContentState.Initial -> {
            // Minimal placeholder
            Box(modifier = modifier.height(1.dp))
        }

        is DynamicContentManager.ContentState.Loading -> {
            LoadingPlaceholder(
                sectionType = section.type,
                modifier = modifier
            )
        }

        is DynamicContentManager.ContentState.Success -> {
            val movieList = remember(contentState.content) {
                contentState.content.mapNotNull { dynamicContent ->
                    if (dynamicContent.name.isNotBlank()) {
                        Movie(
                            id = dynamicContent.id,
                            videoUri = dynamicContent.deepLink ?: "",
                            subtitleUri = null,
                            posterUri = dynamicContent.posterVertical
                                ?: dynamicContent.posterHorizontal
                                ?: "",
                            name = dynamicContent.name,
                            description = dynamicContent.description
                        )
                    } else null
                }
            }

            if (movieList.isNotEmpty()) {
                RenderSection(
                    section = section,
                    movieList = movieList,
                    onMovieClick = onMovieClick,
                    goToVideoPlayer = goToVideoPlayer,
                    modifier = modifier
                )
            }
        }

        is DynamicContentManager.ContentState.Error -> {
            if (contentState.canRetry) {
                ErrorWithRetry(
                    message = contentState.message,
                    onRetry = { contentManager.retryContent(sectionId, section.uri) },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun LoadingPlaceholder(
    sectionType: String?,
    modifier: Modifier = Modifier
) {
    val height = when (sectionType?.lowercase()) {
        "carousel", "banner" -> 400.dp
        else -> 200.dp
    }

    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    )
}

@Composable
private fun ErrorWithRetry(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = onRetry
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Failed to load content",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                text = "Tap to retry",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun RenderSection(
    section: ViewSubItem,
    movieList: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    goToVideoPlayer: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    when (section.type?.lowercase()) {
        "carousel" -> {
            FeaturedMoviesCarousel(
                movies = movieList.take(5), // Limit carousel items
                goToVideoPlayer = goToVideoPlayer,
                modifier = modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        }

        "list" -> {
            MoviesRow(
                modifier = modifier.padding(top = 16.dp),
                movieList = movieList.take(20), // Limit list items
                title = section.title?.mn ?: section.name,
                showItemTitle = true,
                onMovieSelected = onMovieClick
            )
        }

        "tag" -> {
            MoviesRow(
                modifier = modifier.padding(top = 16.dp),
                movieList = movieList.take(10), // Limit tag items
                title = section.title?.mn ?: section.name,
                itemDirection = ItemDirection.Horizontal,
                onMovieSelected = onMovieClick,
                showItemTitle = false,
                showIndexOverImage = true,
            )
        }

        "banner" -> {
            if (movieList.isNotEmpty()) {
                FeaturedMoviesCarousel(
                    movies = listOf(movieList.first()),
                    goToVideoPlayer = goToVideoPlayer,
                    modifier = modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }
        }

        else -> {
            MoviesRow(
                modifier = modifier.padding(top = 16.dp),
                movieList = movieList.take(15), // Default limit
                title = section.title?.mn ?: section.name,
                onMovieSelected = onMovieClick
            )
        }
    }
}
