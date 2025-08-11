package mn.univision.secretroom.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import mn.univision.secretroom.R
import mn.univision.secretroom.data.util.StringConstants
import mn.univision.secretroom.presentation.screens.Screens
import mn.univision.secretroom.presentation.theme.IconSize
import mn.univision.secretroom.presentation.theme.LexendExa
import mn.univision.secretroom.presentation.theme.SecretRoomCardShape
import mn.univision.secretroom.presentation.utils.occupyScreenSize

val TopBarTabs = Screens.entries.toList().filter { it.isTabItem }

// +1 for ProfileTab
val TopBarFocusRequesters = List(size = TopBarTabs.size + 1) { FocusRequester() }

private const val PROFILE_SCREEN_INDEX = -1

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DashboardTopBar(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    screens: List<Screens> = TopBarTabs,
    focusRequesters: List<FocusRequester> = remember { TopBarFocusRequesters },
    onScreenSelection: (screen: Screens) -> Unit
) {
    val focusManager = LocalFocusManager.current
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .background(MaterialTheme.colorScheme.surface)
                .focusRestorer(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecretRoomLogo(
                modifier = Modifier
                    .alpha(0.75f)
                    .padding(end = 8.dp),
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                var isTabRowFocused by remember { mutableStateOf(false) }

                Spacer(modifier = Modifier.width(20.dp))
                TabRow(
                    modifier = Modifier
                        .onFocusChanged {
                            isTabRowFocused = it.isFocused || it.hasFocus
                        },
                    selectedTabIndex = selectedTabIndex,
                    indicator = { tabPositions, _ ->
                        if (selectedTabIndex >= 0) {
                            DashboardTopBarItemIndicator(
                                currentTabPosition = tabPositions[selectedTabIndex],
                                anyTabFocused = isTabRowFocused,
                                shape = SecretRoomCardShape
                            )
                        }
                    },
                    separator = { Spacer(modifier = Modifier) }
                ) {
                    screens.forEachIndexed { index, screen ->
                        key(index) {
                            Tab(
                                modifier = Modifier
                                    .height(32.dp)
                                    .focusRequester(focusRequesters[index + 1]),
                                selected = index == selectedTabIndex,
                                onFocus = { onScreenSelection(screen) },
                                onClick = { focusManager.moveFocus(FocusDirection.Down) },
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
                                        text = screen.title ?: stringResource(R.string.app_name),
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            color = LocalContentColor.current
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            UserAvatar(
                modifier = Modifier
                    .size(32.dp)
                    .focusRequester(focusRequesters[0])
                    .semantics {
                        contentDescription =
                            StringConstants.Composable.ContentDescription.UserAvatar
                    },
                selected = selectedTabIndex == PROFILE_SCREEN_INDEX,
                onClick = {
                    onScreenSelection(Screens.Profile)
                }
            )
        }
    }
}

@Composable
private fun SecretRoomLogo(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.PlayCircle,
            contentDescription = StringConstants.Composable
                .ContentDescription.BrandLogoImage,
            modifier = Modifier
                .padding(end = 4.dp)
                .size(IconSize)
        )
        Text(
            text = stringResource(R.string.brand_logo_text),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            fontFamily = LexendExa
        )
    }
}
