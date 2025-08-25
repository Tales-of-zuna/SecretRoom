package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.entities.MovieList
import mn.univision.secretroom.data.models.ViewSubItem
import mn.univision.secretroom.presentation.common.Error
import mn.univision.secretroom.presentation.common.FeaturedMoviesCarousel
import mn.univision.secretroom.presentation.common.Loading
import mn.univision.secretroom.presentation.common.MoviesRow
import mn.univision.secretroom.presentation.screens.dashboard.rememberChildPadding
import mn.univision.secretroom.presentation.screens.home.HomeScreenUiState
import mn.univision.secretroom.presentation.screens.home.HomeScreenViewModel

@Composable
fun DynamicSection(
    section: ViewSubItem,
    onMovieClick: (movie: Movie) -> Unit,
    goToVideoPlayer: (movie: Movie) -> Unit,
    homeScreeViewModel: HomeScreenViewModel = hiltViewModel()
) {

    val uiState by homeScreeViewModel.uiState.collectAsStateWithLifecycle()
    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {

            Content(
                section = section,
                featuredMovies = s.featuredMovieList,
                trendingMovies = s.trendingMovieList,
                top10Movies = s.top10MovieList,
                nowPlayingMovies = s.nowPlayingMovieList,
                onMovieClick = onMovieClick,
                onScroll = {},
                goToVideoPlayer = goToVideoPlayer,
                isTopBarVisible = true
            )

        }

        is HomeScreenUiState.Loading -> Loading(modifier = Modifier.fillMaxSize())
        is HomeScreenUiState.Error -> Error(modifier = Modifier.fillMaxSize())
    }
}


@Composable
fun Content(
    section: ViewSubItem,
    featuredMovies: MovieList,
    trendingMovies: MovieList,
    top10Movies: MovieList,
    nowPlayingMovies: MovieList,
    onMovieClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    goToVideoPlayer: (movie: Movie) -> Unit,
    modifier: Modifier = Modifier,
    isTopBarVisible: Boolean = true,
) {
    val childPadding = rememberChildPadding()

    when (section.type?.lowercase()) {
        "list" -> {
            MoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                movieList = trendingMovies,
                title = section.title?.mn ?: "",
                onMovieSelected = onMovieClick
            )
        }

        "carousel" -> {
            FeaturedMoviesCarousel(
                movies = featuredMovies,
                padding = childPadding,
                goToVideoPlayer = goToVideoPlayer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(324.dp)
            )
        }

        "tags" -> {
            Text(text = "Tags")
        }

        else -> {
            Text(text = "No section")
        }


    }
}