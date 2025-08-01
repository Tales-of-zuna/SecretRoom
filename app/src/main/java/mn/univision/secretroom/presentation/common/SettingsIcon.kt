package mn.univision.secretroom.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SelectableSurfaceDefaults
import androidx.tv.material3.Surface

@Composable
fun SettingsIcon(
    selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Surface(
        selected = selected,
        onClick = onClick,
        shape = SelectableSurfaceDefaults.shape(shape = CircleShape),
        border = SelectableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                shape = CircleShape
            ),
            selectedBorder = Border(
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape
            ),
        ),
        scale = SelectableSurfaceDefaults.scale(focusedScale = 1f),
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}