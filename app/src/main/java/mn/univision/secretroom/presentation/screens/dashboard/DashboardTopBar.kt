package mn.univision.secretroom.presentation.screens.dashboard

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
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import mn.univision.secretroom.R
import mn.univision.secretroom.data.models.ViewItem
import mn.univision.secretroom.data.util.StringConstants
import mn.univision.secretroom.presentation.screens.Screens
import mn.univision.secretroom.presentation.theme.IconSize
import mn.univision.secretroom.presentation.theme.LexendExa
import mn.univision.secretroom.presentation.utils.occupyScreenSize

val TopBarFocusRequesters = List(size = 10) { FocusRequester() }

private const val PROFILE_SCREEN_INDEX = -1
private const val SEARCH_SCREEN_INDEX = -2

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DashboardTopBar(
    dynamicScreens: List<ViewItem>,
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    focusRequesters: List<FocusRequester> = remember { TopBarFocusRequesters },
    onScreenSelection: (screenId: String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .focusRestorer(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SecretRoomLogo(
                modifier = Modifier
                    .alpha(0.75f)
                    .padding(end = 8.dp),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                TabRow(
                    selectedTabIndex = if (selectedTabIndex >= 0 && selectedTabIndex < dynamicScreens.size) selectedTabIndex else -1,
                    separator = { Spacer(modifier = Modifier) }
                ) {
                    dynamicScreens.forEachIndexed { index, screen ->
                        key(index) {
                            val isSelected = index == selectedTabIndex
                            var isFocused by remember { mutableStateOf(false) }

                            val alpha = when {
                                isSelected -> 1f
                                isFocused -> 0.8f
                                else -> 0.6f
                            }

                            Tab(
                                modifier = Modifier
                                    .height(32.dp)
                                    .focusRequester(focusRequesters[index + 2])
                                    .alpha(alpha)
                                    .onFocusChanged { focusState ->
                                        isFocused = focusState.isFocused
                                    },
                                selected = isSelected,
                                onFocus = {
                                    if (!isSelected) {
                                        onScreenSelection(screen._id)
                                    }
                                },
                                onClick = { focusManager.moveFocus(FocusDirection.Down) },
                            ) {
                                Text(
                                    modifier = Modifier
                                        .occupyScreenSize()
                                        .padding(horizontal = 16.dp),
                                    text = screen.title?.mn ?: "",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = LocalContentColor.current
                                    )
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                SearchIcon(
                    modifier = Modifier
                        .size(16.dp)
                        .semantics {
                            contentDescription = StringConstants.Composable
                                .ContentDescription.DashboardSearchButton
                        },
                    selected = selectedTabIndex == SEARCH_SCREEN_INDEX,
                    onClick = {
                        onScreenSelection(Screens.Search())
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                UserAvatar(
                    modifier = Modifier
                        .size(16.dp)
                        .semantics {
                            contentDescription =
                                StringConstants.Composable.ContentDescription.UserAvatar
                        },
                    selected = selectedTabIndex == PROFILE_SCREEN_INDEX,
                    onClick = {
                        onScreenSelection(Screens.Profile())
                    }
                )
            }
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
        AsyncImage(
            model = R.drawable.logo,
            contentDescription = "logo",
            modifier = Modifier
                .size(IconSize)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(R.string.brand_logo_text),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            fontFamily = LexendExa
        )
    }
}