package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
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

    openCategoryMovieList: (categoryId: String) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset < 100
        }
    }

    LaunchedEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
    }

    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) {
            lazyListState.animateScrollToItem(0)
        }
    }

    Text(screen?._id ?: "No id")

    LazyColumn(
        state = lazyListState, modifier = Modifier
            .fillMaxSize()

    ) {
        screen?.items?.forEach { section ->
            item {
                key(section._id) {
                    DynamicSection(
                        section = section,
                        onMovieClick = onMovieClick,
                        goToVideoPlayer = goToVideoPlayer
                    )
                }
            }
        }
    }

}