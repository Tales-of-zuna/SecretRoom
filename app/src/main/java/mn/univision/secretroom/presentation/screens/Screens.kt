package mn.univision.secretroom.presentation.screens

import androidx.compose.ui.graphics.vector.ImageVector
import mn.univision.secretroom.presentation.screens.categories.CategoryMovieListScreen
import mn.univision.secretroom.presentation.screens.movies.MovieDetailsScreen
import mn.univision.secretroom.presentation.screens.videoPlayer.VideoPlayerScreen

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null,
    val title: String? = null
) {

    DynamicScreen(listOf("screenId"), isTabItem = true),
    Profile,
    Search,
    Dashboard,
    CategoryMovieList(listOf(CategoryMovieListScreen.CategoryIdBundleKey)),
    MovieDetails(listOf(MovieDetailsScreen.MovieIdBundleKey)),
    VideoPlayer(listOf(VideoPlayerScreen.MovieIdBundleKey));

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
