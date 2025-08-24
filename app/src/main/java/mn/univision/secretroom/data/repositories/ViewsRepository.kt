package mn.univision.secretroom.data.repositories

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import mn.univision.secretroom.data.models.ViewItem
import mn.univision.secretroom.data.remote.AuthApiService
import mn.univision.secretroom.data.storage.DataStoreManager
import mn.univision.secretroom.data.storage.ViewsDataManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewsRepository @Inject constructor(
    private val api: AuthApiService,
    private val dataStore: DataStoreManager,
    private val viewsDataManager: ViewsDataManager // Add this
) {
    companion object {
        private const val TAG = "ViewsRepository"
        private const val VIEWS_API_URL = "https://looktv.mn/appmgr/views.json"
    }

    sealed class ViewsResult {
        data class Success(val views: List<ViewItem>) : ViewsResult()
        data class Error(val message: String) : ViewsResult()
        object Loading : ViewsResult()
    }

    suspend fun fetchViews(): ViewsResult {
        return try {
            // Try to load from cache first
            val cachedViews = viewsDataManager.loadCachedViews()
            if (cachedViews != null && cachedViews.isNotEmpty()) {
                Log.d(TAG, "Loaded views from cache: ${cachedViews.size} items")
                return ViewsResult.Success(cachedViews)
            }

            val cookie = dataStore.cookieFlow.first()
            if (cookie.isNullOrEmpty()) {
                return ViewsResult.Error("Authentication required. No cookie found.")
            }

            Log.d(TAG, "Fetching views from: $VIEWS_API_URL")

            val response = api.getViews(
                url = VIEWS_API_URL,
                cookie = cookie
            )

            if (response.isSuccessful) {
                val viewsData = response.body()
                if (viewsData != null) {
                    Log.d(TAG, "Successfully fetched views: ${viewsData.size} items")

                    // Save to cache
                    viewsDataManager.saveViews(viewsData)

                    ViewsResult.Success(viewsData)
                } else {
                    Log.e(TAG, "Views response body is null")
                    ViewsResult.Error("Empty response from views API")
                }
            } else {
                Log.e(TAG, "Failed to fetch views: ${response.code()} - ${response.message()}")
                ViewsResult.Error("Failed to fetch views: ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching views", e)
            ViewsResult.Error(e.message ?: "Unknown error occurred while fetching views")
        }
    }

    fun getViewsFlow(): Flow<ViewsResult> = flow {
        emit(ViewsResult.Loading)
        emit(fetchViews())
    }
}
