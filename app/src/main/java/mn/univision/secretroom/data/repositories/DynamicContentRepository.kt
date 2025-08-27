package mn.univision.secretroom.data.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import mn.univision.secretroom.data.entities.DynamicContent
import mn.univision.secretroom.data.entities.toDynamicContent
import mn.univision.secretroom.data.remote.DynamicContentApi
import mn.univision.secretroom.data.storage.DataStoreManager
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicContentRepository @Inject constructor(
    private val api: DynamicContentApi,
    private val dataStore: DataStoreManager
) {
    companion object {
        private const val TAG = "DynamicContentRepository"
    }

    // In-memory cache with TTL
    private val memoryCache = ConcurrentHashMap<String, CacheEntry>()

    data class CacheEntry(
        val data: List<DynamicContent>,
        val timestamp: Long = System.currentTimeMillis()
    ) {
        fun isExpired(): Boolean =
            System.currentTimeMillis() - timestamp > 5 * 60 * 1000L
    }

    sealed class ContentResult {
        data class Success(val content: List<DynamicContent>) : ContentResult()
        data class Error(val message: String) : ContentResult()
        object Loading : ContentResult()
    }

    suspend fun fetchContentFromUri(uri: String): ContentResult =
        withContext(Dispatchers.IO) {
            try {
                // Check memory cache first
                val cached = memoryCache[uri]
                if (cached != null && !cached.isExpired()) {
                    return@withContext ContentResult.Success(cached.data)
                }

                val cookie = dataStore.cookieFlow.first()
                val cleanUri = prepareUri(uri)

                val response = api.getUnifiedList(
                    url = cleanUri,
                    cookie = cookie
                )

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val content = body.response.map { it.toDynamicContent() }
                        memoryCache[uri] = CacheEntry(content)
                        ContentResult.Success(content)
                    } ?: ContentResult.Error("Empty response body")
                } else {
                    ContentResult.Error("Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                memoryCache[uri]?.let {
                    ContentResult.Success(it.data)
                } ?: ContentResult.Error(e.message ?: "Unknown error")
            }
        }

    fun getContentFlow(uri: String): Flow<ContentResult> = flow {
        emit(ContentResult.Loading)
        emit(fetchContentFromUri(uri))
    }.flowOn(Dispatchers.IO)

    private fun prepareUri(uri: String): String {
        return uri.replace("{PROVINCE}", "")
    }

    fun clearCache() {
        memoryCache.clear()
    }
}
