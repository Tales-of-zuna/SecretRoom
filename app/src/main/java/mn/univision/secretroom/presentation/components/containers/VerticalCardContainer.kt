package mn.univision.secretroom.presentation.components.containers

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun VerticalCardContainer(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
    ) {
        content()
    }
}