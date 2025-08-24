package mn.univision.secretroom.data.storage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import mn.univision.secretroom.data.models.ViewItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ViewsDataManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStore: DataStoreManager
) {
    private val gson = Gson()
    private val _viewsFlow = MutableStateFlow<List<ViewItem>?>(null)
    val viewsFlow = _viewsFlow.asStateFlow()

    // In-memory cache
    private var cachedViews: List<ViewItem>? = null

    companion object {
        private const val VIEWS_CACHE_FILE = "views_cache.json"
        private const val VIEWS_PREFS_KEY = "cached_views_json"
    }

    /**
     * Save views to both memory and persistent storage
     */
    suspend fun saveViews(views: List<ViewItem>) {
        // Save to memory
        cachedViews = views
        _viewsFlow.value = views

        // Save to SharedPreferences (for small data)
        val json = gson.toJson(views)
        saveToPreferences(json)

        // Alternative: Save to internal storage (for larger data)
        saveToInternalStorage(json)
    }

    /**
     * Load views from cache (memory first, then disk)
     */
    suspend fun loadCachedViews(): List<ViewItem>? {
        // Check memory cache first
        cachedViews?.let { return it }

        // Try loading from preferences
        val prefsViews = loadFromPreferences()
        if (prefsViews != null) {
            cachedViews = prefsViews
            _viewsFlow.value = prefsViews
            return prefsViews
        }

        // Try loading from internal storage
        val storageViews = loadFromInternalStorage()
        if (storageViews != null) {
            cachedViews = storageViews
            _viewsFlow.value = storageViews
            return storageViews
        }

        return null
    }

    /**
     * Get specific view by ID
     */
    fun getViewById(viewId: String): ViewItem? {
        return cachedViews?.find { it._id == viewId }
    }

    /**
     * Get views filtered by type
     */
    fun getViewsByType(type: String): List<ViewItem> {
        return cachedViews?.filter { view ->
            view.items?.any { it.type == type } == true
        } ?: emptyList()
    }

    /**
     * Get kids views
     */
    fun getKidsViews(): List<ViewItem> {
        return cachedViews?.filter { it.kids == true } ?: emptyList()
    }

    /**
     * Clear all cached views
     */
    suspend fun clearCache() {
        cachedViews = null
        _viewsFlow.value = null
        clearPreferences()
        clearInternalStorage()
    }

    // Private helper methods

    private fun saveToPreferences(json: String) {
        context.getSharedPreferences("views_cache", Context.MODE_PRIVATE)
            .edit()
            .putString(VIEWS_PREFS_KEY, json)
            .apply()
    }

    private fun loadFromPreferences(): List<ViewItem>? {
        return try {
            val prefs = context.getSharedPreferences("views_cache", Context.MODE_PRIVATE)
            val json = prefs.getString(VIEWS_PREFS_KEY, null) ?: return null

            val type = object : TypeToken<List<ViewItem>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            null
        }
    }

    private fun saveToInternalStorage(json: String) {
        try {
            context.openFileOutput(VIEWS_CACHE_FILE, Context.MODE_PRIVATE).use {
                it.write(json.toByteArray())
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun loadFromInternalStorage(): List<ViewItem>? {
        return try {
            context.openFileInput(VIEWS_CACHE_FILE).use { stream ->
                val json = stream.bufferedReader().readText()
                val type = object : TypeToken<List<ViewItem>>() {}.type
                gson.fromJson(json, type)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun clearPreferences() {
        context.getSharedPreferences("views_cache", Context.MODE_PRIVATE)
            .edit()
            .remove(VIEWS_PREFS_KEY)
            .apply()
    }

    private fun clearInternalStorage() {
        try {
            context.deleteFile(VIEWS_CACHE_FILE)
        } catch (e: Exception) {
            // Handle error
        }
    }
}
