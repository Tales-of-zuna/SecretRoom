package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.tv.material3.Text
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.models.ViewItem

@Composable
fun DynamicScreen(
    screen: ViewItem?,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    goToVideoPlayer: (Movie) -> Unit,
    isTopBarVisible: Boolean,
    onMovieClick: (Movie) -> Unit,
    openCategoryMovieList: (categoryId: String) -> Unit
) {
    val lazyColumnState = rememberLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyColumnState.firstVisibleItemIndex == 0 &&
                    lazyColumnState.firstVisibleItemScrollOffset < 100
        }
    }
    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    Text(screen?._id ?: "No id")

    Column {
        Content(
            screen = screen,
            onMovieClick = onMovieClick,
            goToVideoPlayer = goToVideoPlayer,
            modifier = Modifier.fillMaxSize()
        )
    }

}

@Composable
fun Content(
    screen: ViewItem?,
    onMovieClick: (Movie) -> Unit,
    goToVideoPlayer: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    screen?.items?.forEach { viewSubItem ->
        key(viewSubItem._id) {
            DynamicSection(
                section = viewSubItem,
                onMovieClick = onMovieClick,
                goToVideoPlayer = goToVideoPlayer
            )
        }
    }
}