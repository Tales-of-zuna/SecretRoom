package mn.univision.secretroom.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import mn.univision.secretroom.R
import mn.univision.secretroom.data.util.StringConstants
import mn.univision.secretroom.presentation.screens.Screens
import mn.univision.secretroom.presentation.utils.occupyScreenSize

val TopBarTabs = Screens.entries.toList().filter { it.isTabItem }
val TopbarFocusRequesters = List(TopBarTabs.size + 2) { FocusRequester() }
private const val SEARCH_SCREEN_INDEX = -2
private const val SETTINGS_SCREEN_INDEX = -1

@Composable
fun Navbar(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    screens: List<Screens> = TopBarTabs,
    focusRequesters: List<FocusRequester> = remember { TopbarFocusRequesters },
    onScreenSelection: (screen: Screens) -> Unit
) {

    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(MaterialTheme.colorScheme.surface)
                .focusRestorer(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier) {
                AsyncImage(
                    model = R.drawable.logo,
                    modifier = Modifier
                        .size(24.dp),
                    contentDescription = "logo"
                )
                Text(
                    text = "НУУЦ ӨРӨӨ",
                    style = MaterialTheme.typography.titleMedium
                        .copy(color = Color(0xFFe8245c)),
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isTabRowFocused by remember { mutableStateOf(false) }

                Spacer(modifier = Modifier.weight(1f))
                TabRow(modifier = Modifier.onFocusChanged {
                    isTabRowFocused = it.isFocused || it.hasFocus
                }, selectedTabIndex = selectedTabIndex, indicator = { tabPositions, _ ->
                    if (selectedTabIndex >= 0) {
                        NavBarItemIndicator(
                            currentTabPosition = tabPositions[selectedTabIndex],
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                            anyTabFocused = isTabRowFocused,
                            shape = MaterialTheme.shapes.large,
                        )
                    }

                }, separator = {
                    Spacer(modifier = Modifier)
                }) {

                    screens.forEachIndexed { index, screen ->
                        key(index) {
                            Tab(
                                modifier = Modifier
                                    .height(32.dp)
                                    .focusRequester(
                                        focusRequesters[index + 2]
                                    ),
                                selected = index == selectedTabIndex,
                                onFocus = { onScreenSelection(screen) },
                                onClick = {
                                    focusManager.moveFocus(FocusDirection.Down)
                                },
                            ) {

                                if (screen.tabIcon != null) {
                                    Icon(
                                        screen.tabIcon,
                                        modifier = Modifier.padding(4.dp),
                                        contentDescription = StringConstants.Composable
                                            .ContentDescription.DashboardSearchButton,
                                        tint = LocalContentColor.current
                                    )
                                } else {
                                    Text(
                                        modifier = Modifier
                                            .occupyScreenSize()
                                            .padding(horizontal = 16.dp),
                                        text = screen.displayName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = LocalContentColor.current
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    SearchIcon(
                        modifier = Modifier
                            .size(24.dp),

                        selected = selectedTabIndex == SEARCH_SCREEN_INDEX,
                        onClick = {
                            onScreenSelection(Screens.Search)
                        }
                    )
                    Spacer(modifier = Modifier.width(20.dp))
                    SettingsIcon(
                        modifier = Modifier
                            .size(24.dp),

                        selected = selectedTabIndex == SETTINGS_SCREEN_INDEX,
                        onClick = {
                            onScreenSelection(Screens.Settings)
                        }
                    )
                }
            }
        }
    }
}