

package mn.univision.secretroom.presentation.screens.videoPlayer.components

import androidx.annotation.IntRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce

@androidx.annotation.OptIn(UnstableApi::class)
class VideoPlayerState(
    @IntRange(from = 0)
    private val hideSeconds: Int,
) {
    var isControlsVisible by mutableStateOf(true)
        private set

    fun showControls(isPlaying: Boolean = true) {
        if (isPlaying) {
            updateControlVisibility()
        } else {
            updateControlVisibility(seconds = Int.MAX_VALUE)
        }
    }

    private fun updateControlVisibility(seconds: Int = hideSeconds) {
        isControlsVisible = true
        channel.trySend(seconds)
    }

    private val channel = Channel<Int>(CONFLATED)

    @OptIn(FlowPreview::class)
    suspend fun observe() {
        channel.consumeAsFlow()
            .debounce { it.toLong() * 1000 }
            .collect { isControlsVisible = false }
    }
}

/**
 * Create and remember a [VideoPlayerState] instance. Useful when trying to control the state of
 * the [VideoPlayerOverlay]-related composable.
 * @return A remembered instance of [VideoPlayerState].
 * @param hideSeconds How many seconds should the controls be visible before being hidden.
 * */
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberVideoPlayerState(
    @IntRange(from = 0) hideSeconds: Int = 2
): VideoPlayerState {
    return remember {
        VideoPlayerState(
            hideSeconds = hideSeconds,
        )
    }
        .also { LaunchedEffect(it) { it.observe() } }
}
