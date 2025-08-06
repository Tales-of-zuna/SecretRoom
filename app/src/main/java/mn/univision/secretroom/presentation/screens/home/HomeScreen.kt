package mn.univision.secretroom.presentation.screens.home

import MainCarousel
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mn.univision.secretroom.presentation.screens.layout.rememberChildPadding

@Composable
fun HomeScreen(
//    onMovieClick: (movie: Movie) -> Unit,
//    goToVideoPlayer: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
//    homeScreeViewModel: HomeScreeViewModel = hiltViewModel(),
) {

    val lazyListState = rememberLazyListState()
    val childPadding = rememberChildPadding()
    val shouldShowTopbar by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset < 300
        }
    }

    LaunchedEffect(shouldShowTopbar) {
        onScroll(shouldShowTopbar)
    }

    LaunchedEffect(isTopBarVisible) {
        if (isTopBarVisible) lazyListState.animateScrollToItem(0)
    }

    LazyColumn(state = lazyListState, contentPadding = PaddingValues(bottom = 108.dp)) {
        item(contentType = "Carousel") {
            MainCarousel(
                padding = childPadding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(370.dp)
            )
        }
    }
}



