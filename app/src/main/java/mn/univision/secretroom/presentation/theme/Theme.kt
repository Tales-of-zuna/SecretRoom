package mn.univision.secretroom.presentation.theme

import androidx.compose.runtime.Composable
import androidx.tv.material3.MaterialTheme

@Composable
fun SecretRoomTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = darkColorScheme,
        shapes = MaterialTheme.shapes,
        typography = Typography,
        content = content
    )
}