package mn.univision.secretroom.data.storage

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mn.univision.secretroom.data.entities.DynamicContent
import mn.univision.secretroom.data.repositories.DynamicContentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicContentManager @Inject constructor(
    private val repository: DynamicContentRepository
) {
    private val _contentStates = MutableStateFlow<Map<String, ContentState>>(emptyMap())
    val contentStates: StateFlow<Map<String, ContentState>> = _contentStates.asStateFlow()

    sealed class ContentState {
        object Initial : ContentState()
        object Loading : ContentState()
        data class Success(val content: List<DynamicContent>) : ContentState()
        data class Error(val message: String, val canRetry: Boolean = true) : ContentState()
    }

    fun loadContent(sectionId: String, uri: String?) {
        if (uri.isNullOrEmpty()) {
            updateState(sectionId, ContentState.Error("No URI provided", false))
            return
        }

        val currentState = _contentStates.value[sectionId]
        if (currentState is ContentState.Loading) return // Prevent duplicate loading

        updateState(sectionId, ContentState.Loading)

        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
            repository.getContentFlow(uri).collect { result ->
                val newState = when (result) {
                    is DynamicContentRepository.ContentResult.Loading -> ContentState.Loading
                    is DynamicContentRepository.ContentResult.Success -> ContentState.Success(result.content)
                    is DynamicContentRepository.ContentResult.Error -> ContentState.Error(result.message)
                }
                updateState(sectionId, newState)
            }
        }
    }

    fun retryContent(sectionId: String, uri: String?) {
        loadContent(sectionId, uri)
    }

    fun getContentState(sectionId: String): ContentState {
        return _contentStates.value[sectionId] ?: ContentState.Initial
    }

    private fun updateState(sectionId: String, state: ContentState) {
        _contentStates.update { current ->
            current + (sectionId to state)
        }
    }

    fun preloadContent(sections: List<Pair<String, String?>>) {
        sections.forEach { (sectionId, uri) ->
            if (uri != null && getContentState(sectionId) is ContentState.Initial) {
                loadContent(sectionId, uri)
            }
        }
    }

    fun clearSection(sectionId: String) {
        _contentStates.update { current ->
            current - sectionId
        }
    }

    fun getCacheStats() = repository.getCacheStats()
}
