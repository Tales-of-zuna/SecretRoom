package mn.univision.secretroom.presentation.screens.dashboard

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mn.univision.secretroom.data.entities.Movie
import mn.univision.secretroom.presentation.screens.Screens
import mn.univision.secretroom.presentation.screens.categories.CategoriesScreen
import mn.univision.secretroom.presentation.screens.home.HomeScreen
import mn.univision.secretroom.presentation.screens.profile.ProfileScreen
import mn.univision.secretroom.presentation.screens.search.SearchScreen
import mn.univision.secretroom.presentation.screens.tv.TvScreen
import mn.univision.secretroom.presentation.screens.tvod.TvodScreen
import mn.univision.secretroom.presentation.utils.Padding

val ParentPadding = PaddingValues(vertical = 16.dp, horizontal = 58.dp)

@Composable
fun rememberChildPadding(direction: LayoutDirection = LocalLayoutDirection.current): Padding {
    return remember {
        Padding(
            start = ParentPadding.calculateStartPadding(direction) + 8.dp,
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(direction) + 8.dp,
            bottom = ParentPadding.calculateBottomPadding()
        )
    }
}

@Composable
fun DashboardScreen(
    openCategoryMovieList: (categoryId: String) -> Unit,
    openMovieDetailsScreen: (movieId: String) -> Unit,
    openVideoPlayer: (Movie) -> Unit,
    isComingBackFromDifferentScreen: Boolean,
    resetIsComingBackFromDifferentScreen: () -> Unit,
    onBackPressed: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val navController = rememberNavController()
    var isTopBarVisible by remember { mutableStateOf(true) }
    var isTopBarFocused by remember { mutableStateOf(false) }
    val viewsState by viewModel.viewsState.collectAsStateWithLifecycle()
    var currentDestination: String? by remember { mutableStateOf(null) }
    val currentTopBarSelectedTabIndex by remember(currentDestination) {
        derivedStateOf {
            currentDestination?.let { TopBarTabs.indexOf(Screens.valueOf(it)) } ?: 0
        }
    }

    DisposableEffect(Unit) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination.route
        }

        navController.addOnDestinationChangedListener(listener)

        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    BackPressHandledArea(
        onBackPressed = {
            if (!isTopBarVisible) {
                isTopBarVisible = true
                TopBarFocusRequesters[currentTopBarSelectedTabIndex + 1].requestFocus()
            } else if (currentTopBarSelectedTabIndex == 0) onBackPressed()
            else if (!isTopBarFocused) {
                TopBarFocusRequesters[currentTopBarSelectedTabIndex + 1].requestFocus()
            } else TopBarFocusRequesters[1].requestFocus()
        }
    ) {
        var wasTopBarFocusRequestedBefore by rememberSaveable { mutableStateOf(false) }
        var topBarHeightPx: Int by rememberSaveable { mutableIntStateOf(0) }
        val topBarYOffsetPx by animateIntAsState(
            targetValue = if (isTopBarVisible) 0 else -topBarHeightPx,
            animationSpec = tween(),
            label = "",
            finishedListener = {
                if (it == -topBarHeightPx && isComingBackFromDifferentScreen) {
                    focusManager.moveFocus(FocusDirection.Down)
                    resetIsComingBackFromDifferentScreen()
                }
            }
        )
        val navHostTopPaddingDp by animateDpAsState(
            targetValue = if (isTopBarVisible) with(density) { topBarHeightPx.toDp() } else 0.dp,
            animationSpec = tween(),
            label = "",
        )

        LaunchedEffect(Unit) {
            if (!wasTopBarFocusRequestedBefore) {
                TopBarFocusRequesters[currentTopBarSelectedTabIndex + 1].requestFocus()
                wasTopBarFocusRequestedBefore = true
            }
        }

        val gradientBrush = Brush.verticalGradient(
            colors = listOf(
                Color.Black,
                Color.Transparent
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        when (viewsState) {
            is ViewsState.Loading -> {
                // Show loading
            }

            is ViewsState.Success -> {
                DashboardTopBar(
                    views = (viewsState as ViewsState.Success).views,
                    modifier = Modifier
                        .offset { IntOffset(x = 0, y = topBarYOffsetPx) }
                        .onSizeChanged { topBarHeightPx = it.height }
                        .onFocusChanged { isTopBarFocused = it.hasFocus }
                        .background(gradientBrush)
                        .padding(
                            horizontal = ParentPadding.calculateStartPadding(
                                LocalLayoutDirection.current
                            ) + 8.dp
                        )
                        .padding(
                            top = ParentPadding.calculateTopPadding(),
                            bottom = ParentPadding.calculateBottomPadding()
                        ),
                    selectedTabIndex = currentTopBarSelectedTabIndex,
                ) { screen ->
                    navController.navigate(screen()) {
                        if (screen == TopBarTabs[0]) popUpTo(TopBarTabs[0].invoke())
                        launchSingleTop = true
                    }
                }

                Body(
                    openCategoryMovieList = openCategoryMovieList,
                    openMovieDetailsScreen = openMovieDetailsScreen,
                    openVideoPlayer = openVideoPlayer,
                    updateTopBarVisibility = { isTopBarVisible = it },
                    isTopBarVisible = isTopBarVisible,
                    navController = navController,
                    modifier = Modifier.offset(y = navHostTopPaddingDp),
                )
            }

            is ViewsState.Error -> {
                // Show error
            }
        }


    }
}

@Composable
private fun BackPressHandledArea(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) =
    Box(
        modifier = Modifier
            .onPreviewKeyEvent {
                if (it.key == Key.Back && it.type == KeyEventType.KeyUp) {
                    onBackPressed()
                    true
                } else {
                    false
                }
            }
            .then(modifier),
        content = content
    )

@Composable
private fun Body(
    openCategoryMovieList: (categoryId: String) -> Unit,
    openMovieDetailsScreen: (movieId: String) -> Unit,
    openVideoPlayer: (Movie) -> Unit,
    updateTopBarVisibility: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    isTopBarVisible: Boolean = true,
) =
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Home(),
    ) {
        composable(Screens.Profile()) {
            ProfileScreen()
        }
        composable(Screens.Home()) {
            HomeScreen(
                onMovieClick = { selectedMovie ->
                    openMovieDetailsScreen(selectedMovie.id)
                },
                goToVideoPlayer = openVideoPlayer,
                onScroll = updateTopBarVisibility,
                isTopBarVisible = isTopBarVisible
            )
        }
        composable(Screens.Categories()) {
            CategoriesScreen(
                onCategoryClick = openCategoryMovieList,
                onScroll = updateTopBarVisibility
            )
        }
        composable(Screens.Tvod()) {
            TvodScreen(
                onMovieClick = { movie -> openMovieDetailsScreen(movie.id) },
                onScroll = updateTopBarVisibility,
                isTopBarVisible = isTopBarVisible
            )
        }
//        composable(Screens.Svod()) {
//            SvodScreen(
//                onMovieClick = { movie -> openMovieDetailsScreen(movie.id) },
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
        composable(Screens.Tv()) {
            TvScreen(
                onMovieClick = { movie -> openMovieDetailsScreen(movie.id) },
                onScroll = updateTopBarVisibility,
                isTopBarVisible = isTopBarVisible
            )
        }
//        composable(Screens.Actors()) {
//            ActorsScreen(
//            )
//        }
//        composable(Screens.Shop()) {
//            ShopScreen()
//        }
//        composable(Screens.Movies()) {
//            MoviesScreen(
//                onMovieClick = { movie -> openMovieDetailsScreen(movie.id) },
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
//        composable(Screens.Shows()) {
//            ShowsScreen(
//                onTVShowClick = { movie -> openMovieDetailsScreen(movie.id) },
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
//        composable(Screens.Favourites()) {
//            FavouritesScreen(
//                onMovieClick = openMovieDetailsScreen,
//                onScroll = updateTopBarVisibility,
//                isTopBarVisible = isTopBarVisible
//            )
//        }
        composable(Screens.Search()) {
            SearchScreen(
                onMovieClick = { movie -> openMovieDetailsScreen(movie.id) },
                onScroll = updateTopBarVisibility
            )
        }
    }
