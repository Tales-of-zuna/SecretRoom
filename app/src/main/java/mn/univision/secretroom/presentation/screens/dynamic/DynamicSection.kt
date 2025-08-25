package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.entities.MovieList
import mn.univision.secretroom.data.models.ViewSubItem
import mn.univision.secretroom.presentation.common.Error
import mn.univision.secretroom.presentation.common.FeaturedMoviesCarousel
import mn.univision.secretroom.presentation.common.HighlightMoviesRow
import mn.univision.secretroom.presentation.common.ImmersiveListMoviesRow
import mn.univision.secretroom.presentation.common.ItemDirection
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

        is HomeScreenUiState.Loading -> Loading(modifier = Modifier)
        is HomeScreenUiState.Error -> Error(modifier = Modifier)
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
    var immersiveListHasFocus by remember { mutableStateOf(false) }

    when (section.type?.lowercase()) {
        "list" -> {
            MoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                movieList = trendingMovies,
                title = section.title?.mn ?: "",
                showItemTitle = false,
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

        "tag" -> {
            ImmersiveListMoviesRow(
                modifier = Modifier.padding(top = 16.dp),
                movieList = featuredMovies,
                title = section.title?.mn ?: "",
                itemDirection = ItemDirection.Horizontal,
                onMovieSelected = onMovieClick,
                showItemTitle = false,
                showIndexOverImage = true,
            )
        }
// ene 2 iig veiws json deer nemne
        "banner" -> {
            Text(text = "Banner section")
        }

        "highlight" -> {
            HighlightMoviesRow(
                movieList = top10Movies,
                onMovieClick = onMovieClick,
                modifier = Modifier.onFocusChanged {
                    immersiveListHasFocus = it.hasFocus
                },
            )
        }

        else -> {
            Text(text = "Views json deer nemeh")
        }


    }
}