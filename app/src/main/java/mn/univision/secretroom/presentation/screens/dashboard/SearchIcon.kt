package mn.univision.secretroom.presentation.screens.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.tv.material3.Border
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.SelectableSurfaceDefaults
import androidx.tv.material3.Surface
import mn.univision.secretroom.presentation.theme.SecretRoomBorderWidth

@Composable
fun SearchIcon(
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Surface(
        selected = selected,
        onClick = onClick,
        shape = SelectableSurfaceDefaults.shape(shape = CircleShape),
        border = SelectableSurfaceDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = SecretRoomBorderWidth,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                shape = CircleShape
            ),
            selectedBorder = Border(
                border = BorderStroke(
                    width = SecretRoomBorderWidth,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape
            ),
        ),
        scale = SelectableSurfaceDefaults.scale(focusedScale = 1f),
        modifier = modifier
            .alpha(if (selected || isFocused) 1f else 0.6f)
            .onFocusChanged {
                isFocused = it.isFocused || it.hasFocus
            },
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
    }
}