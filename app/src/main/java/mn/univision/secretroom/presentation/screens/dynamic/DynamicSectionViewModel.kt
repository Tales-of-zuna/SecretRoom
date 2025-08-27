package mn.univision.secretroom.presentation.screens.dynamic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.univision.secretroom.data.entities.DynamicContent
import mn.univision.secretroom.data.repositories.DynamicContentRepository
import javax.inject.Inject

@HiltViewModel
class DynamicSectionViewModel @Inject constructor(
    private val repository: DynamicContentRepository
) : ViewModel() {

    private val _contentState = MutableStateFlow<DynamicSectionState>(DynamicSectionState.Initial)
    val contentState: StateFlow<DynamicSectionState> = _contentState.asStateFlow()

    sealed class DynamicSectionState {
        object Initial : DynamicSectionState()
        object Loading : DynamicSectionState()
        data class Success(val content: List<DynamicContent>) : DynamicSectionState()
        data class Error(val message: String) : DynamicSectionState()
    }

    fun loadContent(uri: String?) {
        if (uri.isNullOrEmpty()) {
            _contentState.value = DynamicSectionState.Error("No URI provided")
            return
        }

        viewModelScope.launch {
            repository.getContentFlow(uri).collect { result ->
                _contentState.value = when (result) {
                    is DynamicContentRepository.ContentResult.Loading -> DynamicSectionState.Loading
                    is DynamicContentRepository.ContentResult.Success -> DynamicSectionState.Success(
                        result.content
                    )

                    is DynamicContentRepository.ContentResult.Error -> DynamicSectionState.Error(
                        result.message
                    )
                }
            }
        }
    }
}
