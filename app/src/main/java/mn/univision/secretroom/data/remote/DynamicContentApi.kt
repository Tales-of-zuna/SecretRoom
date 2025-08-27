package mn.univision.secretroom.data.remote

import mn.univision.secretroom.data.models.UnifiedListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Url

interface DynamicContentApi {
    @GET
    suspend fun getUnifiedList(
        @Url url: String,
        @Header("Cookie") cookie: String? = null
    ): Response<UnifiedListResponse>
}
