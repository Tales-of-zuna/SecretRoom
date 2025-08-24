package mn.univision.secretroom.presentation.screens.svod


//
//@Composable
//fun SvodScreen(
//    viewModel: DynamicContentViewModel = hiltViewModel()
//) {
//    val viewsState by viewModel.viewsState.collectAsStateWithLifecycle()
//
//    when (viewsState) {
//        is ViewsState.Loading -> {
//            // Show loading
//        }
//
//        is ViewsState.Success -> {
//            DynamicContentList(views = (viewsState as ViewsState.Success).views)
//        }
//
//        is ViewsState.Error -> {
//            // Show error
//        }
//    }
//}
//
//@Composable
//fun DynamicContentList(views: List<ViewItem>) {
//    LazyColumn(
//        modifier = Modifier.fillMaxSize(),
//        contentPadding = PaddingValues(16.dp),
//        verticalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        items(views) { view ->
//            DynamicViewCard(view = view)
//        }
//    }
//}
//
//@Composable
//fun DynamicViewCard(view: ViewItem) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        onClick = { /* Handle card click */ }
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = view.name,
//                style = MaterialTheme.typography.headlineMedium
//            )
//
//            view.title?.let { title ->
//                Text(
//                    text = title.mn ?: title.en ?: "",
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            }
//
//            view.items?.forEach { item ->
//                DynamicSubItem(item = item)
//            }
//        }
//    }
//}
//
//@Composable
//fun DynamicSubItem(item: ViewSubItem) {
//    when (item.type) {
//        "movie" -> MovieItemView(item)
//    }
//}
//
//@Composable
//fun MovieItemView(item: ViewSubItem) {
//    // Render movie item based on ViewSubItem data
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 4.dp),
//        onClick = { /* Handle movie click */ }
//    ) {
//        Row(
//            modifier = Modifier.padding(12.dp)
//        ) {
//            // Movie thumbnail placeholder
//            Box(
//                modifier = Modifier
//                    .size(60.dp)
//                    .background(Color.Gray)
//            )
//
//            Column(
//                modifier = Modifier
//                    .padding(start = 12.dp)
//                    .weight(1f)
//            ) {
//                Text(
//                    text = item.title?.mn ?: item.name ?: "Unknown",
//                    style = MaterialTheme.typography.titleMedium
//                )
//
//                item.uri?.let { uri ->
//                    Text(
//                        text = "URI: $uri",
//                        style = MaterialTheme.typography.bodySmall,
//                        modifier = Modifier.padding(top = 4.dp)
//                    )
//                }
//            }
//        }
//    }
//}