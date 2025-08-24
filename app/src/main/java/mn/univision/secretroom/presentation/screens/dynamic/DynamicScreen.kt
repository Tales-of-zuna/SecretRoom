package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import mn.univision.secretroom.data.models.ViewItem

@Composable
fun DynamicScreen(
    screen: ViewItem?,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
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
    
    screen?.items?.forEach { viewSubItem ->
        key(viewSubItem._id) {
            DynamicSection(section = viewSubItem)
        }
    }
}