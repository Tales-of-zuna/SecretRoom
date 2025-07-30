package mn.univision.secretroom.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import mn.univision.secretroom.presentation.screens.Screens

val TopBarTabs = Screens.entries.toList().filter { it.isTabItem }
val TopbarFocusRequesters = List(TopBarTabs.size + 2) { FocusRequester() }

@Composable
fun Navbar(
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    screens: List<Screens> = TopBarTabs,
    focusRequesters: List<FocusRequester> = remember { TopbarFocusRequesters },
    onScreenSelection: (screen: Screens) -> Unit
) {

    val focusManager = LocalFocusManager.current
    focusManager
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(MaterialTheme.colorScheme.surface)
                .focusRestorer(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Text("Secret Room")
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isTabRowFocused by remember { mutableStateOf(false) }

                Spacer(modifier = Modifier.width(10.dp))
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

                                Text(
                                    modifier = Modifier,
                                    text = screen.name,
                                )

                            }
                        }

                    }


                }
            }
        }
    }


}