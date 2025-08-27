package mn.univision.secretroom.data.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mn.univision.secretroom.data.entities.DynamicContent
import mn.univision.secretroom.data.repositories.DynamicContentRepository
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class DynamicContentManager @Inject constructor(
    private val repository: DynamicContentRepository
) : ViewModel() {

    // Cache content by URI to prevent redundant API calls
    private val contentCache = ConcurrentHashMap<String, CacheEntry>()
    private val _contentStates = MutableStateFlow<Map<String, DynamicSectionState>>(emptyMap())
    val contentStates: StateFlow<Map<String, DynamicSectionState>> = _contentStates.asStateFlow()

    // Cache validity duration (5 minutes)
    private val CACHE_DURATION_MS = 5 * 60 * 1000L

    data class CacheEntry(
        val content: List<DynamicContent>,
        val timestamp: Long = System.currentTimeMillis()
    ) {
        fun isValid(): Boolean = System.currentTimeMillis() - timestamp < 5 * 60 * 1000L
    }

    sealed class DynamicSectionState {
        object Initial : DynamicSectionState()
        object Loading : DynamicSectionState()
        data class Success(val content: List<DynamicContent>) : DynamicSectionState()
        data class Error(val message: String) : DynamicSectionState()
    }

    fun loadContent(sectionId: String, uri: String?) {
        if (uri.isNullOrEmpty()) {
            updateState(sectionId, DynamicSectionState.Error("No URI provided"))
            return
        }

        // Check cache first
        contentCache[uri]?.let { cached ->
            if (cached.isValid()) {
                updateState(sectionId, DynamicSectionState.Success(cached.content))
                return
            }
        }

        // Prevent duplicate loading
        if (_contentStates.value[sectionId] is DynamicSectionState.Loading) {
            return
        }

        updateState(sectionId, DynamicSectionState.Loading)

        viewModelScope.launch(Dispatchers.IO) {
            repository.getContentFlow(uri).collect { result ->
                val newState = when (result) {
                    is DynamicContentRepository.ContentResult.Loading ->
                        DynamicSectionState.Loading

                    is DynamicContentRepository.ContentResult.Success -> {
                        // Cache the result
                        contentCache[uri] = CacheEntry(result.content)
                        DynamicSectionState.Success(result.content)
                    }

                    is DynamicContentRepository.ContentResult.Error ->
                        DynamicSectionState.Error(result.message)
                }
                updateState(sectionId, newState)
            }
        }
    }

    fun getContentState(sectionId: String): DynamicSectionState {
        return _contentStates.value[sectionId] ?: DynamicSectionState.Initial
    }

    private fun updateState(sectionId: String, state: DynamicSectionState) {
        _contentStates.update { current ->
            current + (sectionId to state)
        }
    }

    fun preloadContent(sections: List<Pair<String, String?>>) {
        sections.forEach { (sectionId, uri) ->
            if (uri != null && contentCache[uri]?.isValid() != true) {
                loadContent(sectionId, uri)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        contentCache.clear()
    }
}