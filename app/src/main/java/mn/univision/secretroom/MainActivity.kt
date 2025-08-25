package mn.univision.secretroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import dagger.hilt.android.AndroidEntryPoint
import mn.univision.secretroom.presentation.App
import mn.univision.secretroom.presentation.AuthViewModel
import mn.univision.secretroom.presentation.common.Loading
import mn.univision.secretroom.presentation.theme.SecretRoomButtonShape
import mn.univision.secretroom.presentation.theme.SecretRoomTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setContent {
            SecretRoomTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                ) {
                    CompositionLocalProvider(
                        LocalContentColor provides MaterialTheme.colorScheme.onSurface
                    ) {
                        AuthenticationWrapper(
                            authViewModel = authViewModel,
                            onAuthenticated = {
                                App(
                                    onBackPressed = onBackPressedDispatcher::onBackPressed,
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuthenticationWrapper(
    authViewModel: AuthViewModel,
    onAuthenticated: @Composable () -> Unit
) {
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthViewModel.AuthState.Initial -> {
            authViewModel.authenticate()
            AuthenticationLoadingScreen()
        }

        is AuthViewModel.AuthState.Loading -> {
            AuthenticationLoadingScreen()
        }

        is AuthViewModel.AuthState.Success -> {
            onAuthenticated()
        }

        is AuthViewModel.AuthState.Error -> {
            AuthenticationErrorScreen(
                errorMessage = (authState as AuthViewModel.AuthState.Error).message,
                onRetry = { authViewModel.retryAuthentication() }
            )
        }
    }
}

@Composable
fun AuthenticationLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Loading()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Authenticating device...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun AuthenticationErrorScreen(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Нэвтрэхэд алдаа гарлаа",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetry,
                shape = ButtonDefaults.shape(shape = SecretRoomButtonShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Text("Дахин оролдох")
            }
        }
    }
}