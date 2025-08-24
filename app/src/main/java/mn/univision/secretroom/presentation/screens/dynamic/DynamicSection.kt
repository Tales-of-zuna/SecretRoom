package mn.univision.secretroom.presentation.screens.dynamic

import androidx.compose.runtime.Composable
import androidx.tv.material3.Text
import mn.univision.secretroom.data.models.ViewSubItem

@Composable
fun DynamicSection(
    section: ViewSubItem
) {
    Text(section.name.toString())
}