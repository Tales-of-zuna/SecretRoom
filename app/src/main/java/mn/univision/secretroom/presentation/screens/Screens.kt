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
    Profile,
    Home(isTabItem = true, title = "Нүүр"),
    Categories(isTabItem = true, title = "Ангилалууд"),
    Tvod(isTabItem = true, title = "Кино сан"),
    Svod(isTabItem = true, title = "Багц"),
    Tv(isTabItem = true, title = "ТВ"),

    //    Actors(isTabItem = true, title = "Жүжигчид"),
//    Shop(isTabItem = true, title = "Дэлгүүр"),
    Search,
    CategoryMovieList(listOf(CategoryMovieListScreen.CategoryIdBundleKey)),
    MovieDetails(listOf(MovieDetailsScreen.MovieIdBundleKey)),
    Dashboard,
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
