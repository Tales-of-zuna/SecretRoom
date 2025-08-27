package mn.univision.secretroom.data.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import mn.univision.secretroom.data.entities.DynamicContent
import mn.univision.secretroom.data.entities.toDynamicContent
import mn.univision.secretroom.data.remote.DynamicContentApi
import mn.univision.secretroom.data.storage.DataStoreManager
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

    sealed class ContentResult {
        data class Success(val content: List<DynamicContent>) : ContentResult()
        data class Error(val message: String) : ContentResult()
        object Loading : ContentResult()
    }

    suspend fun fetchContentFromUri(uri: String): ContentResult {
        return try {
            val cookie = dataStore.cookieFlow.first()

            val cleanUri = prepareUri(uri)

            val response = api.getUnifiedList(
                url = cleanUri,
                cookie = cookie
            )

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val content = body.response.map { it.toDynamicContent() }
                    ContentResult.Success(content)
                } else {
                    ContentResult.Error("Empty response body")
                }
            } else {
                ContentResult.Error("Failed to fetch content: ${response.message()}")
            }
        } catch (e: Exception) {
            ContentResult.Error(e.message ?: "Unknown error occurred")
        }
    }

    fun getContentFlow(uri: String): Flow<ContentResult> = flow {
        emit(ContentResult.Loading)
        emit(fetchContentFromUri(uri))
    }

    private fun prepareUri(uri: String): String {
        // Replace {PROVINCE} with appropriate value
        // You might want to get this from user preferences
        return uri.replace("{PROVINCE}", "")
            .replace("client=json", "client=json")
    }
}
