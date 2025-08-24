package mn.univision.secretroom.presentation.screens.svod


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import mn.univision.secretroom.data.models.ViewItem
import mn.univision.secretroom.data.storage.ViewsDataManager
import javax.inject.Inject

@HiltViewModel
class DynamicContentViewModel @Inject constructor(
    private val viewsDataManager: ViewsDataManager
) : ViewModel() {

    private val _viewsState = MutableStateFlow<ViewsState>(ViewsState.Loading)
    val viewsState: StateFlow<ViewsState> = _viewsState.asStateFlow()

    init {
        loadViews()
        observeViewsChanges()
    }

    private fun loadViews() {
        viewModelScope.launch {
            val cachedViews = viewsDataManager.loadCachedViews()
            if (cachedViews != null) {
                _viewsState.value = ViewsState.Success(cachedViews)
            } else {
                _viewsState.value = ViewsState.Error("No views available")
            }
        }
    }

    private fun observeViewsChanges() {
        viewsDataManager.viewsFlow
            .filterNotNull()
            .onEach { views ->
                _viewsState.value = ViewsState.Success(views)
            }
            .launchIn(viewModelScope)
    }

    // Example: Get specific type of content
    fun getMovieViews(): List<ViewItem> {
        return viewsDataManager.getViewsByType("movie")
    }

    fun getKidsContent(): List<ViewItem> {
        return viewsDataManager.getKidsViews()
    }
}

sealed class ViewsState {
    object Loading : ViewsState()
    data class Success(val views: List<ViewItem>) : ViewsState()
    data class Error(val message: String) : ViewsState()
}