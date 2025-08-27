package mn.univision.secretroom.data.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
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
        private const val CACHE_SIZE = 50
        private const val CACHE_TTL_MS = 5 * 60 * 1000L
    }

    // LRU Cache instead of ConcurrentHashMap
    private val memoryCache = object : LinkedHashMap<String, CacheEntry>(CACHE_SIZE, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, CacheEntry>?): Boolean {
            return size > CACHE_SIZE
        }
    }

    // Prevent duplicate requests for same URI
    private val ongoingRequests = ConcurrentHashMap<String, Deferred<ContentResult>>()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    data class CacheEntry(
        val data: List<DynamicContent>,
        val timestamp: Long = System.currentTimeMillis()
    ) {
        fun isExpired(): Boolean = System.currentTimeMillis() - timestamp > CACHE_TTL_MS
    }

    sealed class ContentResult {
        data class Success(val content: List<DynamicContent>) : ContentResult()
        data class Error(val message: String) : ContentResult()
        object Loading : ContentResult()
    }

    suspend fun fetchContentFromUri(uri: String): ContentResult = withContext(Dispatchers.IO) {
        // Check memory cache first
        synchronized(memoryCache) {
            val cached = memoryCache[uri]
            if (cached != null && !cached.isExpired()) {
                return@withContext ContentResult.Success(cached.data)
            }
        }

        // Check if request is already ongoing
        ongoingRequests[uri]?.let { return@withContext it.await() }

        // Start new request
        val deferred = scope.async {
            try {
                val cookie = dataStore.cookieFlow.first()
                val cleanUri = prepareUri(uri)

                val response = api.getUnifiedList(url = cleanUri, cookie = cookie)

                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val content = body.response.map { it.toDynamicContent() }
                        synchronized(memoryCache) {
                            memoryCache[uri] = CacheEntry(content)
                        }
                        ContentResult.Success(content)
                    } ?: ContentResult.Error("Empty response body")
                } else {
                    ContentResult.Error("HTTP ${response.code()}: ${response.message()}")
                }
            } catch (e: Exception) {
                // Return stale cache if available
                synchronized(memoryCache) {
                    memoryCache[uri]?.let { ContentResult.Success(it.data) }
                } ?: ContentResult.Error(e.message ?: "Network error")
            } finally {
                ongoingRequests.remove(uri)
            }
        }

        ongoingRequests[uri] = deferred
        deferred.await()
    }

    fun getContentFlow(uri: String): Flow<ContentResult> = flow {
        emit(ContentResult.Loading)
        emit(fetchContentFromUri(uri))
    }.flowOn(Dispatchers.IO)

    private fun prepareUri(uri: String): String = uri.replace("{PROVINCE}", "")

    fun clearCache() {
        synchronized(memoryCache) {
            memoryCache.clear()
        }
        ongoingRequests.clear()
    }

    fun getCacheStats(): Pair<Int, Int> = synchronized(memoryCache) {
        Pair(memoryCache.size, CACHE_SIZE)
    }
}