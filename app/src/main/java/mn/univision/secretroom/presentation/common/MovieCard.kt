package mn.univision.secretroom.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.StandardCardContainer
import androidx.tv.material3.Surface
import mn.univision.secretroom.presentation.theme.SecretRoomBorderWidth
import mn.univision.secretroom.presentation.theme.SecretRoomCardShape

@Composable
fun MovieCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    image: @Composable BoxScope.() -> Unit,
) {
    StandardCardContainer(
        modifier = modifier,
        title = title,
        imageCard = {
            Surface(
                onClick = onClick,
                shape = ClickableSurfaceDefaults.shape(SecretRoomCardShape),
                border = ClickableSurfaceDefaults.border(
                    focusedBorder = Border(
                        border = BorderStroke(
                            width = SecretRoomBorderWidth,
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = SecretRoomCardShape
                    )
                ),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1f),
                content = image
            )
        },
    )
}