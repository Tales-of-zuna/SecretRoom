package mn.univision.secretroom.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null
) {
    LayoutScreen,
    Home(isTabItem = true),
    Movies(isTabItem = true),
    Bandles(isTabItem = true),
    TV(isTabItem = true),
    Actors(isTabItem = true),
    Commerce(isTabItem = true),
    Search(tabIcon = Icons.Default.Search),
    Settings(tabIcon = Icons.Default.Settings);
//    CategoryMovieList(listOf(CategoryMovieListScreen.CategoryIdBundleKey)),
//    MovieDetails(listOf(MovieDetailsScreen.MovieIdBundleKey)),
//    VideoPlayer(listOf(VideoPlayerScreen.MovieIdBundleKey));

    operator fun invoke(): String {
        val argList = StringBuilder()
        args?.let { nnArgs ->
            nnArgs.forEach { arg -> argList.append("/{$arg}") }
        }
        return name + argList
    }

    fun withArgs(vararg args: Any): String {
        val destination = StringBuilder()
        args.forEach { arg -> destination.append("/$arg") }
        return name + destination
    }
}
