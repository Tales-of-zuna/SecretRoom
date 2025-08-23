package mn.univision.secretroom.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mn.univision.secretroom.data.models.ViewItem
import mn.univision.secretroom.data.repositories.ViewsRepository
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val viewsRepository: ViewsRepository
) : ViewModel() {

    private val _viewsState = MutableStateFlow<ViewsState>(ViewsState.Initial)
    val viewsState: StateFlow<ViewsState> = _viewsState.asStateFlow()

    sealed class ViewsState {
        object Initial : ViewsState()
        object Loading : ViewsState()
        data class Success(val views: List<ViewItem>) : ViewsState()
        data class Error(val message: String) : ViewsState()
    }

    fun loadViews() {
        viewModelScope.launch {
            _viewsState.value = ViewsState.Loading

            when (val result = viewsRepository.fetchViews()) {
                is ViewsRepository.ViewsResult.Success -> {
                    _viewsState.value = ViewsState.Success(result.views)
                }

                is ViewsRepository.ViewsResult.Error -> {
                    _viewsState.value = ViewsState.Error(result.message)
                }

                is ViewsRepository.ViewsResult.Loading -> {
                    // Already set to loading
                }
            }
        }
    }

    fun retryLoadViews() {
        loadViews()
    }
}
