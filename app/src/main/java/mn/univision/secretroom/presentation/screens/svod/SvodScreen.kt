package mn.univision.secretroom.presentation.screens.svod


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import mn.univision.secretroom.data.models.ViewItem
import mn.univision.secretroom.data.models.ViewSubItem

@Composable
fun SvodScreen(
    viewModel: DynamicContentViewModel = hiltViewModel()
) {
    val viewsState by viewModel.viewsState.collectAsStateWithLifecycle()

    when (viewsState) {
        is ViewsState.Loading -> {
            // Show loading
        }

        is ViewsState.Success -> {
            DynamicContentList(views = (viewsState as ViewsState.Success).views)
        }

        is ViewsState.Error -> {
            // Show error
        }
    }
}

@Composable
fun DynamicContentList(views: List<ViewItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(views) { view ->
            DynamicViewCard(view = view)
        }
    }
}

@Composable
fun DynamicViewCard(view: ViewItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* Handle card click */ }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = view.name,
                style = MaterialTheme.typography.headlineMedium
            )

            view.title?.let { title ->
                Text(
                    text = title.mn ?: title.en ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            view.items?.forEach { item ->
                DynamicSubItem(item = item)
            }
        }
    }
}

@Composable
fun DynamicSubItem(item: ViewSubItem) {
    when (item.type) {
        "movie" -> MovieItemView(item)
    }
}

@Composable
fun MovieItemView(item: ViewSubItem) {
    // Render movie item based on ViewSubItem data
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { /* Handle movie click */ }
    ) {
        Row(
            modifier = Modifier.padding(12.dp)
        ) {
            // Movie thumbnail placeholder
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color.Gray)
            )

            Column(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = item.title?.mn ?: item.name ?: "Unknown",
                    style = MaterialTheme.typography.titleMedium
                )

                item.uri?.let { uri ->
                    Text(
                        text = "URI: $uri",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}