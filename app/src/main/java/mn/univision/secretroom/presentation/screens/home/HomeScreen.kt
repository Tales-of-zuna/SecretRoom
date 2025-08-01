package mn.univision.secretroom.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@Composable
fun HomeScreen(
//    onMovieClick: (movie: Movie) -> Unit,
//    goToVideoPlayer: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
//    homeScreeViewModel: HomeScreeViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Home Screen",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Welcome to Secret Room!",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}