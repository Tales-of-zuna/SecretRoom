package mn.univision.secretroom.presentation.screens.tvod

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.presentation.screens.movies.MoviesScreenViewModel

@Composable
fun TvodScreen(
    onMovieClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    moviesScreenViewModel: MoviesScreenViewModel = hiltViewModel(),
) {
}