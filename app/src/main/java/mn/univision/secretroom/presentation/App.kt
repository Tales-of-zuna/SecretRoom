package mn.univision.secretroom.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mn.univision.secretroom.presentation.screens.Screens
import mn.univision.secretroom.presentation.screens.search.SearchScreen

@Composable
fun App(
    onBackPressed: () -> Unit,
    // You can add more parameters here if needed, such as a navigation controller or theme settings.
) {

    val navController = rememberNavController()
    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screens.Home(),
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        builder = {
            composable(
                route = Screens.Search()
            ) {
                SearchScreen(
                    onBackPressed = {
                        if (navController.navigateUp()) {
                            isComingBackFromDifferentScreen = true
                        }
                    }
                )
            }
        }

    )
}