package mn.univision.secretroom.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/token")
    suspend fun getToken(@Body body: Map<String, String>): TokenResponse

    @GET("household")
    suspend fun getHouseholdId(@Header("Authorization") bearerToken: String): HouseholdResponse
}

data class TokenResponse(
    val accessToken: String,
    val expiresIn: Long
)

data class HouseholdResponse(
    val householdId: String
)
