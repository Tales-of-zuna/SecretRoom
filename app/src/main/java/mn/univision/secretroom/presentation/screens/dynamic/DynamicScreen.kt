package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
    contentManager: DynamicContentManager // Injected from parent
) {
    val lazyListState = rememberLazyListState()

    // Optimize scroll detection
    val shouldShowTopBar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset < 100
        }
    }

    DisposableEffect(shouldShowTopBar) {
        onScroll(shouldShowTopBar)
        onDispose { }
    }

    // Preload visible and next sections
    LaunchedEffect(screen) {
        screen?.items?.let { sections ->
            val preloadSections = sections.take(3).map {
                it._id to it.uri
            }
            contentManager.preloadContent(preloadSections)
        }
    }

    // Track visible items for preloading
    val visibleItemsInfo by remember {
        derivedStateOf { lazyListState.layoutInfo.visibleItemsInfo }
    }

    LaunchedEffect(visibleItemsInfo) {
        val visibleIndices = visibleItemsInfo.map { it.index }
        val maxIndex = visibleIndices.maxOrNull() ?: 0

        // Preload next 2 sections
        screen?.items?.let { sections ->
            val nextSections = sections
                .drop(maxIndex + 1)
                .take(2)
                .map { it._id to it.uri }

            if (nextSections.isNotEmpty()) {
                contentManager.preloadContent(nextSections)
            }
        }
    }

    screen?.let {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 108.dp)
        ) {
            screen.items?.forEach { section ->
                item(
                    key = section._id,
                    contentType = section.type
                ) {
                    DynamicSection(
                        section = section,
                        contentManager = contentManager,
                        onMovieClick = onMovieClick,
                        goToVideoPlayer = goToVideoPlayer
                    )
                }
            }
        }
    }
}
