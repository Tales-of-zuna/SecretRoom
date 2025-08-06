package mn.univision.secretroom.presentation.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screens(
    private val args: List<String>? = null,
    val isTabItem: Boolean = false,
    val tabIcon: ImageVector? = null,
    val displayName: String = "",
) {
    LayoutScreen,
    Home(isTabItem = true, displayName = "Нүүр"),
    Movies(isTabItem = true, displayName = "Кино сан"),
    Bandles(isTabItem = true, displayName = "Багц"),
    TV(isTabItem = true, displayName = "ТВ"),
    Actors(isTabItem = true, displayName = "Жүжигчид"),
    Commerce(isTabItem = true, displayName = "Дэлгүүр"),
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
