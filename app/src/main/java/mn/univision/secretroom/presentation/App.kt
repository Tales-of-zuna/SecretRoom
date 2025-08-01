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
import mn.univision.secretroom.presentation.screens.layout.LayoutScreen

@Composable
fun App(
    onBackPressed: () -> Unit,
) {

    val navController = rememberNavController()
    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }

    NavHost(
        navController = navController,
        startDestination = Screens.LayoutScreen(),
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        builder = {
            composable(
                route = Screens.LayoutScreen()
            ) {
                LayoutScreen(
                    onBackPressed = onBackPressed,
                    isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
                    resetIsComingBackFromDifferentScreen = {
                        isComingBackFromDifferentScreen = false
                    }

                )
            }
        }
    )
}