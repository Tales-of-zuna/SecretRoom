package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mn.univision.secretroom.data.entities.DynamicContent
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.entities.MovieList
import mn.univision.secretroom.data.models.ViewSubItem
import mn.univision.secretroom.presentation.common.Error
import mn.univision.secretroom.presentation.common.FeaturedMoviesCarousel
import mn.univision.secretroom.presentation.common.ItemDirection
import mn.univision.secretroom.presentation.common.Loading
import mn.univision.secretroom.presentation.common.MoviesRow

@Composable
fun DynamicSection(
    section: ViewSubItem,
    onMovieClick: (Movie) -> Unit,
    goToVideoPlayer: (Movie) -> Unit,
    viewModel: DynamicSectionViewModel = hiltViewModel()
) {
    val contentState by viewModel.contentState.collectAsStateWithLifecycle()

    // Load content when the composable is first displayed
    LaunchedEffect(section.uri) {
        viewModel.loadContent(section.uri)
    }

    when (contentState) {
        is DynamicSectionViewModel.DynamicSectionState.Initial -> {
            // Do nothing, waiting to load
        }

        is DynamicSectionViewModel.DynamicSectionState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Loading(modifier = Modifier)
            }
        }

        is DynamicSectionViewModel.DynamicSectionState.Success -> {
            RenderSection(
                section = section,
                content = (contentState as DynamicSectionViewModel.DynamicSectionState.Success).content,
                onMovieClick = onMovieClick,
                goToVideoPlayer = goToVideoPlayer
            )
        }

        is DynamicSectionViewModel.DynamicSectionState.Error -> {
            Error(modifier = Modifier.padding(16.dp))
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
    val movieList: MovieList = content.map { dynamicContent ->
        Movie(
            id = dynamicContent.id,
            videoUri = dynamicContent.deepLink ?: "",
            subtitleUri = null,
            posterUri = dynamicContent.posterVertical ?: dynamicContent.posterHorizontal ?: "",
            name = dynamicContent.name,
            description = dynamicContent.description
        )
    }

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
            // Implement banner section
            if (movieList.isNotEmpty()) {
                FeaturedMoviesCarousel(
                    movies = movieList.take(1),
                    goToVideoPlayer = goToVideoPlayer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )
            }
        }

        else -> {
            // Fallback to list
            MoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                movieList = movieList,
                title = section.title?.mn ?: section.name,
                onMovieSelected = onMovieClick
            )
        }
    }
}