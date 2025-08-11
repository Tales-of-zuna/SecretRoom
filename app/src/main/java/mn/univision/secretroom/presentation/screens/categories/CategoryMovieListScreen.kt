

package mn.univision.secretroom.presentation.screens.categories

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.data.entities.MovieCategoryDetails
import mn.univision.secretroom.presentation.common.Error
import mn.univision.secretroom.presentation.common.Loading
import mn.univision.secretroom.presentation.common.MovieCard
import mn.univision.secretroom.presentation.common.PosterImage
import mn.univision.secretroom.presentation.screens.dashboard.rememberChildPadding
import mn.univision.secretroom.presentation.theme.SecretRoomBottomListPadding
import mn.univision.secretroom.presentation.utils.focusOnInitialVisibility

object CategoryMovieListScreen {
    const val CategoryIdBundleKey = "categoryId"
}

@Composable
fun CategoryMovieListScreen(
    onBackPressed: () -> Unit,
    onMovieSelected: (Movie) -> Unit,
    categoryMovieListScreenViewModel: CategoryMovieListScreenViewModel = hiltViewModel()
) {
    val uiState by categoryMovieListScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        CategoryMovieListScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }

        CategoryMovieListScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }

        is CategoryMovieListScreenUiState.Done -> {
            val categoryDetails = s.movieCategoryDetails
            CategoryDetails(
                categoryDetails = categoryDetails,
                onBackPressed = onBackPressed,
                onMovieSelected = onMovieSelected
            )
        }
    }
}

@Composable
private fun CategoryDetails(
    categoryDetails: MovieCategoryDetails,
    onBackPressed: () -> Unit,
    onMovieSelected: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    val childPadding = rememberChildPadding()
    val isFirstItemVisible = remember { mutableStateOf(false) }

    BackHandler(onBack = onBackPressed)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = categoryDetails.name,
            style = MaterialTheme.typography.displaySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(
                vertical = childPadding.top.times(3.5f)
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            contentPadding = PaddingValues(bottom = SecretRoomBottomListPadding)
        ) {
            itemsIndexed(
                categoryDetails.movies,
                key = { _, movie ->
                    movie.id
                }
            ) { index, movie ->
                MovieCard(
                    onClick = { onMovieSelected(movie) },
                    modifier = Modifier
                        .aspectRatio(1 / 1.5f)
                        .padding(8.dp)
                        .then(
                            if (index == 0)
                                Modifier.focusOnInitialVisibility(isFirstItemVisible)
                            else Modifier
                        ),
                ) {
                    PosterImage(movie = movie, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
