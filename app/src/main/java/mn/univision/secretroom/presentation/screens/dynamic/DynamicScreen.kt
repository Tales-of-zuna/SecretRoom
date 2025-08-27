package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.models.ViewItem
import mn.univision.secretroom.data.storage.DynamicContentManager

@Composable
fun DynamicScreen(
    screen: ViewItem?,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    goToVideoPlayer: (Movie) -> Unit,
    isTopBarVisible: Boolean,
    onMovieClick: (Movie) -> Unit,
    openCategoryMovieList: (categoryId: String) -> Unit,
    contentManager: DynamicContentManager
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

    val visibleItemsInfo by remember {
        derivedStateOf {
            lazyListState.layoutInfo.visibleItemsInfo.map { it.index }.toSet()
        }
    }

    LaunchedEffect(visibleItemsInfo) {
        screen?.items?.let { sections ->
            val maxVisibleIndex = visibleItemsInfo.maxOrNull() ?: 0
            val preloadRange = (maxVisibleIndex + 1)..(maxVisibleIndex + 3)

            val sectionsToPreload = sections
                .filterIndexed { index, _ -> index in preloadRange }
                .map { it._id to it.uri }
                .filter { (_, uri) -> !uri.isNullOrEmpty() }

            if (sectionsToPreload.isNotEmpty()) {
                contentManager.preloadContent(sectionsToPreload)
            }
        }
    }

    screen?.let {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 108.dp)
        ) {
            screen.items?.let { sections ->
                items(
                    count = sections.size,
                    key = { index -> sections[index]._id },
                    contentType = { index -> sections[index].type }
                ) { index ->
                    val section = sections[index]
                    DynamicSection(
                        section = section,
                        contentManager = contentManager,
                        onMovieClick = onMovieClick,
                        goToVideoPlayer = goToVideoPlayer,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
