package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mn.univision.secretroom.data.entities.DynamicContent
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.models.ViewSubItem
import mn.univision.secretroom.data.storage.DynamicContentManager
import mn.univision.secretroom.presentation.common.FeaturedMoviesCarousel
import mn.univision.secretroom.presentation.common.ItemDirection
import mn.univision.secretroom.presentation.common.MoviesRow

@Composable
fun DynamicSection(
    section: ViewSubItem,
    contentManager: DynamicContentManager, // Pass from parent
    onMovieClick: (Movie) -> Unit,
    goToVideoPlayer: (Movie) -> Unit,
) {
    val sectionId = remember(section) { section._id }
    val contentStates by contentManager.contentStates.collectAsStateWithLifecycle()
    val contentState = contentStates[sectionId] ?: DynamicContentManager.DynamicSectionState.Initial

    // Load content only once
    LaunchedEffect(sectionId, section.uri) {
        if (contentState is DynamicContentManager.DynamicSectionState.Initial) {
            contentManager.loadContent(sectionId, section.uri)
        }
    }

    when (contentState) {
        is DynamicContentManager.DynamicSectionState.Initial -> {
            // Show placeholder or nothing
        }

        is DynamicContentManager.DynamicSectionState.Loading -> {
            // Use a lightweight placeholder instead of Loading composable
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        is DynamicContentManager.DynamicSectionState.Success -> {
            // Use remember to prevent recreation on recomposition
            val content = remember(contentState) { contentState.content }
            RenderSection(
                section = section,
                content = content,
                onMovieClick = onMovieClick,
                goToVideoPlayer = goToVideoPlayer
            )
        }

        is DynamicContentManager.DynamicSectionState.Error -> {
            // Don't show error for individual sections to avoid UI clutter
        }
    }
}

@Composable
private fun RenderSection(
    section: ViewSubItem,
    content: List<DynamicContent>,
    onMovieClick: (Movie) -> Unit,
    goToVideoPlayer: (Movie) -> Unit
) {
    // Convert only once and remember
    val movieList = remember(content) {
        content.map { dynamicContent ->
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
        }
    }

    // Early return if empty
    if (movieList.isEmpty()) return

    when (section.type?.lowercase()) {
        "carousel" -> {
            FeaturedMoviesCarousel(
                movies = movieList,
                goToVideoPlayer = goToVideoPlayer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )
        }

        "list" -> {
            MoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                movieList = movieList,
                title = section.title?.mn ?: section.name,
                showItemTitle = true,
                onMovieSelected = onMovieClick
            )
        }

        "tag" -> {
            MoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                movieList = movieList,
                title = section.title?.mn ?: section.name,
                itemDirection = ItemDirection.Horizontal,
                onMovieSelected = onMovieClick,
                showItemTitle = false,
                showIndexOverImage = true,
            )
        }

        "banner" -> {
            FeaturedMoviesCarousel(
                movies = movieList.take(1),
                goToVideoPlayer = goToVideoPlayer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
        }

        else -> {
            MoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                movieList = movieList,
                title = section.title?.mn ?: section.name,
                onMovieSelected = onMovieClick
            )
        }
    }
}
